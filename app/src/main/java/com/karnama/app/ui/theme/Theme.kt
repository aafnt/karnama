package com.karnama.app.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

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

    // ─────────────────────────────────────────────────────────────────
    // آیکون‌های نوار وضعیت/ناوبری سیستم (ساعت، باتری، آنتن، دکمه‌های
    // ناوبری) باید بر اساس تمِ واقعیِ برنامه روشن/تیره شوند، نه بر اساس
    // تمِ سیستم. در غیر این صورت، وقتی کاربر تم برنامه را «روشن» انتخاب
    // کرده ولی گوشی در حالت «تاریک» است، این آیکون‌ها سفید می‌مانند و
    // روی پس‌زمینه‌ی روشن برنامه دیده نمی‌شوند (و برعکس).
    // ─────────────────────────────────────────────────────────────────
    val view = LocalView.current
    if (!view.isInEditMode) {
        val activity = LocalContext.current as? Activity
        SideEffect {
            val window = activity?.window ?: return@SideEffect
            val insetsController = WindowCompat.getInsetsController(window, view)
            // isAppearanceLightStatusBars = true یعنی آیکون‌ها تیره باشند
            // (مناسب پس‌زمینه‌ی روشن)؛ یعنی دقیقاً معکوس darkTheme.
            insetsController.isAppearanceLightStatusBars = !darkTheme
            insetsController.isAppearanceLightNavigationBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = KarnamaTypography,
        content = content
    )
}
