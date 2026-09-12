package com.karnama.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = TealPrimaryLight,
    onPrimary = Color.White,
    background = BackgroundLight,
    onBackground = TextPrimaryLight,
    surface = SurfaceLight,
    onSurface = TextPrimaryLight,
    surfaceVariant = BackgroundLight,
    onSurfaceVariant = TextSecondaryLight,
    outline = DividerLight,
    error = DestructiveRed
)

private val DarkColors = darkColorScheme(
    primary = TealPrimaryDark,
    onPrimary = Color(0xFF00201F),
    background = BackgroundDark,
    onBackground = TextPrimaryDark,
    surface = SurfaceDark,
    onSurface = TextPrimaryDark,
    surfaceVariant = BackgroundDark,
    onSurfaceVariant = TextSecondaryDark,
    outline = DividerDark,
    error = DestructiveRed
)

enum class ThemeMode { LIGHT, DARK, SYSTEM }

@Composable
fun KarnamaTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    content: @Composable () -> Unit
) {
    val darkTheme = when (themeMode) {
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
    }
    val colorScheme = if (darkTheme) DarkColors else LightColors

    MaterialTheme(
        colorScheme = colorScheme,
        typography = KarnamaTypography,
        content = content
    )
}
