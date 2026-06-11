package com.example.squadup.core.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun rememberIsLandscape(): Boolean {
    return LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE
}

@Composable
fun responsiveHorizontalPadding(portrait: Dp): Dp {
    return if (rememberIsLandscape() && portrait >= 16.dp) portrait + 12.dp else portrait
}

@Composable
fun responsiveVerticalSpacing(portrait: Dp): Dp {
    return if (rememberIsLandscape()) (portrait * 0.55f).coerceAtLeast(8.dp) else portrait
}

@Composable
fun Modifier.responsiveContentWidth(maxLandscapeWidth: Dp = 980.dp): Modifier {
    return if (rememberIsLandscape()) {
        fillMaxWidth().widthIn(max = maxLandscapeWidth)
    } else {
        fillMaxWidth()
    }
}
