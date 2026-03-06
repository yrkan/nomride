package com.nomride.engine

import com.nomride.model.IntakeEntry
import com.nomride.model.RideNutritionState
import com.nomride.util.Preferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import timber.log.Timber

class CarbBalanceTracker(private val preferences: Preferences) {
    private val _stateFlow = MutableStateFlow(RideNutritionState())
    val stateFlow: StateFlow<RideNutritionState> = _stateFlow.asStateFlow()

    private val burnRateSamples = mutableListOf<Pair<Long, Double>>()

    private var lastSaveTimeMs = 0L
    private val saveIntervalMs = 30_000L
    private val undoWindowMs = 60_000L
    private val debounceMs = 3_000L
    private var lastLogTimeMs = 0L

    private var isRiding = false

    fun init() {
        val saved = preferences.loadNutritionState()
        if (saved != null) {
            _stateFlow.value = saved
            Timber.d("Restored nutrition state: burned=%.1f eaten=%.1f", saved.totalBurned, saved.totalEaten)
        }
    }

    fun onRideStart() {
        if (!isRiding) {
            isRiding = true
            // Only reset if the previous state was fully idle (no data)
            // If there's existing data, it means extension restarted mid-ride
            val current = _stateFlow.value
            if (current.totalBurned == 0.0 && current.totalEaten == 0.0) {
                Timber.d("New ride started, state already clean")
            } else {
                Timber.d("Ride start with existing state — possibly resumed after extension restart")
            }
        }
    }

    fun onRideEnd() {
        if (isRiding) {
            isRiding = false
            forceSave()
            Timber.d("Ride ended, state saved. Burned=%.1f Eaten=%.1f", _stateFlow.value.totalBurned, _stateFlow.value.totalEaten)
        }
    }

    fun onNewRide() {
        Timber.d("New ride — resetting nutrition state")
        reset()
    }

    fun addBurned(grams: Double) {
        if (grams <= 0) return
        val now = System.currentTimeMillis()
        val current = _stateFlow.value

        burnRateSamples.add(now to grams)
        val cutoff = now - 5 * 60 * 1000L
        burnRateSamples.removeAll { it.first < cutoff }

        val burnRate = if (burnRateSamples.size > 1) {
            val windowMs = burnRateSamples.last().first - burnRateSamples.first().first
            if (windowMs > 0) {
                val totalGrams = burnRateSamples.sumOf { it.second }
                totalGrams / (windowMs / 3_600_000.0)
            } else 0.0
        } else 0.0

        _stateFlow.value = current.copy(
            totalBurned = current.totalBurned + grams,
            burnRateGph = burnRate,
        )
        maybeSave(now)
    }

    fun logIntake(entry: IntakeEntry): Boolean {
        val now = System.currentTimeMillis()
        if (now - lastLogTimeMs < debounceMs) {
            Timber.d("Debounced intake log")
            return false
        }
        lastLogTimeMs = now

        val current = _stateFlow.value
        _stateFlow.value = current.copy(
            totalEaten = current.totalEaten + entry.carbsGrams,
            lastIntakeTimestampMs = entry.timestampMs,
            lastIntakeName = entry.templateName,
            lastIntakeEmoji = entry.templateEmoji,
            intakeLog = current.intakeLog + entry,
        )
        maybeSave(now)
        Timber.d("Logged food: %s %.0fg", entry.templateName, entry.carbsGrams)
        return true
    }

    fun logWater(ml: Double): Boolean {
        val now = System.currentTimeMillis()
        if (now - lastLogTimeMs < debounceMs) return false
        lastLogTimeMs = now

        val entry = IntakeEntry(
            timestampMs = now,
            carbsGrams = 0.0,
            type = IntakeEntry.IntakeType.WATER,
            templateName = "${ml.toInt()}ml water",
            waterMl = ml,
        )
        val current = _stateFlow.value
        _stateFlow.value = current.copy(
            totalWaterMl = current.totalWaterMl + ml,
            lastIntakeTimestampMs = now,
            lastIntakeName = entry.templateName,
            lastIntakeEmoji = "💧",
            intakeLog = current.intakeLog + entry,
        )
        maybeSave(now)
        Timber.d("Logged water: %.0fml", ml)
        return true
    }

    fun undoLast(): Boolean {
        val current = _stateFlow.value
        val lastEntry = current.intakeLog.lastOrNull() ?: return false

        val now = System.currentTimeMillis()
        if (now - lastEntry.timestampMs > undoWindowMs) {
            Timber.d("Undo window expired")
            return false
        }

        val newLog = current.intakeLog.dropLast(1)
        val prevEntry = newLog.lastOrNull()

        _stateFlow.value = when (lastEntry.type) {
            IntakeEntry.IntakeType.FOOD -> current.copy(
                totalEaten = (current.totalEaten - lastEntry.carbsGrams).coerceAtLeast(0.0),
                lastIntakeTimestampMs = prevEntry?.timestampMs ?: 0L,
                lastIntakeName = prevEntry?.templateName,
                lastIntakeEmoji = prevEntry?.templateEmoji,
                intakeLog = newLog,
            )
            IntakeEntry.IntakeType.WATER -> current.copy(
                totalWaterMl = (current.totalWaterMl - lastEntry.waterMl).coerceAtLeast(0.0),
                lastIntakeTimestampMs = prevEntry?.timestampMs ?: 0L,
                lastIntakeName = prevEntry?.templateName,
                lastIntakeEmoji = prevEntry?.templateEmoji,
                intakeLog = newLog,
            )
        }
        Timber.d("Undid last entry: %s", lastEntry.templateName)
        return true
    }

    fun reset() {
        _stateFlow.value = RideNutritionState()
        burnRateSamples.clear()
        lastLogTimeMs = 0L
        preferences.clearNutritionState()
    }

    fun forceSave() {
        preferences.saveNutritionState(_stateFlow.value)
    }

    private fun maybeSave(now: Long) {
        if (now - lastSaveTimeMs > saveIntervalMs) {
            preferences.saveNutritionState(_stateFlow.value)
            lastSaveTimeMs = now
        }
    }
}
