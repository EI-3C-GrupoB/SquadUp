package com.example.squadup.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.squadup.ui.theme.SquadGrayLight
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
    val selectorHeight = 64.dp
    val selectorPadding = 6.dp
    val density = LocalDensity.current

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .height(selectorHeight)
            .background(
                color = SquadGrayLight,
                shape = RoundedCornerShape(9.dp)
            )
            .padding(selectorPadding)
    ) {
        val thumbWidth = maxWidth / 2
        val thumbOffset by animateDpAsState(
            targetValue = if (selectedTab == TeamsHubTab.MyTeams) 0.dp else thumbWidth,
            animationSpec = tween(durationMillis = 180),
            label = "TeamsHubTabThumbOffset"
        )

        Box(
            modifier = Modifier
                .offset {
                    IntOffset(
                        x = with(density) { thumbOffset.roundToPx() },
                        y = 0
                    )
                }
                .width(thumbWidth)
                .height(selectorHeight - selectorPadding * 2)
                .background(
                    color = SquadOrange,
                    shape = RoundedCornerShape(9.dp)
                )
        )

        Row(
            modifier = Modifier.fillMaxSize()
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
}

@Composable
private fun TeamsHubTabItem(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            textAlign = TextAlign.Center,
            fontSize = 13.sp,
            lineHeight = 17.sp,
            fontWeight = FontWeight.Bold,
            color = if (selected) SquadSurface else SquadTextPrimary
        )
    }
}
