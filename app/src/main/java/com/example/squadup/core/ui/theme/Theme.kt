package com.example.squadup.core.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

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

private val SquadDarkColorScheme = darkColorScheme(
    primary = SquadOrange,
    onPrimary = SquadWhite,

    primaryContainer = SquadOrangeDark,
    onPrimaryContainer = SquadWhite,

    secondary = SquadOrange,
    onSecondary = SquadWhite,

    secondaryContainer = SquadOrangeDark,
    onSecondaryContainer = SquadWhite,

    tertiary = SquadOrange,
    onTertiary = SquadWhite,

    tertiaryContainer = SquadOrangeDark,
    onTertiaryContainer = SquadWhite,

    background = SquadDarkBackground,
    onBackground = SquadDarkTextPrimary,

    surface = SquadDarkSurface,
    onSurface = SquadDarkTextPrimary,

    surfaceVariant = SquadDarkSurfaceVariant,
    onSurfaceVariant = SquadDarkTextSecondary,

    surfaceTint = SquadOrange,

    inverseSurface = SquadWhite,
    inverseOnSurface = SquadDarkBackground,
    inversePrimary = SquadOrangeDark,

    error = SquadErrorLight,
    onError = SquadWhite,

    errorContainer = SquadDarkErrorContainer,
    onErrorContainer = SquadErrorLight,

    outline = SquadDarkOutline,
    outlineVariant = SquadDarkSurfaceVariant,

    scrim = SquadWhite.copy(alpha = 0.35f)
)

@Composable
fun SquadUpTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = if (darkTheme) SquadDarkColorScheme else SquadLightColorScheme,
        typography = SquadTypography,
        content = content
    )
}
