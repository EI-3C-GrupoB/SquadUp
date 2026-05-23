package com.example.squadup.ui.screens.admin

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.squadup.ui.theme.SquadBackground
import com.example.squadup.ui.theme.SquadGrayDark
import com.example.squadup.ui.theme.SquadGrayLight
import com.example.squadup.ui.theme.SquadOrange
import com.example.squadup.ui.theme.SquadSurface
import com.example.squadup.ui.theme.SquadTextPrimary
import com.example.squadup.ui.theme.SquadTextSecondary
import com.example.squadup.ui.theme.SquadUpTheme

private val BorderOrange = Color(0xFFFFC9B0)
private val SoftOrange = Color(0xFFFFD8C7)
private val RedEnd = Color(0xFFC8171D)
private val PurpleSoft = Color(0xFFF0ECF3)
private val BlueTeamBg = Color(0xFFEFF2F7)
private val EventMuted = Color(0xFFF5F3F5)
private val Teal = Color(0xFF008577)
private val Blue = Color(0xFF5167D9)

@Composable
fun MatchOverviewScreen(
    onNotificationsClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onGoalPointClick: () -> Unit,
    onFoulCardClick: () -> Unit,
    onSubstitutionClick: () -> Unit,
    onTimeoutClick: () -> Unit,
    onStopTimerClick: () -> Unit,
    onEndMatchClick: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            MatchOverviewTopBar(
                onNotificationsClick = onNotificationsClick,
                onSettingsClick = onSettingsClick
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(SquadBackground)
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 18.dp)
                .padding(top = 18.dp, bottom = 24.dp)
        ) {
            LiveMatchCard()

            Spacer(modifier = Modifier.height(10.dp))

            MatchEventsCard()

            Spacer(modifier = Modifier.height(28.dp))

            ActionButtonsGrid(
                onGoalPointClick = onGoalPointClick,
                onFoulCardClick = onFoulCardClick,
                onSubstitutionClick = onSubstitutionClick,
                onTimeoutClick = onTimeoutClick
            )

            Spacer(modifier = Modifier.height(20.dp))

            AdvancedStatsSection()

            Spacer(modifier = Modifier.height(16.dp))

            TimeManagementCard(
                onStopTimerClick = onStopTimerClick,
                onEndMatchClick = onEndMatchClick
            )

            Spacer(modifier = Modifier.height(18.dp))

            Button(
                onClick = onBackClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SquadOrange,
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = "Go back",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun MatchOverviewTopBar(
    onNotificationsClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = SquadSurface,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .padding(start = 18.dp, end = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Match Overview",
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                color = SquadOrange,
                modifier = Modifier.weight(1f)
            )

            IconButton(
                onClick = onNotificationsClick,
                modifier = Modifier.size(34.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Notifications,
                    contentDescription = "Notificações",
                    tint = SquadGrayDark,
                    modifier = Modifier.size(19.dp)
                )
            }

            IconButton(
                onClick = onSettingsClick,
                modifier = Modifier.size(34.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Settings,
                    contentDescription = "Definições",
                    tint = SquadGrayDark,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun AdminMatchCard(
    modifier: Modifier = Modifier,
    borderColor: Color = BorderOrange,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = SquadSurface,
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, borderColor)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            content = content
        )
    }
}

@Composable
private fun LiveMatchCard() {
    AdminMatchCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(9.dp)
                        .background(SquadOrange, CircleShape)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "L I V E   M A T C H",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.8.sp,
                    color = SquadOrange
                )
            }

            Surface(
                color = Color(0xFFEDEBED),
                shape = RoundedCornerShape(7.dp)
            ) {
                Text(
                    text = "38:42",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = SquadTextPrimary,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(26.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TeamScoreBlock(
                teamLogoText = "🦁",
                teamName = "BENFICA\nSTARS",
                modifier = Modifier.weight(1f)
            )

            Text(
                text = "2",
                fontSize = 33.sp,
                fontWeight = FontWeight.Medium,
                color = SquadOrange
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = "1",
                fontSize = 33.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF9CA3AF)
            )

            TeamScoreBlock(
                teamLogoText = "🛡️",
                teamName = "FC PORTO\nELITE",
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun TeamScoreBlock(
    teamLogoText: String,
    teamName: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(BlueTeamBg),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = teamLogoText,
                fontSize = 28.sp
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = teamName,
            fontSize = 12.sp,
            lineHeight = 16.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = SquadTextPrimary
        )
    }
}

@Composable
private fun MatchEventsCard() {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                    .background(SquadOrange)
                    .padding(horizontal = 10.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "MATCH EVENTS",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Box(
                modifier = Modifier
                    .padding(start = 8.dp)
                    .padding(top = 8.dp)
            ) {
                Text(
                    text = "STATS",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = SquadTextSecondary
                )
            }
        }

        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = SquadSurface,
            shape = RoundedCornerShape(bottomStart = 8.dp, bottomEnd = 8.dp, topEnd = 8.dp),
            border = BorderStroke(1.dp, BorderOrange)
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp)
            ) {
                MatchEventRow(
                    iconText = "⚽",
                    iconBackground = SoftOrange,
                    title = "Goal - Lions VC",
                    subtitle = "J. Smith • 32:15",
                    muted = false
                )

                Spacer(modifier = Modifier.height(16.dp))

                MatchEventRow(
                    iconText = "△",
                    iconBackground = EventMuted,
                    title = "Foul - Blue Hawks",
                    subtitle = "M. Jordan • 28:04",
                    muted = true
                )
            }
        }
    }
}

