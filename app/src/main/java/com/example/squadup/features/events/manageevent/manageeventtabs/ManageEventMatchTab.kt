package com.example.squadup.features.events.manageevent.manageeventtabs

import com.example.squadup.features.events.manageevent.*
import com.example.squadup.core.enums.GameStatus
import com.example.squadup.core.enums.TeamEventStatus

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
import com.example.squadup.core.ui.theme.*

@Composable
internal fun MatchTabContent(
    uiState: ManageEventUiState,
    onManageLiveClick: (String) -> Unit,
    onEditMatchClick: () -> Unit,
    onTeamExpand: (String) -> Unit,
) {
    val game = uiState.scheduledGames.firstOrNull()
    val homeTeam = uiState.teams.getOrNull(0)
    val awayTeam = uiState.teams.getOrNull(1)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // ── Matchup Card ──────────────────────────────────────────────────────
        if (game != null) {
            SingleMatchHeroCard(
                game = game,
                homeTeam = homeTeam,
                awayTeam = awayTeam,
                onManageLive = { onManageLiveClick(game.id) },
                onEdit = onEditMatchClick
            )
        } else {
            MatchEmptyState()
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ── Match Info ────────────────────────────────────────────────────────
        game?.let { g ->
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White,
                shape = RoundedCornerShape(12.dp),
                shadowElevation = 2.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Data
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Outlined.CalendarMonth, null, tint = SquadOrange, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("${g.month} ${g.day}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = SquadTextPrimary)
                        Text(g.time, fontSize = 12.sp, color = SquadTextSecondary)
                    }
                    // Divider
                    Box(modifier = Modifier.width(1.dp).height(48.dp).background(SquadGrayLight).align(Alignment.CenterVertically))
                    // Venue
                    Column(
                        modifier = Modifier.weight(2f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Outlined.LocationOn, null, tint = SquadOrange, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            g.venue.ifBlank { stringResource(R.string.manageEvent_match_no_venue) },
                            fontSize = 13.sp, fontWeight = FontWeight.Medium,
                            color = SquadTextPrimary, textAlign = TextAlign.Center
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        Spacer(modifier = Modifier.height(88.dp))
    }
}

// ─── Single Match Hero Card ───────────────────────────────────────────────────

@Composable
private fun SingleMatchHeroCard(
    game: ScheduledGameItem,
    homeTeam: ManageTeamItem?,
    awayTeam: ManageTeamItem?,
    onManageLive: () -> Unit,
    onEdit: () -> Unit
) {
    val isLive   = game.status == GameStatus.LIVE
    val isWarmUp = game.status == GameStatus.WARM_UP
    val isFinished = game.status == GameStatus.FINISHED
    val isColored = isLive || isWarmUp

    val cardColor = when {
        isLive   -> SquadOrange
        isWarmUp -> Color(0xFFFFA000)
        else     -> Color.White
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = cardColor,
        shape = RoundedCornerShape(16.dp),
        shadowElevation = 4.dp
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            // Status header
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                when {
                    isLive -> {
                        Box(modifier = Modifier.size(8.dp).background(Color.White, CircleShape))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("LIVE", fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, color = Color.White, modifier = Modifier.weight(1f))
                        Text(game.liveTimer ?: "", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                    isWarmUp -> {
                        Box(modifier = Modifier.size(8.dp).background(Color.White, CircleShape))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(stringResource(R.string.manageEvent_games_warmup), fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, color = Color.White, modifier = Modifier.weight(1f))
                    }
                    isFinished -> {
                        Text(stringResource(R.string.manageEvent_match_final_result), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SquadTextSecondary, modifier = Modifier.weight(1f))
                        IconButton(onClick = onEdit, modifier = Modifier.size(32.dp)) {
                            Icon(Icons.Outlined.Edit, null, tint = SquadTextSecondary, modifier = Modifier.size(16.dp))
                        }
                    }
                    else -> {
                        Text(stringResource(R.string.manageEvent_tab_match).uppercase(), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SquadTextSecondary, modifier = Modifier.weight(1f))
                        IconButton(onClick = onEdit, modifier = Modifier.size(32.dp)) {
                            Icon(Icons.Outlined.Edit, null, tint = SquadTextSecondary, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Teams VS
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                // Home
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                    Box(
                        modifier = Modifier.size(56.dp)
                            .background(
                                if (isColored) Color.White.copy(alpha = 0.2f) else SquadOrangeLight,
                                RoundedCornerShape(12.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            (homeTeam?.abbreviation ?: game.homeTeamAbbr).ifBlank { game.homeTeam.take(3) },
                            fontSize = 14.sp, fontWeight = FontWeight.ExtraBold,
                            color = if (isColored) Color.White else SquadOrange
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        homeTeam?.name ?: game.homeTeam,
                        fontSize = 12.sp, fontWeight = FontWeight.SemiBold,
                        color = if (isColored) Color.White.copy(alpha = 0.9f) else SquadTextPrimary,
                        textAlign = TextAlign.Center
                    )
                }

                // Score or VS
                if ((isLive || isFinished) && game.homeScore != null && game.awayScore != null) {
                    Text(
                        "${game.homeScore}  –  ${game.awayScore}",
                        fontSize = 32.sp, fontWeight = FontWeight.ExtraBold,
                        color = if (isColored) Color.White else SquadTextPrimary,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                } else {
                    Text(
                        "VS",
                        fontSize = 22.sp, fontWeight = FontWeight.ExtraBold,
                        color = if (isColored) Color.White else SquadGray,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }

                // Away
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                    Box(
                        modifier = Modifier.size(56.dp)
                            .background(
                                if (isColored) Color.White.copy(alpha = 0.2f) else SquadOrangeLight,
                                RoundedCornerShape(12.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            (awayTeam?.abbreviation ?: game.awayTeamAbbr).ifBlank { game.awayTeam.take(3) },
                            fontSize = 14.sp, fontWeight = FontWeight.ExtraBold,
                            color = if (isColored) Color.White else SquadOrange
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        awayTeam?.name ?: game.awayTeam,
                        fontSize = 12.sp, fontWeight = FontWeight.SemiBold,
                        color = if (isColored) Color.White.copy(alpha = 0.9f) else SquadTextPrimary,
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Action button
            if (isLive || isWarmUp) {
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onManageLive,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = if (isLive) SquadOrange else Color(0xFFFFA000)
                    )
                ) {
                    Icon(Icons.Outlined.PlayArrow, null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        if (isLive) stringResource(R.string.manageEvent_manage_live)
                        else stringResource(R.string.manageEvent_go_to_match),
                        fontSize = 14.sp, fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

// ─── Team Card (simplified) ───────────────────────────────────────────────────

@Composable
private fun MatchTeamCard(team: ManageTeamItem, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        color = Color.White,
        shape = RoundedCornerShape(12.dp),
        shadowElevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Team header
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(36.dp).background(SquadOrange, RoundedCornerShape(6.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(team.abbreviation, fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(team.name, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = SquadTextPrimary, maxLines = 1)
                    Text("${team.playerCount} players", fontSize = 11.sp, color = SquadTextSecondary)
                }
            }

            if (team.players.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider(color = SquadGrayLight)
                Spacer(modifier = Modifier.height(6.dp))

                team.players.take(5).forEach { player ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier.size(24.dp).background(SquadOrangeLight, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(player.initials, fontSize = 8.sp, fontWeight = FontWeight.Bold, color = SquadOrange)
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(player.name, fontSize = 11.sp, color = SquadTextPrimary, maxLines = 1)
                    }
                }

                if (team.players.size > 5) {
                    Text(
                        "+ ${team.players.size - 5} more",
                        fontSize = 10.sp, color = SquadTextSecondary,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }
        }
    }
}

// ─── Empty State ──────────────────────────────────────────────────────────────

@Composable
private fun MatchEmptyState() {
    Column(
        modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(Icons.Outlined.SportsScore, null, tint = SquadGray, modifier = Modifier.size(52.dp))
        Spacer(modifier = Modifier.height(12.dp))
        Text(stringResource(R.string.manageEvent_match_no_game), fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = SquadTextSecondary)
        Text(stringResource(R.string.manageEvent_match_no_game_sub), fontSize = 13.sp, color = SquadGray, textAlign = TextAlign.Center)
    }
}
