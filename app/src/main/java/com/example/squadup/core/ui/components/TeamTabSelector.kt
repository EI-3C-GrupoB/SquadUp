package com.example.squadup.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.squadup.core.ui.theme.SquadGrayLight
import com.example.squadup.core.ui.theme.SquadOrange
import com.example.squadup.core.ui.theme.SquadTextPrimary

enum class TeamDetailsTab { Members, Matches }

@Composable
fun TeamTabSelector(
    selectedTab: TeamDetailsTab,
    onTabSelected: (TeamDetailsTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 14.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TeamTabItem(
            text = "MEMBERS",
            selected = selectedTab == TeamDetailsTab.Members,
            onClick = { onTabSelected(TeamDetailsTab.Members) },
            modifier = Modifier.weight(1f)
        )
        TeamTabItem(
            text = "MATCHES",
            selected = selectedTab == TeamDetailsTab.Matches,
            onClick = { onTabSelected(TeamDetailsTab.Matches) },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun TeamTabItem(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        textAlign = TextAlign.Center,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        color = if (selected) Color.White else SquadTextPrimary,
        modifier = modifier
            .background(
                color = if (selected) SquadOrange else SquadGrayLight,
                shape = RoundedCornerShape(999.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 10.dp)
    )
}
