package com.example.squadup.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val SquadLightColorScheme = lightColorScheme(
    primary = SquadOrange,
    onPrimary = Color.White,
    secondary = SquadOrangeDark,
    onSecondary = Color.White,
    background = SquadBackground,
    onBackground = SquadTextPrimary,
    surface = SquadSurface,
    onSurface = SquadTextPrimary,
    error = SquadDanger,
    onError = Color.White,
    outline = SquadBorder
)

private val SquadDarkColorScheme = darkColorScheme(
    primary = SquadOrange,
    onPrimary = Color.White,
    secondary = SquadOrangeDark,
    onSecondary = Color.White,
    background = Color(0xFF121212),
    onBackground = Color.White,
    surface = Color(0xFF1E1E1E),
    onSurface = Color.White,
    error = SquadDanger,
    onError = Color.White,
    outline = Color(0xFF333333)
)

@Composable
fun SquadUpTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) SquadDarkColorScheme else SquadLightColorScheme,
        typography = SquadTypography,
        content = content
    )
}