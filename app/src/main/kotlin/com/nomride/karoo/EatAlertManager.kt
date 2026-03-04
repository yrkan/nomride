package com.nomride.karoo

import com.nomride.R
import com.nomride.engine.CarbBalanceTracker
import com.nomride.util.Preferences
import io.hammerhead.karooext.KarooSystemService
import io.hammerhead.karooext.models.InRideAlert
import io.hammerhead.karooext.models.PlayBeepPattern
import io.hammerhead.karooext.models.RideState
import io.hammerhead.karooext.models.SystemNotification
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber

class EatAlertManager(
    private val karooSystem: KarooSystemService,
    private val tracker: CarbBalanceTracker,
    private val preferences: Preferences,
) {
    private var job: Job? = null
    private var eatTimerJob: Job? = null
    private var drinkTimerJob: Job? = null
    private var recordingStartTimeMs = 0L

    fun start(scope: CoroutineScope) {
        job = scope.launch {
            karooSystem.consumerFlow<RideState>().collect { rideState ->
                when (rideState) {
                    is RideState.Recording -> {
                        if (recordingStartTimeMs == 0L) {
                            recordingStartTimeMs = System.currentTimeMillis()
                        }
                        startTimers(scope)
                    }
                    is RideState.Paused -> {
                        stopTimers()
                    }
                    is RideState.Idle -> {
                        stopTimers()
                        recordingStartTimeMs = 0L
                    }
                }
            }
        }
    }

    private fun startTimers(scope: CoroutineScope) {
        if (eatTimerJob?.isActive == true) return

        eatTimerJob = scope.launch {
            while (true) {
                delay(preferences.eatIntervalMinutes * 60_000L)

                val elapsedMinutes = (System.currentTimeMillis() - recordingStartTimeMs) / 60_000
                if (elapsedMinutes < preferences.reminderStartMinutes) continue

                val state = tracker.stateFlow.value
                val minutesSinceLastEat = if (state.lastIntakeTimestampMs > 0) {
                    (System.currentTimeMillis() - state.lastIntakeTimestampMs) / 60_000
                } else {
                    Long.MAX_VALUE
                }

                if (minutesSinceLastEat < preferences.eatIntervalMinutes / 2) continue

                dispatchEatAlert(state.balance)
            }
        }

        drinkTimerJob = scope.launch {
            while (true) {
                delay(preferences.drinkIntervalMinutes * 60_000L)

                val elapsedMinutes = (System.currentTimeMillis() - recordingStartTimeMs) / 60_000
                if (elapsedMinutes < preferences.reminderStartMinutes) continue

                dispatchDrinkAlert()
            }
        }
    }

    private fun stopTimers() {
        eatTimerJob?.cancel()
        eatTimerJob = null
        drinkTimerJob?.cancel()
        drinkTimerJob = null
    }

    private fun dispatchEatAlert(balance: Double) {
        val defaultTemplate = preferences.getDefaultTemplate()
        Timber.d("Eat alert: balance=%.0f, recommend=%dg", balance, defaultTemplate.carbsGrams)

        karooSystem.dispatch(
            InRideAlert(
                id = "eat-reminder-${System.currentTimeMillis()}",
                icon = R.drawable.ic_food,
                title = "Time to eat!",
                detail = "Recommended: ${defaultTemplate.carbsGrams}g carbs. Balance: ${balance.toInt()}g",
                autoDismissMs = 10_000,
                backgroundColor = R.color.alert_eat,
                textColor = R.color.white,
            ),
        )

        karooSystem.dispatch(
            SystemNotification(
                "eat-notification",
                "Time to eat! Recommended: ${defaultTemplate.carbsGrams}g carbs. Balance: ${balance.toInt()}g",
                action = "Log ${defaultTemplate.name} ${defaultTemplate.carbsGrams}g",
                actionIntent = "com.nomride.QUICK_LOG",
            ),
        )

        if (preferences.soundEnabled) {
            karooSystem.dispatch(
                PlayBeepPattern(
                    listOf(
                        PlayBeepPattern.Tone(frequency = 1000, durationMs = 200),
                        PlayBeepPattern.Tone(frequency = null, durationMs = 100),
                        PlayBeepPattern.Tone(frequency = 1200, durationMs = 200),
                    ),
                ),
            )
        }
    }

    private fun dispatchDrinkAlert() {
        Timber.d("Drink alert")

        karooSystem.dispatch(
            InRideAlert(
                id = "drink-reminder-${System.currentTimeMillis()}",
                icon = R.drawable.ic_water,
                title = "Drink water!",
                detail = "Stay hydrated — take a sip",
                autoDismissMs = 8_000,
                backgroundColor = R.color.alert_drink,
                textColor = R.color.white,
            ),
        )

        if (preferences.soundEnabled) {
            karooSystem.dispatch(
                PlayBeepPattern(
                    listOf(
                        PlayBeepPattern.Tone(frequency = 800, durationMs = 150),
                        PlayBeepPattern.Tone(frequency = null, durationMs = 100),
                        PlayBeepPattern.Tone(frequency = 800, durationMs = 150),
                    ),
                ),
            )
        }
    }

    fun stop() {
        stopTimers()
        job?.cancel()
        job = null
        recordingStartTimeMs = 0L
    }
}
