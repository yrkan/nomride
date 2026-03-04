package com.nomride.engine

object IntensityZone {
    fun getCarbFraction(ftpPercent: Double): Double = when {
        ftpPercent < 55 -> 0.40
        ftpPercent < 75 -> 0.55
        ftpPercent < 90 -> 0.70
        ftpPercent < 105 -> 0.85
        else -> 0.95
    }

    fun getRecommendedIntakeGph(ftpPercent: Double): Double = when {
        ftpPercent < 55 -> 30.0
        ftpPercent < 75 -> 45.0
        ftpPercent < 90 -> 60.0
        ftpPercent < 105 -> 75.0
        else -> 90.0
    }
}
