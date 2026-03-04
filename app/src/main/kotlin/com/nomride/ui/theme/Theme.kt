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
// COLOR SYSTEM - Garmin Edge style, optimized for Karoo outdoor visibility
// =============================================================================

data class NomRideColors(
    val primary: Color,
    val primaryDark: Color,
    val onPrimary: Color,
    val secondary: Color,
    val accent: Color,
    val background: Color,
    val surface: Color,
    val surfaceVariant: Color,
    val surfaceElevated: Color,
    val onBackground: Color,
    val onSurface: Color,
    val onSurfaceVariant: Color,
    val success: Color,
    val warning: Color,
    val error: Color,
    val balanceGreen: Color,
    val balanceYellowGreen: Color,
    val balanceYellow: Color,
    val balanceOrange: Color,
    val balanceRed: Color,
    val hydrationBlue: Color,
)

val NomRideDefaultColors = NomRideColors(
    primary = Color(0xFF4CAF50),
    primaryDark = Color(0xFF388E3C),
    onPrimary = Color(0xFF000000),
    secondary = Color(0xFFFF9800),
    accent = Color(0xFF2196F3),
    background = Color(0xFF000000),       // Pure black
    surface = Color(0xFF111111),
    surfaceVariant = Color(0xFF1A1A1A),
    surfaceElevated = Color(0xFF222222),
    onBackground = Color(0xFFFFFFFF),
    onSurface = Color(0xFFFFFFFF),
    onSurfaceVariant = Color(0xFFAAAAAA),
    success = Color(0xFF4CAF50),
    warning = Color(0xFFFF9800),
    error = Color(0xFFF44336),
    balanceGreen = Color(0xFF4CAF50),
    balanceYellowGreen = Color(0xFF8BC34A),
    balanceYellow = Color(0xFFFFEB3B),
    balanceOrange = Color(0xFFFF9800),
    balanceRed = Color(0xFFF44336),
    hydrationBlue = Color(0xFF1565C0),
)

val LocalNomRideColors = staticCompositionLocalOf { NomRideDefaultColors }

// Composable color accessors
val Primary: Color @Composable get() = LocalNomRideColors.current.primary
val PrimaryDark: Color @Composable get() = LocalNomRideColors.current.primaryDark
val OnPrimary: Color @Composable get() = LocalNomRideColors.current.onPrimary
val Secondary: Color @Composable get() = LocalNomRideColors.current.secondary
val Accent: Color @Composable get() = LocalNomRideColors.current.accent
val Background: Color @Composable get() = LocalNomRideColors.current.background
val Surface: Color @Composable get() = LocalNomRideColors.current.surface
val SurfaceVariant: Color @Composable get() = LocalNomRideColors.current.surfaceVariant
val SurfaceElevated: Color @Composable get() = LocalNomRideColors.current.surfaceElevated
val OnBackground: Color @Composable get() = LocalNomRideColors.current.onBackground
val OnSurface: Color @Composable get() = LocalNomRideColors.current.onSurface
val OnSurfaceVariant: Color @Composable get() = LocalNomRideColors.current.onSurfaceVariant
val Success: Color @Composable get() = LocalNomRideColors.current.success
val Warning: Color @Composable get() = LocalNomRideColors.current.warning
val Error: Color @Composable get() = LocalNomRideColors.current.error
val HydrationBlue: Color @Composable get() = LocalNomRideColors.current.hydrationBlue

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
        primary = colors.primary,
        onPrimary = colors.onPrimary,
        primaryContainer = colors.primaryDark,
        secondary = colors.secondary,
        onSecondary = colors.onPrimary,
        tertiary = colors.accent,
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
