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

private val NexusDarkColorScheme = darkColorScheme(
    primary = NexusNeonGreen,
    onPrimary = NexusBlackPrimary,
    primaryContainer = NexusBlackSecondary,
    onPrimaryContainer = NexusPureWhite,
    secondary = NexusGray300,
    onSecondary = NexusBlackPrimary,
    tertiary = NexusIncomeGreen,
    background = NexusBlackPrimary,
    onBackground = NexusPureWhite,
    surface = NexusBlackSecondary,
    onSurface = NexusPureWhite,
    surfaceVariant = NexusBlackCard,
    onSurfaceVariant = NexusGray300,
    outline = NexusBorderSubtle
)

@Composable
fun ValorTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = NexusDarkColorScheme
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = NexusBlackPrimary.toArgb()
            window.navigationBarColor = NexusBlackPrimary.toArgb()
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

