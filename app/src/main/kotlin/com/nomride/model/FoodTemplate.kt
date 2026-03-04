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
            FoodTemplate("Isotonic Drink", 30),
            FoodTemplate("Chew Pack", 45),
            FoodTemplate("Half Bottle Mix", 15),
            FoodTemplate("Rice Cake", 35),
            FoodTemplate("Date x3", 50),
        )
    }
}
