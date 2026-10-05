package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = QuranGold,
    onPrimary = Color.Black,
    secondary = QuranGoldLight,
    onSecondary = Color.Black,
    background = QuranDarkGreen,
    onBackground = Color.White,
    surface = QuranSurfaceDark,
    onSurface = Color.White,
    surfaceVariant = QuranCardHeader,
    onSurfaceVariant = QuranGoldLight
)

private val LightColorScheme = lightColorScheme(
    primary = QuranDarkGreen,
    onPrimary = Color.White,
    secondary = QuranGold,
    onSecondary = Color.Black,
    background = QuranParchment,
    onBackground = QuranTextDark,
    surface = QuranParchment,
    onSurface = QuranTextDark,
    surfaceVariant = QuranCardHeader,
    onSurfaceVariant = Color.White
)

@Composable
fun QuranAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = QuranTypography,
        content = content
    )
}
