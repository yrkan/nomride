package com.nomride.karoo

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import com.nomride.BuildConfig
import com.nomride.R
import com.nomride.engine.CarbBalanceTracker
import com.nomride.engine.CarbBurnEngine
import com.nomride.model.IntakeEntry
import com.nomride.ui.NomRideExtensionHolder
import com.nomride.ui.QuickLogActivity
import com.nomride.util.Preferences
import io.hammerhead.karooext.KarooSystemService
import io.hammerhead.karooext.extension.KarooExtension
import io.hammerhead.karooext.internal.Emitter
import io.hammerhead.karooext.models.DataType
import io.hammerhead.karooext.models.DeveloperField
import io.hammerhead.karooext.models.FieldValue
import io.hammerhead.karooext.models.FitEffect
import io.hammerhead.karooext.models.RideState
import io.hammerhead.karooext.models.StreamState
import io.hammerhead.karooext.models.WriteToRecordMesg
import io.hammerhead.karooext.models.WriteToSessionMesg
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.launch
import timber.log.Timber

class NomRideExtension : KarooExtension("nomride", BuildConfig.VERSION_NAME) {
    companion object {
        private const val CHANNEL_ID = "nomride_foreground"
        private const val NOTIFICATION_ID = 1
    }

    private lateinit var karooSystem: KarooSystemService
    private lateinit var preferences: Preferences
    private lateinit var tracker: CarbBalanceTracker
    private lateinit var burnEngine: CarbBurnEngine
    private lateinit var alertManager: EatAlertManager

    private var serviceJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.IO)
    private var wasRecording = false

    override val types by lazy {
        listOf(
            CarbBalanceDataType(tracker, extension),
            CarbsBurnedDataType(tracker, extension),
            CarbsEatenDataType(tracker, extension),
            BurnRateDataType(tracker, extension),
            QuickLogStatusDataType(tracker, extension),
            HydrationDataType(tracker, extension),
        )
    }

    private val carbsBurnedField by lazy {
        DeveloperField(
            fieldDefinitionNumber = 0,
            fitBaseTypeId = 136,
            fieldName = "carbs_burned",
            units = "g",
        )
    }

    private val carbsEatenField by lazy {
        DeveloperField(
            fieldDefinitionNumber = 1,
            fitBaseTypeId = 136,
            fieldName = "carbs_eaten",
            units = "g",
        )
    }

    private val carbBalanceField by lazy {
        DeveloperField(
            fieldDefinitionNumber = 2,
            fitBaseTypeId = 136,
            fieldName = "carb_balance",
            units = "g",
        )
    }

    private fun startForegroundService() {
        val notificationManager = getSystemService(NotificationManager::class.java)
        if (notificationManager.getNotificationChannel(CHANNEL_ID) == null) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                getString(R.string.notification_channel_name),
                NotificationManager.IMPORTANCE_LOW,
            ).apply {
                description = getString(R.string.notification_channel_description)
            }
            notificationManager.createNotificationChannel(channel)
        }

        val notification = Notification.Builder(this, CHANNEL_ID)
            .setContentTitle(getString(R.string.notification_title))
            .setContentText(getString(R.string.notification_text))
            .setSmallIcon(R.drawable.ic_nomride)
            .setOngoing(true)
            .build()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            startForeground(NOTIFICATION_ID, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE)
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }
    }

    override fun onCreate() {
        super.onCreate()
        startForegroundService()
        karooSystem = KarooSystemService(this)
        preferences = Preferences(applicationContext)
        tracker = CarbBalanceTracker(preferences)
        burnEngine = CarbBurnEngine(karooSystem, tracker, preferences)
        alertManager = EatAlertManager(karooSystem, tracker, preferences)

        NomRideExtensionHolder.tracker = tracker
        NomRideExtensionHolder.preferences = preferences

        serviceJob = scope.launch {
            karooSystem.connect { connected ->
                if (connected) {
                    Timber.d("NomRide connected to Karoo System")
                    tracker.init()
                    burnEngine.start(scope)
                    alertManager.start(scope)
                    observeRideLifecycle(scope)
                }
            }
        }
    }

    private fun observeRideLifecycle(scope: CoroutineScope) {
        scope.launch {
            karooSystem.consumerFlow<RideState>().collect { rideState ->
                when (rideState) {
                    is RideState.Recording -> {
                        if (!wasRecording) {
                            wasRecording = true
                            tracker.onRideStart()
                        }
                    }
                    is RideState.Idle -> {
                        if (wasRecording) {
                            wasRecording = false
                            tracker.onRideEnd()
                            // Reset for next ride
                            tracker.onNewRide()
                        }
                    }
                    is RideState.Paused -> { /* keep wasRecording true */ }
                }
            }
        }
    }

    override fun startFit(emitter: Emitter<FitEffect>) {
        if (!preferences.fitExportEnabled) {
            emitter.setCancellable { }
            return
        }

        val job = scope.launch {
            // Use ELAPSED_TIME stream for 1Hz pacing (same pattern as sample app)
            karooSystem.streamDataFlow(DataType.Type.ELAPSED_TIME)
                .mapNotNull { (it as? StreamState.Streaming)?.dataPoint?.singleValue }
                .combine(karooSystem.consumerFlow<RideState>()) { _, rideState -> rideState }
                .combine(tracker.stateFlow) { rideState, state -> Pair(rideState, state) }
                .collect { (rideState, state) ->
                    val burnedField = FieldValue(carbsBurnedField, state.totalBurned)
                    val eatenField = FieldValue(carbsEatenField, state.totalEaten)
                    val balanceField = FieldValue(carbBalanceField, state.balance)

                    when (rideState) {
                        is RideState.Recording -> {
                            emitter.onNext(WriteToRecordMesg(burnedField))
                            emitter.onNext(WriteToRecordMesg(eatenField))
                            emitter.onNext(WriteToRecordMesg(balanceField))
                        }
                        is RideState.Paused -> {
                            emitter.onNext(WriteToSessionMesg(burnedField))
                            emitter.onNext(WriteToSessionMesg(eatenField))
                            emitter.onNext(WriteToSessionMesg(balanceField))
                        }
                        is RideState.Idle -> { }
                    }
                }
        }
        emitter.setCancellable { job.cancel() }
    }

    override fun onBonusAction(actionId: String) {
        when (actionId) {
            "log-food" -> {
                val intent = Intent(this, QuickLogActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
            "log-water" -> {
                val intent = Intent(this, QuickLogActivity::class.java)
                    .putExtra("mode", "water")
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
            "quick-gel" -> {
                val template = preferences.getDefaultTemplate()
                val entry = IntakeEntry(
                    timestampMs = System.currentTimeMillis(),
                    carbsGrams = template.carbsGrams.toDouble(),
                    type = IntakeEntry.IntakeType.FOOD,
                    templateName = template.name,
                    templateEmoji = template.emoji.ifEmpty { null },
                )
                tracker.logIntake(entry)
            }
            "undo-last" -> {
                tracker.undoLast()
            }
            else -> Timber.w("Unknown action: %s", actionId)
        }
    }

    override fun onDestroy() {
        tracker.forceSave()
        burnEngine.stop()
        alertManager.stop()
        serviceJob?.cancel()
        serviceJob = null
        karooSystem.disconnect()
        NomRideExtensionHolder.tracker = null
        NomRideExtensionHolder.preferences = null
        super.onDestroy()
    }
}
