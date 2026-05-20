package com.example.squadup.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.squadup.ui.theme.SquadIconSecondary
import com.example.squadup.ui.theme.SquadOrange

enum class TeamDetailsTab {
    Members,
    Matches
}

@Composable
fun TeamTabSelector(
    selectedTab: TeamDetailsTab,
    onTabSelected: (TeamDetailsTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier) {
        TeamTabItem(
            text = "MEMBERS",
            selected = selectedTab == TeamDetailsTab.Members,
            onClick = { onTabSelected(TeamDetailsTab.Members) }
        )

        TeamTabItem(
            text = "MATCHES",
            selected = selectedTab == TeamDetailsTab.Matches,
            onClick = { onTabSelected(TeamDetailsTab.Matches) }
        )
    }
}

@Composable
private fun TeamTabItem(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Text(
        text = text,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        color = if (selected) Color.White else SquadIconSecondary,
        modifier = Modifier
            .background(
                color = if (selected) SquadOrange else Color.Transparent,
                shape = RoundedCornerShape(999.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 8.dp)
    )
}