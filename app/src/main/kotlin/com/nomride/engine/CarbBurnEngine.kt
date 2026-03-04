package com.nomride.engine

import com.nomride.karoo.consumerFlow
import com.nomride.karoo.streamDataFlow
import com.nomride.util.Preferences
import io.hammerhead.karooext.KarooSystemService
import io.hammerhead.karooext.models.DataType
import io.hammerhead.karooext.models.RideState
import io.hammerhead.karooext.models.StreamState
import io.hammerhead.karooext.models.SystemNotification
import io.hammerhead.karooext.models.UserProfile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.launch
import timber.log.Timber

class CarbBurnEngine(
    private val karooSystem: KarooSystemService,
    private val tracker: CarbBalanceTracker,
    private val preferences: Preferences,
) {
    private var prevCalories = -1.0
    private var job: Job? = null
    var hasFtp: Boolean = false
        private set

    fun start(scope: CoroutineScope) {
        job = scope.launch {
            val userProfile = karooSystem.consumerFlow<UserProfile>().first()
            val ftp = if (preferences.ftpOverride > 0) preferences.ftpOverride else userProfile.ftp
            hasFtp = ftp > 0
            Timber.d("User FTP: %d (override: %d), Weight: %.1f", userProfile.ftp, preferences.ftpOverride, userProfile.weight)

            if (!hasFtp) {
                karooSystem.dispatch(
                    SystemNotification(
                        "nomride-no-ftp",
                        "Set your FTP for accurate tracking. Open NomRide settings or Karoo profile.",
                        action = "Open Settings",
                        actionIntent = "com.nomride.SETTINGS",
                    ),
                )
            }

            val caloriesFlow = karooSystem.streamDataFlow(DataType.Type.CALORIES)
                .mapNotNull { (it as? StreamState.Streaming)?.dataPoint?.singleValue }

            val ftpPercentFlow: Flow<Double> = if (hasFtp) {
                karooSystem.streamDataFlow(DataType.Type.PERCENT_MAX_FTP)
                    .mapNotNull { (it as? StreamState.Streaming)?.dataPoint?.singleValue }
            } else {
                // No FTP — use fixed 55% estimate
                flow { emit(65.0) }
            }

            val rideStateFlow = karooSystem.consumerFlow<RideState>()

            combine(caloriesFlow, ftpPercentFlow, rideStateFlow) { totalCalories, ftpPercent, rideState ->
                Triple(totalCalories, ftpPercent, rideState)
            }.collect { (totalCalories, ftpPercent, rideState) ->
                when (rideState) {
                    is RideState.Recording -> {
                        if (prevCalories < 0) {
                            // First tick — initialize, don't compute delta
                            prevCalories = totalCalories
                            return@collect
                        }
                        val deltaKcal = totalCalories - prevCalories
                        prevCalories = totalCalories
                        if (deltaKcal > 0 && deltaKcal < 50) {
                            val carbFraction = if (hasFtp) {
                                IntensityZone.getCarbFraction(ftpPercent)
                            } else {
                                0.55 // Default estimate without FTP
                            }
                            val deltaCarbsGrams = deltaKcal * carbFraction / 4.0
                            tracker.addBurned(deltaCarbsGrams)
                        }
                    }
                    is RideState.Idle -> {
                        prevCalories = -1.0
                    }
                    else -> {
                        // Paused — update prevCalories so we don't get a spike on resume
                        prevCalories = totalCalories
                    }
                }
            }
        }
    }

    fun stop() {
        job?.cancel()
        job = null
    }
}
