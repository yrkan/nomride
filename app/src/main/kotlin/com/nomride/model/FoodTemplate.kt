package com.nomride.model

import kotlinx.serialization.Serializable

@Serializable
data class FoodTemplate(
    val name: String,
    val carbsGrams: Int,
    val emoji: String = "",
    val isDefault: Boolean = false,
) {
    val displayName: String get() = if (emoji.isNotEmpty()) "$emoji $name" else name
    val shortName: String get() = emoji.ifEmpty { name }

    companion object {
        val DEFAULTS = listOf(
            FoodTemplate("Energy Gel", 25, emoji = "⚡", isDefault = true),
            FoodTemplate("Energy Bar", 40, emoji = "🍫"),
            FoodTemplate("Banana", 27, emoji = "🍌"),
            FoodTemplate("Energy Chews", 35, emoji = "🍬"),
            FoodTemplate("Stroopwafel", 30, emoji = "🧇"),
            FoodTemplate("Rice Cake", 35, emoji = "🍙"),
            FoodTemplate("Bottle Mix", 45, emoji = "🍶"),
            FoodTemplate("Dates (3 pcs)", 50, emoji = "🌴"),
        )
    }
}
