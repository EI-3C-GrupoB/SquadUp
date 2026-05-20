package com.example.squadup.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.squadup.ui.theme.SquadBorder
import com.example.squadup.ui.theme.SquadDivider

data class MatchEventUi(
    val type: MatchEventType,
    val title: String,
    val subtitle: String
)

@Composable
fun MatchEventList(
    events: List<MatchEventUi>,
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
        Column {
            events.forEachIndexed { index, event ->
                MatchEventItem(
                    type = event.type,
                    title = event.title,
                    subtitle = event.subtitle
                )

                if (index < events.lastIndex) {
                    HorizontalDivider(
                        thickness = 1.dp,
                        color = SquadDivider
                    )
                }
            }
        }
    }
}