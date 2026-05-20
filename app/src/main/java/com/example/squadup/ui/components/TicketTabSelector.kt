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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.squadup.ui.theme.SquadSurface
import com.example.squadup.ui.theme.SquadSurfaceVariant
import com.example.squadup.ui.theme.SquadTextPrimary
import com.example.squadup.ui.theme.SquadOrangeDark

enum class TicketTab {
    Upcoming,
    History
}

@Composable
fun TicketTabSelector(
    selectedTab: TicketTab,
    onTabSelected: (TicketTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = SquadSurfaceVariant,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(4.dp)
    ) {
        TicketTabItem(
            text = "Upcoming",
            selected = selectedTab == TicketTab.Upcoming,
            onClick = { onTabSelected(TicketTab.Upcoming) },
            modifier = Modifier.weight(1f)
        )

        TicketTabItem(
            text = "History",
            selected = selectedTab == TicketTab.History,
            onClick = { onTabSelected(TicketTab.History) },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun TicketTabItem(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        textAlign = TextAlign.Center,
        fontSize = 14.sp,
        fontWeight = FontWeight.SemiBold,
        color = if (selected) SquadOrangeDark else SquadTextPrimary,
        modifier = modifier
            .background(
                color = if (selected) SquadSurface else SquadSurfaceVariant,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp)
    )
}