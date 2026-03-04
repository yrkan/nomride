package com.nomride.engine

class HydrationTracker {
    var totalWaterMl: Double = 0.0
        private set

    fun addWater(ml: Double) {
        totalWaterMl += ml
    }

    fun reset() {
        totalWaterMl = 0.0
    }

    fun recommendedMlPerHour(ftpPercent: Double): Int = when {
        ftpPercent > 90 -> 750
        ftpPercent > 75 -> 625
        else -> 500
    }
}
