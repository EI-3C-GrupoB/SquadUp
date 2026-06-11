package com.example.squadup.features.events.manageevent.manageeventtabs

import com.example.squadup.core.ui.components.responsiveHorizontalPadding

import androidx.compose.material3.MaterialTheme

import com.example.squadup.features.events.manageevent.*
import com.example.squadup.core.enums.GameStatus

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.squadup.R
import com.example.squadup.core.ui.theme.*

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun GamesTabContent(
    uiState: ManageEventUiState,
    onSearchQueryChange: (String) -> Unit,
    onEditGameClick: (String) -> Unit,
    onManageLiveClick: (String) -> Unit = {},
) {
    val filtered = if (uiState.gameSearchQuery.isBlank()) uiState.scheduledGames
    else uiState.scheduledGames.filter {
        it.homeTeam.contains(uiState.gameSearchQuery, ignoreCase = true) ||
        it.awayTeam.contains(uiState.gameSearchQuery, ignoreCase = true)
    }

    val liveGame    = uiState.scheduledGames.firstOrNull { it.status == GameStatus.LIVE }
    val warmUpGame  = uiState.scheduledGames.firstOrNull { it.status == GameStatus.WARM_UP }
    val nextGame    = uiState.scheduledGames
        .filter { it.status == GameStatus.SCHEDULED }
        .minByOrNull { it.day.toIntOrNull() ?: Int.MAX_VALUE }
    val heroGame    = liveGame ?: warmUpGame ?: nextGame

    val liveGames      = filtered.filter { it.status == GameStatus.LIVE }
    val warmUpGames    = filtered.filter { it.status == GameStatus.WARM_UP }
    val upcomingGames  = filtered.filter { it.status == GameStatus.SCHEDULED }
        .sortedBy { it.day.toIntOrNull() ?: Int.MAX_VALUE }
    val finishedGames  = filtered.filter { it.status == GameStatus.FINISHED }
        .sortedByDescending { it.day.toIntOrNull() ?: 0 }
    val cancelledGames = filtered.filter { it.status == GameStatus.CANCELLED }

    // Stats
    val totalGames    = uiState.scheduledGames.size
    val inProgress    = uiState.scheduledGames.count { it.status == GameStatus.LIVE || it.status == GameStatus.WARM_UP }
    val upcomingCount = uiState.scheduledGames.count { it.status == GameStatus.SCHEDULED }
    val finishedCount = uiState.scheduledGames.count { it.status == GameStatus.FINISHED }

    val statsMetrics = buildList {
        add(Triple(Icons.Outlined.List,          stringResource(R.string.manageEvent_games_stat_total),    "$totalGames"))
        if (inProgress > 0)
            add(Triple(Icons.Outlined.PlayArrow, stringResource(R.string.manageEvent_games_stat_progress), "$inProgress"))
        add(Triple(Icons.Outlined.CalendarMonth, stringResource(R.string.manageEvent_games_stat_upcoming), "$upcomingCount"))
        add(Triple(Icons.Outlined.Done,          stringResource(R.string.manageEvent_games_stat_finished), "$finishedCount"))
    }
    val warningStats = if (inProgress > 0) setOf(1) else emptySet()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 88.dp)
    ) {
        // Stats strip
        item {
            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)) {
                MetricRow(statsMetrics, warningStats)
            }
        }

        // Hero card (ou empty hero)
        item {
            if (heroGame != null) {
                GameHeroCard(
                    game = heroGame,
                    modifier = Modifier.padding(horizontal = responsiveHorizontalPadding(20.dp)),
                    onManageLive = { onManageLiveClick(heroGame.id) }
                )
            } else {
                GamesEmptyHeroCard(modifier = Modifier.padding(horizontal = responsiveHorizontalPadding(20.dp)))
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Search
        item {
            OutlinedTextField(
                value = uiState.gameSearchQuery,
                onValueChange = onSearchQueryChange,
                placeholder = { Text(stringResource(R.string.manageEvent_search_game), color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp) },
                leadingIcon = { Icon(Icons.Outlined.Search, null, tint = MaterialTheme.colorScheme.onSurfaceVariant) },
                modifier = Modifier.fillMaxWidth().padding(horizontal = responsiveHorizontalPadding(20.dp)),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = SquadOrange,
                    unfocusedBorderColor = Color.Transparent,
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Empty state (sem jogos ou sem resultados de pesquisa)
        if (filtered.isEmpty()) {
            item {
                GamesEmptyState(
                    isSearch = uiState.gameSearchQuery.isNotBlank(),
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 32.dp)
                )
            }
        }

        // Live
        if (liveGames.isNotEmpty()) {
            stickyHeader {
                GameGroupHeader(stringResource(R.string.manageEvent_games_live), liveGames.size, Color(0xFFD32F2F))
            }
            items(liveGames, key = { it.id }) { game ->
                GameRow(game = game, onEdit = { onEditGameClick(game.id) }, onManageLive = { onManageLiveClick(game.id) })
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        // Warm Up
        if (warmUpGames.isNotEmpty()) {
            stickyHeader {
                GameGroupHeader(stringResource(R.string.manageEvent_games_warmup), warmUpGames.size, Color(0xFFFFA000))
            }
            items(warmUpGames, key = { it.id }) { game ->
                GameRow(game = game, onEdit = { onEditGameClick(game.id) }, onManageLive = { onManageLiveClick(game.id) })
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        // Upcoming
        if (upcomingGames.isNotEmpty()) {
            stickyHeader {
                GameGroupHeader(stringResource(R.string.manageEvent_games_upcoming), upcomingGames.size, SquadOrange)
            }
            items(upcomingGames, key = { it.id }) { game ->
                GameRow(game = game, onEdit = { onEditGameClick(game.id) })
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        // Finished
        if (finishedGames.isNotEmpty()) {
            stickyHeader {
                GameGroupHeader(stringResource(R.string.manageEvent_games_finished), finishedGames.size, Color(0xFF2E7D32))
            }
            items(finishedGames, key = { it.id }) { game ->
                GameRow(game = game, onEdit = { onEditGameClick(game.id) })
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        // Cancelled
        if (cancelledGames.isNotEmpty()) {
            stickyHeader {
                GameGroupHeader(stringResource(R.string.manageEvent_games_cancelled), cancelledGames.size, SquadGrayDark)
            }
            items(cancelledGames, key = { it.id }) { game ->
                GameRow(game = game, onEdit = { onEditGameClick(game.id) })
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun GameHeroCard(
    game: ScheduledGameItem,
    modifier: Modifier = Modifier,
    onManageLive: () -> Unit = {}
) {
    val isLive   = game.status == GameStatus.LIVE
    val isWarmUp = game.status == GameStatus.WARM_UP
    val cardColor = when {
        isLive   -> SquadOrange
        isWarmUp -> Color(0xFFFFA000)
        else     -> Color.White
    }
    val isColored = isLive || isWarmUp

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = cardColor,
        shape = RoundedCornerShape(14.dp),
        shadowElevation = 4.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                when {
                    isLive -> {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Box(modifier = Modifier.size(8.dp).background(MaterialTheme.colorScheme.surface, CircleShape))
                            Text("LIVE MATCH", fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        Text(game.liveTimer ?: "", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White.copy(alpha = 0.9f))
                    }
                    isWarmUp -> {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Box(modifier = Modifier.size(8.dp).background(MaterialTheme.colorScheme.surface, CircleShape))
                            Text(stringResource(R.string.manageEvent_games_warmup), fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        Text(game.time, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White.copy(alpha = 0.9f))
                    }
                    else -> {
                        Text(stringResource(R.string.manageEvent_next_game_label), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant, letterSpacing = 0.5.sp)
                        Spacer(modifier = Modifier.weight(1f))
                        Text("${game.month} ${game.day}", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = SquadOrange)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Teams + score
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                    Box(
                        modifier = Modifier.size(48.dp)
                            .background(if (isColored) Color.White.copy(alpha = 0.2f) else SquadOrangeLight, RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(game.homeTeamAbbr.ifBlank { game.homeTeam.take(3) }, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = if (isColored) Color.White else SquadOrange)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(game.homeTeam, fontSize = 11.sp, fontWeight = FontWeight.Medium, color = if (isColored) Color.White.copy(alpha = 0.9f) else MaterialTheme.colorScheme.onSurface, textAlign = TextAlign.Center)
                }

                if (isLive && game.homeScore != null && game.awayScore != null) {
                    Text("${game.homeScore}  —  ${game.awayScore}", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = Color.White, modifier = Modifier.padding(horizontal = responsiveHorizontalPadding(8.dp)))
                } else {
                    Text("VS", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = if (isColored) Color.White else MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(horizontal = responsiveHorizontalPadding(16.dp)))
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                    Box(
                        modifier = Modifier.size(48.dp)
                            .background(if (isColored) Color.White.copy(alpha = 0.2f) else SquadOrangeLight, RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(game.awayTeamAbbr.ifBlank { game.awayTeam.take(3) }, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = if (isColored) Color.White else SquadOrange)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(game.awayTeam, fontSize = 11.sp, fontWeight = FontWeight.Medium, color = if (isColored) Color.White.copy(alpha = 0.9f) else MaterialTheme.colorScheme.onSurface, textAlign = TextAlign.Center)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Venue + time
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val venueColor = if (isColored) Color.White.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant
                Icon(Icons.Outlined.LocationOn, null, tint = venueColor, modifier = Modifier.size(13.dp))
                Spacer(modifier = Modifier.width(3.dp))
                Text(game.venue, fontSize = 12.sp, color = venueColor)
                if (!isColored) {
                    Text(" · ${game.time}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            // Botão de ação (Live → Manage, WarmUp → Go to Match)
            if (isLive || isWarmUp) {
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = onManageLive,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surface, contentColor = if (isLive) SquadOrange else Color(0xFFFFA000))
                ) {
                    Icon(Icons.Outlined.PlayArrow, null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isLive) stringResource(R.string.manageEvent_manage_live) else stringResource(R.string.manageEvent_go_to_match),
                        fontSize = 14.sp, fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun GamesEmptyHeroCard(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(14.dp),
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(Icons.Outlined.CalendarMonth, null, tint = SquadGray, modifier = Modifier.size(40.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(stringResource(R.string.manageEvent_no_games_hero), fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
            Text(stringResource(R.string.manageEvent_no_games_hero_sub), fontSize = 12.sp, color = SquadGray, textAlign = TextAlign.Center)
        }
    }
}

@Composable
private fun GamesEmptyState(isSearch: Boolean, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = if (isSearch) Icons.Outlined.SearchOff else Icons.Outlined.EventBusy,
            contentDescription = null,
            tint = SquadGray,
            modifier = Modifier.size(52.dp)
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = if (isSearch) stringResource(R.string.manageEvent_no_games_search) else stringResource(R.string.manageEvent_no_games_empty),
            fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center
        )
        Text(
            text = if (isSearch) stringResource(R.string.manageEvent_no_games_search_sub) else stringResource(R.string.manageEvent_no_games_empty_sub),
            fontSize = 13.sp, color = SquadGray, textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun GameGroupHeader(label: String, count: Int, color: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 20.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(modifier = Modifier.size(8.dp).background(color, CircleShape))
        Text(label, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = color, letterSpacing = 0.5.sp)
        Box(
            modifier = Modifier
                .background(color.copy(alpha = 0.1f), RoundedCornerShape(999.dp))
                .padding(horizontal = 8.dp, vertical = 2.dp)
        ) {
            Text("$count", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = color)
        }
    }
}

@Composable
private fun GameRow(
    game: ScheduledGameItem,
    onEdit: () -> Unit,
    onManageLive: () -> Unit = {}
) {
    val isFinished  = game.status == GameStatus.FINISHED
    val isLive      = game.status == GameStatus.LIVE
    val isWarmUp    = game.status == GameStatus.WARM_UP
    val isCancelled = game.status == GameStatus.CANCELLED

    val dateBadgeBg = when (game.status) {
        GameStatus.LIVE      -> Color(0xFFFFEBEE)
        GameStatus.WARM_UP   -> Color(0xFFFFF3E0)
        GameStatus.FINISHED  -> SquadGrayLight
        GameStatus.CANCELLED -> SquadGrayLight
        else                 -> SquadOrangeLight
    }
    val dateBadgeColor = when (game.status) {
        GameStatus.LIVE      -> Color(0xFFD32F2F)
        GameStatus.WARM_UP   -> Color(0xFFFFA000)
        GameStatus.FINISHED  -> SquadGrayDark
        GameStatus.CANCELLED -> SquadGray
        else                 -> SquadOrange
    }

    Surface(
        modifier = Modifier.fillMaxWidth().padding(horizontal = responsiveHorizontalPadding(20.dp)),
        color = if (isCancelled) Color(0xFFF5F5F5) else Color.White,
        shape = RoundedCornerShape(12.dp),
        shadowElevation = if (isCancelled) 0.dp else 2.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Date badge
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .background(dateBadgeBg, RoundedCornerShape(8.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Text(game.month, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = dateBadgeColor)
                Text(game.day, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = dateBadgeColor)
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                // Teams + result (if finished/live)
                if ((isFinished || isLive) && game.homeScore != null && game.awayScore != null) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(game.homeTeamAbbr.ifBlank { game.homeTeam.take(3) }, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = if (isCancelled) SquadGray else MaterialTheme.colorScheme.onSurface)
                        Text("${game.homeScore} – ${game.awayScore}", fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = if (isLive) Color(0xFFD32F2F) else MaterialTheme.colorScheme.onSurface)
                        Text(game.awayTeamAbbr.ifBlank { game.awayTeam.take(3) }, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = if (isCancelled) SquadGray else MaterialTheme.colorScheme.onSurface)
                    }
                } else {
                    Text("${game.homeTeam} vs ${game.awayTeam}", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = if (isCancelled) SquadGray else MaterialTheme.colorScheme.onSurface, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("${game.sport} · ${game.time}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    if (game.venue.isNotBlank()) {
                        Text("·", fontSize = 11.sp, color = SquadGray)
                        Icon(Icons.Outlined.LocationOn, null, tint = SquadGray, modifier = Modifier.size(11.dp))
                        Text(game.venue, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                }
            }

            // Action button
            when {
                isLive -> IconButton(onClick = onManageLive, modifier = Modifier.size(36.dp)) {
                    Icon(Icons.Outlined.PlayArrow, null, tint = Color(0xFFD32F2F), modifier = Modifier.size(20.dp))
                }
                isWarmUp -> IconButton(onClick = onManageLive, modifier = Modifier.size(36.dp)) {
                    Icon(Icons.Outlined.PlayArrow, null, tint = Color(0xFFFFA000), modifier = Modifier.size(20.dp))
                }
                isCancelled -> {}
                else -> IconButton(onClick = onEdit, modifier = Modifier.size(36.dp)) {
                    Icon(Icons.Outlined.Edit, null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}