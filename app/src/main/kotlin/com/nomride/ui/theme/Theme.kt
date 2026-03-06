package com.nomride.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// =============================================================================
// COLOR SYSTEM — Vibrant Orange accent, optimized for Karoo outdoor visibility
// =============================================================================

data class NomRideColors(
    // Primary accent — Vibrant Orange
    val accent: Color,
    val accentLight: Color,
    val onAccent: Color,

    // Surfaces
    val background: Color,
    val surface: Color,
    val surfaceVariant: Color,
    val surfaceElevated: Color,

    // Text
    val onBackground: Color,
    val onSurface: Color,
    val onSurfaceVariant: Color,

    // Semantic — Food/Carbs (green)
    val food: Color,
    val foodDark: Color,

    // Semantic — Hydration (blue)
    val water: Color,
    val waterDark: Color,

    // Semantic — Warning/Alert
    val warning: Color,

    // Semantic — Error/Danger
    val error: Color,
    val errorLight: Color,

    // Balance thresholds — 5-level gradient
    val balancePositive: Color,
    val balanceMild: Color,
    val balanceModerate: Color,
    val balanceSevere: Color,
    val balanceCritical: Color,
)

val NomRideDefaultColors = NomRideColors(
    // Vibrant Orange accent
    accent = Color(0xFFEA580C),
    accentLight = Color(0xFFF97316),
    onAccent = Color(0xFFFFFFFF),

    // Pure black surfaces for OLED + outdoor readability
    background = Color(0xFF000000),
    surface = Color(0xFF111111),
    surfaceVariant = Color(0xFF1C1C1E),
    surfaceElevated = Color(0xFF2C2C2E),

    // Text hierarchy — high contrast for outdoor
    onBackground = Color(0xFFF4F4F5),
    onSurface = Color(0xFFF4F4F5),
    onSurfaceVariant = Color(0xFF71717A),

    // Food/Carbs — rich green
    food = Color(0xFF22C55E),
    foodDark = Color(0xFF16A34A),

    // Hydration — deep blue
    water = Color(0xFF3B82F6),
    waterDark = Color(0xFF2563EB),

    // Warning — amber gold
    warning = Color(0xFFD97706),

    // Error — true red
    error = Color(0xFFEF4444),
    errorLight = Color(0xFFF87171),

    // Balance thresholds — green→yellow→orange→red
    balancePositive = Color(0xFF22C55E),
    balanceMild = Color(0xFF16A34A),
    balanceModerate = Color(0xFFEAB308),
    balanceSevere = Color(0xFFF97316),
    balanceCritical = Color(0xFFEF4444),
)

val LocalNomRideColors = staticCompositionLocalOf { NomRideDefaultColors }

// Composable color accessors
val Accent: Color @Composable get() = LocalNomRideColors.current.accent
val AccentLight: Color @Composable get() = LocalNomRideColors.current.accentLight
val OnAccent: Color @Composable get() = LocalNomRideColors.current.onAccent
val Background: Color @Composable get() = LocalNomRideColors.current.background
val Surface: Color @Composable get() = LocalNomRideColors.current.surface
val SurfaceVariant: Color @Composable get() = LocalNomRideColors.current.surfaceVariant
val SurfaceElevated: Color @Composable get() = LocalNomRideColors.current.surfaceElevated
val OnBackground: Color @Composable get() = LocalNomRideColors.current.onBackground
val OnSurface: Color @Composable get() = LocalNomRideColors.current.onSurface
val OnSurfaceVariant: Color @Composable get() = LocalNomRideColors.current.onSurfaceVariant
val Food: Color @Composable get() = LocalNomRideColors.current.food
val FoodDark: Color @Composable get() = LocalNomRideColors.current.foodDark
val Water: Color @Composable get() = LocalNomRideColors.current.water
val WaterDark: Color @Composable get() = LocalNomRideColors.current.waterDark
val Warning: Color @Composable get() = LocalNomRideColors.current.warning
val Error: Color @Composable get() = LocalNomRideColors.current.error

// =============================================================================
// TYPOGRAPHY
// =============================================================================

val NomRideTypography = Typography(
    headlineLarge = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        lineHeight = 40.sp,
    ),
    headlineMedium = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        lineHeight = 32.sp,
    ),
    headlineSmall = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        lineHeight = 28.sp,
    ),
    titleLarge = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp,
        lineHeight = 24.sp,
    ),
    titleMedium = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.15.sp,
    ),
    bodyLarge = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
    ),
    bodyMedium = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
    ),
    bodySmall = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 11.sp,
        lineHeight = 14.sp,
    ),
    labelLarge = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
    ),
    labelMedium = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 14.sp,
        letterSpacing = 0.5.sp,
    ),
    labelSmall = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 9.sp,
        lineHeight = 12.sp,
        letterSpacing = 0.5.sp,
    ),
)

// =============================================================================
// GARMIN BORDER MODIFIER
// =============================================================================

fun Modifier.garminBorder(color: Color): Modifier = this.drawBehind {
    val strokeWidth = 1.dp.toPx()
    drawLine(
        color = color,
        start = Offset(0f, size.height),
        end = Offset(size.width, size.height),
        strokeWidth = strokeWidth,
    )
}

// =============================================================================
// THEME COMPOSABLE
// =============================================================================

@Composable
fun NomRideTheme(content: @Composable () -> Unit) {
    val colors = NomRideDefaultColors

    val colorScheme = darkColorScheme(
        primary = colors.accent,
        onPrimary = colors.onAccent,
        primaryContainer = colors.accentLight,
        secondary = colors.food,
        onSecondary = Color.Black,
        tertiary = colors.water,
        background = colors.background,
        onBackground = colors.onBackground,
        surface = colors.surface,
        onSurface = colors.onSurface,
        surfaceVariant = colors.surfaceVariant,
        onSurfaceVariant = colors.onSurfaceVariant,
        error = colors.error,
        onError = Color.White,
    )

    CompositionLocalProvider(LocalNomRideColors provides colors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = NomRideTypography,
            content = content,
        )
    }
}
