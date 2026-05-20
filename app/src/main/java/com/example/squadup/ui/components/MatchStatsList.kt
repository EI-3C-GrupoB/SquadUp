package com.example.squadup.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.squadup.ui.theme.SquadBorder
import com.example.squadup.ui.theme.SquadDivider

data class MatchStatUi(
    val label: String,
    val homeValue: String,
    val awayValue: String,
    val progress: Float
)

@Composable
fun MatchStatsList(
    stats: List<MatchStatUi>,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = Color.White,
        shape = RoundedCornerShape(6.dp),
        border = BorderStroke(
            width = 1.dp,
            color = SquadBorder
        )
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
        ) {
            stats.forEachIndexed { index, stat ->
                MatchStatRow(
                    label = stat.label,
                    homeValue = stat.homeValue,
                    awayValue = stat.awayValue,
                    progress = stat.progress
                )

                if (index < stats.lastIndex) {
                    HorizontalDivider(
                        thickness = 1.dp,
                        color = SquadDivider
                    )
                }
            }
        }
    }
}