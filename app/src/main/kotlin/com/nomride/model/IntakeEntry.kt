package com.nomride.model

import kotlinx.serialization.Serializable

@Serializable
data class IntakeEntry(
    val timestampMs: Long,
    val carbsGrams: Double,
    val type: IntakeType,
    val templateName: String? = null,
    val templateEmoji: String? = null,
    val waterMl: Double = 0.0,
) {
    @Serializable
    enum class IntakeType {
        FOOD, WATER
    }
}
