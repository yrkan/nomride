package com.nomride.model

import kotlinx.serialization.Serializable

@Serializable
data class RideNutritionState(
    val totalBurned: Double = 0.0,
    val totalEaten: Double = 0.0,
    val totalWaterMl: Double = 0.0,
    val burnRateGph: Double = 0.0,
    val lastIntakeTimestampMs: Long = 0L,
    val lastIntakeName: String? = null,
    val lastIntakeEmoji: String? = null,
    val intakeLog: List<IntakeEntry> = emptyList(),
) {
    val balance: Double get() = totalEaten - totalBurned
}
