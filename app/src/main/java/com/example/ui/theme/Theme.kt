package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val ValorDarkColorScheme = darkColorScheme(
    primary = IndigoAiAccent,
    onPrimary = TextPrimaryWhite,
    primaryContainer = DarkSurfaceCardElevated,
    onPrimaryContainer = TextPrimaryWhite,
    secondary = CyanAiAccent,
    onSecondary = ObsidianBackground,
    tertiary = EmeraldIncome,
    background = ObsidianBackground,
    onBackground = TextPrimaryWhite,
    surface = DarkSurfaceCard,
    onSurface = TextPrimaryWhite,
    surfaceVariant = DarkSurfaceCardElevated,
    onSurfaceVariant = TextSecondaryMuted,
    outline = DarkBorderLine
)

@Composable
fun ValorTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = ValorDarkColorScheme
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = ObsidianBackground.toArgb()
            window.navigationBarColor = ObsidianBackground.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
