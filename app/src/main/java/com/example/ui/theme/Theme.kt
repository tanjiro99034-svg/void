package com.example.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

private val VoidDarkColorScheme = darkColorScheme(
    primary = VoidNeonGreen,
    onPrimary = VoidBackground,
    primaryContainer = VoidNeonGreenDim,
    onPrimaryContainer = VoidNeonGreen,
    secondary = VoidTextSecondary,
    onSecondary = VoidBackground,
    secondaryContainer = VoidSurfaceElevated,
    onSecondaryContainer = VoidTextPrimary,
    tertiary = VoidWarningAmber,
    onTertiary = VoidBackground,
    background = VoidBackground,
    onBackground = VoidTextPrimary,
    surface = VoidSecondaryBackground,
    onSurface = VoidTextPrimary,
    surfaceVariant = VoidSurfaceElevated,
    onSurfaceVariant = VoidTextSecondary,
    outline = VoidBorderSubtle,
    outlineVariant = VoidNeonGlow,
    error = VoidDangerRed,
    onError = VoidBackground,
    errorContainer = VoidDangerRedDim,
    onErrorContainer = VoidDangerRed,
)

private val VoidShapes = Shapes(
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(22.dp),
)

@Composable
fun VoidTheme(
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = VoidDarkColorScheme,
        typography = VoidTypography,
        shapes = VoidShapes,
        content = content,
    )
}