@Composable
private fun MatchEventRow(
    iconText: String,
    iconBackground: Color,
    title: String,
    subtitle: String,
    muted: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .background(iconBackground, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = iconText,
                fontSize = 12.sp,
                color = if (muted) SquadTextSecondary else SquadOrange
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = if (muted) SquadTextSecondary else SquadTextPrimary
            )

            Spacer(modifier = Modifier.height(3.dp))

            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = SquadTextSecondary
            )
        }

        Text(
            text = "−",
            fontSize = 22.sp,
            fontWeight = FontWeight.Medium,
            color = SquadGrayDark
        )
    }
}

@Composable
private fun ActionButtonsGrid(
    onGoalPointClick: () -> Unit,
    onFoulCardClick: () -> Unit,
    onSubstitutionClick: () -> Unit,
    onTimeoutClick: () -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            MatchActionButton(
                text = "Goal/Point",
                iconText = "⚽",
                selected = true,
                onClick = onGoalPointClick,
                modifier = Modifier.weight(1f)
            )

            MatchActionButton(
                text = "Foul/Card",
                iconText = "▱",
                selected = false,
                iconColor = RedEnd,
                onClick = onFoulCardClick,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            MatchActionButton(
                text = "Substitution",
                iconText = "↻",
                selected = false,
                iconColor = Teal,
                onClick = onSubstitutionClick,
                modifier = Modifier.weight(1f)
            )

            MatchActionButton(
                text = "Timeout",
                iconText = "⑩",
                selected = false,
                iconColor = Blue,
                onClick = onTimeoutClick,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun MatchActionButton(
    text: String,
    iconText: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    iconColor: Color = SquadTextPrimary
) {
    Surface(
        modifier = modifier
            .height(68.dp)
            .clickable(onClick = onClick),
        color = if (selected) SquadOrange else SquadSurface,
        shape = RoundedCornerShape(8.dp),
        border = if (selected) null else BorderStroke(1.dp, BorderOrange)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = iconText,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = if (selected) SquadTextPrimary else iconColor
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = text,
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                color = SquadTextPrimary
            )
        }
    }
}

@Composable
private fun AdvancedStatsSection() {
    Column {
        Text(
            text = "Advanced Stats",
            fontSize = 16.sp,
            fontWeight = FontWeight.Black,
            color = SquadTextPrimary
        )

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AdvancedStatButton(
                iconText = "☝",
                text = "Saves",
                modifier = Modifier.weight(1f)
            )

            AdvancedStatButton(
                iconText = "⚐",
                text = "Corners",
                modifier = Modifier.weight(1f)
            )

            AdvancedStatButton(
                iconText = "»",
                text = "Offsides",
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AdvancedStatButton(
                iconText = "◎",
                text = "Shots On",
                modifier = Modifier.weight(1f)
            )

            AdvancedStatButton(
                iconText = "◉",
                text = "Total Shots",
                modifier = Modifier.weight(1f)
            )

            AdvancedStatButton(
                iconText = "☆",
                text = "Opportunities",
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun AdvancedStatButton(
    iconText: String,
    text: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.height(44.dp),
        color = PurpleSoft,
        shape = RoundedCornerShape(6.dp),
        border = BorderStroke(1.dp, Color(0xFFE6DFE8))
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = iconText,
                fontSize = 15.sp,
                color = SquadTextPrimary
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = text,
                fontSize = 8.sp,
                fontWeight = FontWeight.Black,
                color = SquadTextPrimary,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun TimeManagementCard(
    onStopTimerClick: () -> Unit,
    onEndMatchClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = SquadSurface,
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, Color(0xFFE0E4EA))
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 16.dp)
        ) {
            Text(
                text = "Time Management",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = SquadTextPrimary
            )

            Spacer(modifier = Modifier.height(14.dp))

            Button(
                onClick = onStopTimerClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, Color(0xFF111827)),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SquadSurface,
                    contentColor = SquadTextPrimary
                )
            ) {
                Text(
                    text = "Ⅱ",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.width(18.dp))

                Text(
                    text = "Stop Timer",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onEndMatchClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = RedEnd,
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = "▢",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.width(18.dp))

                Text(
                    text = "End Match",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
fun MatchOverviewScreenPreview() {
    SquadUpTheme {
        MatchOverviewScreen(
            onNotificationsClick = {},
            onSettingsClick = {},
            onGoalPointClick = {},
            onFoulCardClick = {},
            onSubstitutionClick = {},
            onTimeoutClick = {},
            onStopTimerClick = {},
            onEndMatchClick = {},
            onBackClick = {}
        )
    }
}