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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.squadup.R
import com.example.squadup.core.ui.theme.SquadGrayLight
import com.example.squadup.core.ui.theme.SquadOrange
import com.example.squadup.core.ui.theme.SquadTextPrimary

enum class TicketTab { Upcoming, History }

@Composable
fun TicketTabSelector(
    selectedTab: TicketTab,
    onTabSelected: (TicketTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 14.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TicketTabItem(
            text = stringResource(R.string.tickets_tab_upcoming),
            selected = selectedTab == TicketTab.Upcoming,
            onClick = { onTabSelected(TicketTab.Upcoming) },
            modifier = Modifier.weight(1f)
        )
        TicketTabItem(
            text = stringResource(R.string.tickets_tab_history),
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
