package com.example.squadup.core.utils

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.annotation.StringRes
import com.example.squadup.R
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

@StringRes
fun SportType.scoreLabelRes(): Int = when (this) {
    SportType.SOCCER, SportType.FUTSAL -> R.string.stats_score_goals
    SportType.BASKETBALL               -> R.string.stats_score_points
    SportType.PADDLE                   -> R.string.stats_score_wins
    SportType.VOLLEYBALL               -> R.string.stats_score_points
}

@StringRes
fun SportType.topPerformerLabelRes(): Int = when (this) {
    SportType.SOCCER, SportType.FUTSAL -> R.string.stats_top_scorer_football
    SportType.BASKETBALL               -> R.string.stats_top_scorer_basketball
    SportType.PADDLE                   -> R.string.stats_top_player_paddle
    SportType.VOLLEYBALL               -> R.string.stats_top_scorer_volleyball
}
