package com.example.squadup.ui.screens.events

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.SportsVolleyball
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.squadup.ui.components.AppHeader
import com.example.squadup.ui.components.ExploreEventUi
import com.example.squadup.ui.components.defaultExploreEvents
import com.example.squadup.ui.theme.SquadBackground
import com.example.squadup.ui.theme.SquadGrayDark
import com.example.squadup.ui.theme.SquadGrayLight
import com.example.squadup.ui.theme.SquadOrange
import com.example.squadup.ui.theme.SquadOrangeLight
import com.example.squadup.ui.theme.SquadSurface
import com.example.squadup.ui.theme.SquadTextPrimary
import com.example.squadup.ui.theme.SquadTextSecondary

@Composable
fun EventDetailsScreen(
    event: ExploreEventUi,
    date: String,
    time: String,
    rules: List<String>,
    onJoinClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            AppHeader(
                showLogo = true,
                title = "SquadUp",
                showBackButton = false,
                actions = {
                    IconButton(onClick = onNotificationsClick) {
                        Icon(
                            imageVector = Icons.Outlined.Notifications,
                            contentDescription = "Notificações",
                            tint = SquadGrayDark
                        )
                    }

                    IconButton(onClick = onSettingsClick) {
                        Icon(
                            imageVector = Icons.Outlined.Settings,
                            contentDescription = "Definições",
                            tint = SquadGrayDark
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(SquadBackground)
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            EventDetailsHero(event = event)

            Spacer(modifier = Modifier.height(28.dp))

            EventDetailsInfoCard(
                date = date,
                time = time,
                rules = rules,
                modifier = Modifier.padding(horizontal = 28.dp)
            )

            Spacer(modifier = Modifier.height(26.dp))

            Button(
                onClick = onJoinClick,
                modifier = Modifier
                    .padding(horizontal = 28.dp)
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SquadOrange,
                    contentColor = Color.White
                )
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.SportsVolleyball,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )

                    Spacer(modifier = Modifier.size(10.dp))

                    Text(
                        text = "Join Event",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun EventDetailsHero(
    event: ExploreEventUi,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(176.dp)
            .background(Brush.linearGradient(event.gradient))
    ) {
        Icon(
            imageVector = event.icon,
            contentDescription = null,
            tint = Color.White.copy(alpha = 0.22f),
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 44.dp)
                .size(116.dp)
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.76f)
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(horizontal = 28.dp, vertical = 20.dp)
        ) {
            Text(
                text = event.title.replace("\n", " "),
                fontSize = 24.sp,
                lineHeight = 28.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Open Tournament",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White,
                    modifier = Modifier
                        .background(
                            color = Color.Black.copy(alpha = 0.48f),
                            shape = RoundedCornerShape(999.dp)
                        )
                        .padding(horizontal = 14.dp, vertical = 5.dp)
                )

                Text(
                    text = event.tag.uppercase(),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    color = SquadTextPrimary,
                    modifier = Modifier
                        .background(
                            color = Color.White,
                            shape = RoundedCornerShape(999.dp)
                        )
                        .padding(horizontal = 14.dp, vertical = 5.dp)
                )
            }
        }
    }
}

@Composable
private fun EventDetailsInfoCard(
    date: String,
    time: String,
    rules: List<String>,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = SquadSurface,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(
            width = 1.dp,
            color = SquadGrayLight
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                EventInfoBlock(
                    icon = Icons.Outlined.CalendarMonth,
                    title = "Date",
                    value = date,
                    modifier = Modifier.weight(1f)
                )

                EventInfoBlock(
                    icon = Icons.Outlined.AccessTime,
                    title = "Time",
                    value = time,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(22.dp))

            HorizontalDivider(color = SquadGrayLight)

            Spacer(modifier = Modifier.height(22.dp))

            Text(
                text = "Tournament Rules",
                fontSize = 16.sp,
                lineHeight = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = SquadTextPrimary
            )

            Spacer(modifier = Modifier.height(16.dp))

            rules.forEachIndexed { index, rule ->
                TournamentRuleRow(text = rule)

                if (index < rules.lastIndex) {
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
private fun EventInfoBlock(
    icon: ImageVector,
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .background(
                    color = SquadOrangeLight,
                    shape = RoundedCornerShape(12.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = SquadOrange,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.size(12.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                fontSize = 14.sp,
                lineHeight = 18.sp,
                color = SquadTextSecondary
            )

            Text(
                text = value,
                fontSize = 16.sp,
                lineHeight = 20.sp,
                fontWeight = FontWeight.Bold,
                color = SquadTextPrimary
            )
        }
    }
}

@Composable
private fun TournamentRuleRow(
    text: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = Icons.Outlined.CheckCircle,
            contentDescription = null,
            tint = SquadOrange,
            modifier = Modifier
                .padding(top = 2.dp)
                .size(18.dp)
        )

        Spacer(modifier = Modifier.size(12.dp))

        Text(
            text = text,
            fontSize = 14.sp,
            lineHeight = 19.sp,
            color = SquadTextPrimary
        )
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
fun EventDetailsScreenPreview() {
    val event = defaultExploreEvents().first {
        it.title.contains("Sand Pro Tour")
    }

    EventDetailsScreen(
        event = event,
        date = "Oct 24,\n2023",
        time = "6:00 PM -\n9:00 PM",
        rules = listOf(
            "Full-court 6v6 standard rules apply.",
            "Game duration: Until one team wins two sets",
            "Professional referees provided for all matches.",
            "Winners move to semi-finals held next Saturday."
        ),
        onJoinClick = {},
        onNotificationsClick = {},
        onSettingsClick = {}
    )
}