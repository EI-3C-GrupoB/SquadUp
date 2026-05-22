package com.example.squadup.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.squadup.ui.theme.SquadOrange
import com.example.squadup.ui.theme.SquadSurface
import com.example.squadup.ui.theme.SquadTextPrimary

enum class TeamsHubTab {
    MyTeams,
    Discover
}

@Composable
fun TeamsHubTabSelector(
    selectedTab: TeamsHubTab,
    onTabSelected: (TeamsHubTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .background(
                color = Color(0x00000000),
                shape = RoundedCornerShape(8.dp)
            )
            .padding(2.dp)
    ) {
        TeamsHubTabItem(
            text = "My\nTeams",
            selected = selectedTab == TeamsHubTab.MyTeams,
            onClick = { onTabSelected(TeamsHubTab.MyTeams) },
            modifier = Modifier.weight(1f)
        )

        TeamsHubTabItem(
            text = "Discover",
            selected = selectedTab == TeamsHubTab.Discover,
            onClick = { onTabSelected(TeamsHubTab.Discover) },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun TeamsHubTabItem(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        textAlign = TextAlign.Center,
        fontSize = 13.sp,
        lineHeight = 17.sp,
        fontWeight = FontWeight.Bold,
        color = if (selected) SquadSurface else SquadTextPrimary,
        modifier = modifier
            .background(
                color = if (selected) SquadOrange else Color(0x00000000),
                shape = RoundedCornerShape(6.dp)
            )
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp)
    )
}