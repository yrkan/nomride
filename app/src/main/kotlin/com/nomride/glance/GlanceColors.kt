package com.nomride.glance

import androidx.compose.ui.graphics.Color

object GlanceColors {
    val White = Color(0xFFF4F4F5)
    val Background = Color(0xFF000000)
    val Frame = Color(0xFF1C1C1E)
    val Label = Color(0xFFAAAAAA)
    val Divider = Color(0xFF555555)
    val Food = Color(0xFF22C55E)
    val Water = Color(0xFF3B82F6)
    val WaterLight = Color(0xFFBFDBFE)
    val Accent = Color(0xFFEA580C)
    val Warning = Color(0xFFD97706)
    val Error = Color(0xFFEF4444)

    fun balanceBgColor(balance: Double): Color = when {
        balance > 0 -> Color(0xFF22C55E)
        balance > -50 -> Color(0xFF16A34A)
        balance > -100 -> Color(0xFFEAB308)
        balance > -150 -> Color(0xFFF97316)
        else -> Color(0xFFEF4444)
    }

    fun balanceTextColor(balance: Double): Color = when {
        balance > 0 -> Color.Black
        balance > -50 -> Color.White
        balance > -100 -> Color.Black
        else -> Color.White
    }

    fun balanceDimColor(balance: Double): Color =
        balanceTextColor(balance).copy(alpha = 0.7f)

    fun burnRateColor(gph: Double): Color = when {
        gph < 40 -> Food
        gph < 60 -> Warning
        else -> Error
    }

    fun timeSinceColor(minutesAgo: Int): Color = when {
        minutesAgo < 20 -> Food
        minutesAgo < 40 -> Warning
        else -> Error
    }
}
