package com.example.squadup.core.utils

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import com.example.squadup.core.enums.SportType
import com.example.squadup.R

fun SportType.toIcon(): ImageVector = when (this) {
    SportType.SOCCER  -> Icons.Outlined.SportsSoccer
    SportType.BASKETBALL -> Icons.Outlined.SportsBasketball
    SportType.PADDLE -> Icons.Outlined.SportsTennis
    SportType.VOLLEYBALL -> Icons.Outlined.SportsVolleyball
    SportType.FUTSAL -> Icons.Outlined.SportsSoccer
}

fun SportType.toCardColor(): Color = when (this) {
    SportType.SOCCER -> Color(0xFF2F9D73)
    SportType.BASKETBALL -> Color(0xFFD4611A)
    SportType.PADDLE -> Color(0xFF1A7BD4)
    SportType.VOLLEYBALL -> Color(0xFF9B27AF)
    SportType.FUTSAL -> Color(0xFF1A6B3C)
}

fun SportType.toDisplayName(context: Context): String = when (this) {
    SportType.SOCCER -> context.getString(R.string.sport_soccer)
    SportType.BASKETBALL -> context.getString(R.string.sport_basketball)
    SportType.PADDLE -> context.getString(R.string.sport_paddle)
    SportType.VOLLEYBALL -> context.getString(R.string.sport_volleyball)
    SportType.FUTSAL -> context.getString(R.string.sport_futsal)
}