package com.example.squadup.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val SquadLightColorScheme = lightColorScheme(
    primary = SquadOrange,
    onPrimary = SquadWhite,

    primaryContainer = SquadOrangeLight,
    onPrimaryContainer = SquadOrangeDark,

    secondary = SquadOrangeDark,
    onSecondary = SquadWhite,

    secondaryContainer = SquadOrangeLight,
    onSecondaryContainer = SquadOrangeDark,

    tertiary = SquadOrange,
    onTertiary = SquadWhite,

    tertiaryContainer = SquadOrangeLight,
    onTertiaryContainer = SquadOrangeDark,

    background = SquadBackground,
    onBackground = SquadTextPrimary,

    surface = SquadSurface,
    onSurface = SquadTextPrimary,

    surfaceVariant = SquadGrayLight,
    onSurfaceVariant = SquadTextSecondary,

    surfaceTint = SquadOrange,

    inverseSurface = SquadTextPrimary,
    inverseOnSurface = SquadWhite,
    inversePrimary = SquadOrangeLight,

    error = SquadError,
    onError = SquadWhite,

    errorContainer = SquadGrayLight,
    onErrorContainer = SquadError,

    outline = SquadGray,
    outlineVariant = SquadGrayLight,

    scrim = SquadTextPrimary.copy(alpha = 0.35f)
)

@Composable
fun SquadUpTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = SquadLightColorScheme,
        typography = SquadTypography,
        content = content
    )
}