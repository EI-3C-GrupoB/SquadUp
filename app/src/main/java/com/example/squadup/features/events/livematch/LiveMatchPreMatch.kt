package com.example.squadup.features.events.livematch

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
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
import com.example.squadup.core.ui.components.AppHeader
import com.example.squadup.core.ui.theme.*

@Composable
fun LiveMatchPreMatch(
    uiState: LiveMatchUiState,
    onBackClick: () -> Unit,
    onStartMatch: () -> Unit
) {
    Scaffold(
        topBar = {
            AppHeader(
                showLogo = false,
                showBackButton = true,
                onBackClick = onBackClick,
                title = stringResource(R.string.liveMatch_pre_match_title)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(SquadBackground)
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // Hero matchup card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White,
                shape = RoundedCornerShape(16.dp),
                shadowElevation = 3.dp
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    // Teams
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        TeamBadge(uiState.homeTeamAbbr, uiState.homeTeamName, modifier = Modifier.weight(1f))
                        Text(
                            "VS",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = SquadGray,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                        TeamBadge(uiState.awayTeamAbbr, uiState.awayTeamName, modifier = Modifier.weight(1f))
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = SquadGrayLight)
                    Spacer(modifier = Modifier.height(12.dp))

                    // Date + Venue
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Icon(Icons.Outlined.CalendarMonth, null, tint = SquadOrange, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Column {
                                Text(uiState.scheduledDate, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = SquadTextPrimary)
                                Text(uiState.scheduledTime, fontSize = 12.sp, color = SquadTextSecondary)
                            }
                        }
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Icon(Icons.Outlined.LocationOn, null, tint = SquadOrange, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(uiState.venue, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = SquadTextPrimary)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Players per team
            Text(
                stringResource(R.string.liveMatch_players_title),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = SquadTextSecondary,
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Max),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                PlayerListCard(
                    teamAbbr = uiState.homeTeamAbbr,
                    teamName = uiState.homeTeamName,
                    players = uiState.homePlayers,
                    modifier = Modifier.weight(1f).fillMaxHeight()
                )
                PlayerListCard(
                    teamAbbr = uiState.awayTeamAbbr,
                    teamName = uiState.awayTeamName,
                    players = uiState.awayPlayers,
                    modifier = Modifier.weight(1f).fillMaxHeight()
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Start Match
            Button(
                onClick = onStartMatch,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SquadOrange,
                    contentColor = Color.White
                )
            ) {
                Icon(Icons.Outlined.PlayArrow, null, modifier = Modifier.size(22.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    stringResource(R.string.liveMatch_start_match),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            Spacer(modifier = Modifier.height(88.dp))
        }
    }
}

@Composable
private fun TeamBadge(abbr: String, name: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .background(SquadOrange, RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(abbr, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(name, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = SquadTextPrimary, textAlign = TextAlign.Center)
    }
}

@Composable
private fun PlayerListCard(
    teamAbbr: String,
    teamName: String,
    players: List<LiveMatchPlayer>,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = Color.White,
        shape = RoundedCornerShape(12.dp),
        shadowElevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            // Header
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .background(SquadOrange, RoundedCornerShape(6.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(teamAbbr, fontSize = 8.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                }
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    "${players.size} players",
                    fontSize = 11.sp,
                    color = SquadTextSecondary
                )
            }

            if (players.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider(color = SquadGrayLight)
                Spacer(modifier = Modifier.height(6.dp))

                players.forEach { player ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .background(SquadOrangeLight, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(player.initials, fontSize = 8.sp, fontWeight = FontWeight.Bold, color = SquadOrange)
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            player.name,
                            fontSize = 11.sp,
                            color = SquadTextPrimary,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}
