package com.nomride.model

import kotlinx.serialization.Serializable

@Serializable
data class FoodTemplate(
    val name: String,
    val carbsGrams: Int,
    val isDefault: Boolean = false,
) {
    companion object {
        val DEFAULTS = listOf(
            FoodTemplate("Energy Gel", 25, isDefault = true),
            FoodTemplate("Energy Bar", 40),
            FoodTemplate("Banana", 27),
            FoodTemplate("Energy Chews", 35),
            FoodTemplate("Stroopwafel", 30),
            FoodTemplate("Rice Cake", 35),
            FoodTemplate("Bottle Mix", 45),
            FoodTemplate("Dates (3 pcs)", 50),
        )
    }
}
