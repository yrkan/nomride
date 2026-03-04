package com.nomride.karoo

import io.hammerhead.karooext.models.ViewConfig

/**
 * Adaptive text sizes for Glance data fields based on ViewConfig.textSize.
 *
 * Uses config.textSize (SDK-recommended font size in sp) as base and applies
 * multipliers based on the semantic role of the text element.
 */
object TextSizeHelper {

    enum class Role {
        /** Main value (e.g. "-47g") - largest text */
        PRIMARY,
        /** Secondary values (e.g. "Burn: 82g") */
        SECONDARY,
        /** Tertiary values (e.g. "12min ago") */
        TERTIARY,
        /** Labels and units (e.g. "ml", "BALANCE") */
        LABEL,
    }

    private val MULTIPLIERS = mapOf(
        Role.PRIMARY to 1.0f,
        Role.SECONDARY to 0.72f,
        Role.TERTIARY to 0.58f,
        Role.LABEL to 0.38f,
    )

    private val MIN_SP = mapOf(
        Role.PRIMARY to 16f,
        Role.SECONDARY to 12f,
        Role.TERTIARY to 10f,
        Role.LABEL to 8f,
    )

    private val MAX_SP = mapOf(
        Role.PRIMARY to 64f,
        Role.SECONDARY to 40f,
        Role.TERTIARY to 32f,
        Role.LABEL to 14f,
    )

    /**
     * Calculate text size in sp for a given role.
     */
    fun calculateSp(config: ViewConfig, role: Role): Float {
        val baseSp = config.textSize.toFloat()
        val multiplier = MULTIPLIERS[role] ?: 1.0f
        val min = MIN_SP[role] ?: 10f
        val max = MAX_SP[role] ?: 64f
        return (baseSp * multiplier).coerceIn(min, max)
    }

    /**
     * Adaptive padding based on view height.
     * Smaller views need less padding to maximize content area.
     */
    fun paddingDp(viewHeight: Int): Int {
        return when {
            viewHeight < 100 -> 2
            viewHeight < 160 -> 4
            viewHeight < 250 -> 6
            else -> 8
        }
    }
}
