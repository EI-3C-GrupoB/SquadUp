package com.example.squadup.core.utils

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import com.example.squadup.core.enums.SportType

fun SportType.toIcon(): ImageVector = when (this) {
    SportType.SOCCER     -> Icons.Outlined.SportsSoccer
    SportType.BASKETBALL -> Icons.Outlined.SportsBasketball
    SportType.PADDLE     -> Icons.Outlined.SportsTennis
    SportType.VOLLEYBALL -> Icons.Outlined.SportsVolleyball
    SportType.FUTSAL     -> Icons.Outlined.SportsSoccer
}

fun SportType.toCardColor(): Color = this.color

fun SportType.toDisplayName(context: Context): String = context.getString(this.labelRes)
