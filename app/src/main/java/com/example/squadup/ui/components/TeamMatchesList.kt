package com.example.squadup.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

data class TeamMatchUi(
    val title: String,
    val date: String,
    val time: String
)

@Composable
fun TeamMatchesList(
    matches: List<TeamMatchUi>,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 425.dp),
        color = Color.White,
        shape = RoundedCornerShape(6.dp),
        border = BorderStroke(1.dp, Color(0x00000000))
    ) {
        Column {
            matches.forEachIndexed { index, match ->
                TeamMatchItem(
                    title = match.title,
                    date = match.date,
                    time = match.time
                )

                if (index < matches.lastIndex) {
                    HorizontalDivider(color = Color(0xFFF1F1F1))
                }
            }
        }
    }
}