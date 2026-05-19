package com.example.squadup.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.squadup.ui.theme.SquadIconSecondary
import com.example.squadup.ui.theme.SquadOrange

enum class MatchOverviewTab {
    Events,
    Stats
}

@Composable
fun MatchTabSelector(
    selectedTab: MatchOverviewTab,
    onTabSelected: (MatchOverviewTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
    ) {
        MatchTabItem(
            text = "MATCH EVENTS",
            selected = selectedTab == MatchOverviewTab.Events,
            onClick = {
                onTabSelected(MatchOverviewTab.Events)
            }
        )

        MatchTabItem(
            text = "STATS",
            selected = selectedTab == MatchOverviewTab.Stats,
            onClick = {
                onTabSelected(MatchOverviewTab.Stats)
            }
        )
    }
}

@Composable
private fun MatchTabItem(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Text(
        text = text,
        fontSize = 12.sp,
        fontWeight = FontWeight.SemiBold,
        color = if (selected) androidx.compose.ui.graphics.Color.White else SquadIconSecondary,
        modifier = Modifier
            .background(
                color = if (selected) SquadOrange else androidx.compose.ui.graphics.Color.Transparent,
                shape = RoundedCornerShape(999.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 7.dp)
    )
}