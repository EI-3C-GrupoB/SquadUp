package com.example.squadup.features.events.livematch

import androidx.compose.material3.MaterialTheme

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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.squadup.R
import com.example.squadup.core.ui.components.AppHeader
import com.example.squadup.core.ui.components.responsiveHorizontalPadding
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
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            // ── Hero gradient banner ──────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.linearGradient(listOf(Color(0xFFB80000), SquadOrange, Color(0xFFFF6D00)))
                    )
                    .padding(horizontal = 24.dp, vertical = 28.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    // Badge PRÉ-JOGO
                    Surface(
                        color = Color.White.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(999.dp)
                    ) {
                        Text(
                            "PRÉ-JOGO",
                            fontSize = 10.sp, fontWeight = FontWeight.ExtraBold,
                            color = Color.White, letterSpacing = 1.sp,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 5.dp)
                        )
                    }

                    Spacer(Modifier.height(24.dp))

                    // Teams VS
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        HeroTeamBadge(uiState.homeTeamAbbr, uiState.homeTeamName, Modifier.weight(1f))
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(horizontal = responsiveHorizontalPadding(12.dp))
                        ) {
                            Text("VS", fontSize = 26.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                            Text("0  –  0", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White.copy(alpha = 0.5f))
                        }
                        HeroTeamBadge(uiState.awayTeamAbbr, uiState.awayTeamName, Modifier.weight(1f))
                    }

                    Spacer(Modifier.height(24.dp))

                    // Date & venue chips
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        InfoChip(icon = Icons.Outlined.CalendarMonth, text = "${uiState.scheduledDate}  ${uiState.scheduledTime}")
                        if (uiState.venue.isNotBlank()) {
                            Spacer(Modifier.width(8.dp))
                            InfoChip(
                                icon = Icons.Outlined.LocationOn,
                                text = uiState.venue,
                                maxLines = 1
                            )
                        }
                    }
                }
            }

            // ── Players section ───────────────────────────────────────────────
            Column(modifier = Modifier.padding(horizontal = responsiveHorizontalPadding(20.dp))) {
                Spacer(Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        stringResource(R.string.liveMatch_players_title),
                        fontSize = 11.sp, fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant, letterSpacing = 0.6.sp,
                        modifier = Modifier.weight(1f)
                    )
                    val totalPlayers = uiState.homePlayers.size + uiState.awayPlayers.size
                    if (totalPlayers > 0) {
                        Surface(color = SquadOrangeLight, shape = RoundedCornerShape(999.dp)) {
                            Text(
                                "$totalPlayers jogadores",
                                fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = SquadOrange,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                Spacer(Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    PlayerListCard(
                        teamAbbr = uiState.homeTeamAbbr,
                        teamName = uiState.homeTeamName,
                        players = uiState.homePlayers,
                        modifier = Modifier.weight(1f)
                    )
                    PlayerListCard(
                        teamAbbr = uiState.awayTeamAbbr,
                        teamName = uiState.awayTeamName,
                        players = uiState.awayPlayers,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(Modifier.height(28.dp))

                if (uiState.isOrganizer) {
                    Button(
                        onClick = onStartMatch,
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SquadOrange, contentColor = Color.White),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                    ) {
                        Icon(Icons.Outlined.PlayArrow, null, modifier = Modifier.size(22.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(stringResource(R.string.liveMatch_start_match), fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
                    }
                } else {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = SquadOrangeLight,
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 18.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Schedule,
                                contentDescription = null,
                                tint = SquadOrange,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(Modifier.width(10.dp))
                            Text(
                                text = stringResource(R.string.liveMatch_waiting),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = SquadOrange
                            )
                        }
                    }
                }

                Spacer(Modifier.height(88.dp))
            }
        }
    }
}

@Composable
private fun HeroTeamBadge(abbr: String, name: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.2f), RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(abbr, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
        }
        Spacer(Modifier.height(8.dp))
        Text(
            name, fontSize = 13.sp, fontWeight = FontWeight.Bold,
            color = Color.White, textAlign = TextAlign.Center, maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun InfoChip(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    maxLines: Int = 2
) {
    Surface(
        color = Color.White.copy(alpha = 0.18f),
        shape = RoundedCornerShape(999.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Icon(icon, null, tint = Color.White, modifier = Modifier.size(13.dp))
            Text(text, fontSize = 11.sp, color = Color.White, maxLines = maxLines, overflow = TextOverflow.Ellipsis)
        }
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
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(14.dp),
        shadowElevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(30.dp).background(SquadOrange, RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(teamAbbr, fontSize = 8.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                }
                Spacer(Modifier.width(8.dp))
                Column {
                    Text(teamName, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text("${players.size} jogadores", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            if (players.isNotEmpty()) {
                Spacer(Modifier.height(10.dp))
                HorizontalDivider(color = SquadGrayLight)
                Spacer(Modifier.height(8.dp))

                players.take(6).forEach { player ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier.size(26.dp).background(SquadOrangeLight, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(player.initials, fontSize = 8.sp, fontWeight = FontWeight.Bold, color = SquadOrange)
                        }
                        Spacer(Modifier.width(7.dp))
                        Text(player.name, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                }
                if (players.size > 6) {
                    Spacer(Modifier.height(4.dp))
                    Text("+ ${players.size - 6} mais", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                Spacer(Modifier.height(12.dp))
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text("Sem jogadores", fontSize = 11.sp, color = SquadGray, textAlign = TextAlign.Center)
                }
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}