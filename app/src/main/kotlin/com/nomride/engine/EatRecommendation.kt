package com.nomride.engine

import com.nomride.model.RideNutritionState

object EatRecommendation {

    fun minutesUntilNextEat(
        state: RideNutritionState,
        currentFtpPercent: Double,
        eatIntervalMinutes: Int = 20,
    ): Int {
        if (state.lastIntakeTimestampMs == 0L) return 0

        val now = System.currentTimeMillis()
        val minutesSinceLastEat = (now - state.lastIntakeTimestampMs) / 60_000.0

        val adjustedInterval = when {
            currentFtpPercent > 90 -> (eatIntervalMinutes * 0.7).toInt()
            currentFtpPercent > 75 -> (eatIntervalMinutes * 0.85).toInt()
            else -> eatIntervalMinutes
        }

        val remaining = adjustedInterval - minutesSinceLastEat.toInt()
        return remaining.coerceAtLeast(0)
    }

    fun recommendedCarbsGrams(currentFtpPercent: Double): Int {
        return IntensityZone.getRecommendedIntakeGph(currentFtpPercent).toInt() / 3
    }
}
