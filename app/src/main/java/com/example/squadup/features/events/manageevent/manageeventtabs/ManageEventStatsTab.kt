package com.example.squadup.features.events.manageevent.manageeventtabs

import com.example.squadup.features.events.manageevent.*
import com.example.squadup.core.enums.GameStatus
import com.example.squadup.core.utils.scoreLabelRes
import com.example.squadup.core.utils.topPerformerLabelRes

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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.squadup.R
import com.example.squadup.core.enums.SportType
import com.example.squadup.core.ui.theme.*

// scoreLabelRes() e topPerformerLabelRes() estão em core/utils/SportTypeExtensions.kt

@Composable
internal fun StatsTabContent(uiState: ManageEventUiState) {
    val recentFinished = uiState.scheduledGames
        .filter { it.status == GameStatus.FINISHED && it.homeScore != null }
        .sortedByDescending { it.day.toIntOrNull() ?: 0 }
        .take(3)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // 1 — Métricas do evento (sport-aware)
        StatsSectionLabel(stringResource(R.string.stats_section_event))
        Spacer(modifier = Modifier.height(8.dp))
        EventSummaryMetrics(uiState)

        Spacer(modifier = Modifier.height(20.dp))

        // 2 — Melhor performer (sport-aware)
        uiState.bestScorer?.let { scorer ->
            StatsSectionLabel(stringResource(uiState.sportType.topPerformerLabelRes()))
            Spacer(modifier = Modifier.height(8.dp))
            BestScorerCard(scorer, uiState.sportType)
            Spacer(modifier = Modifier.height(20.dp))
        }

        // 3 — Classificação (só para formatos com tabela)
        if (uiState.standings.isNotEmpty()) {
            StatsSectionLabel(stringResource(R.string.manageEvent_standings))
            Spacer(modifier = Modifier.height(8.dp))
            StandingsTable(uiState.standings, uiState.sportHasDraws)
            Spacer(modifier = Modifier.height(20.dp))
        }

        // 4 — Resultados recentes
        if (recentFinished.isNotEmpty()) {
            StatsSectionLabel(stringResource(R.string.stats_recent_results))
            Spacer(modifier = Modifier.height(8.dp))
            RecentResultsSection(recentFinished)
            Spacer(modifier = Modifier.height(20.dp))
        }

        // 5 — Top scorers (sport-aware)
        if (uiState.topScorers.isNotEmpty()) {
            StatsSectionLabel(stringResource(R.string.manageEvent_top_scorers))
            Spacer(modifier = Modifier.height(8.dp))
            TopScorersList(uiState.topScorers, uiState.sportType)
        }

        Spacer(modifier = Modifier.height(88.dp))
    }
}

@Composable
private fun StatsSectionLabel(label: String) {
    Text(label, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SquadTextSecondary, letterSpacing = 0.5.sp)
}

@Composable
private fun EventSummaryMetrics(uiState: ManageEventUiState) {
    val s = uiState.eventSummaryStats
    // Label do score adapta-se ao desporto, tudo o resto é genérico
    val scoreLabel = stringResource(uiState.sportType.scoreLabelRes()).uppercase()
    val metrics = buildList {
        if (!uiState.isSingleMatch) {
            add(Triple(Icons.Outlined.CalendarMonth, stringResource(R.string.manageEvent_games_played), "${uiState.gamesPlayed}"))
        }
        add(Triple(Icons.Outlined.EmojiEvents, scoreLabel,                                       "${s.totalScore}"))
        add(Triple(Icons.Outlined.Flag,         stringResource(R.string.stats_event_infractions), "${s.totalInfractions}"))
    }
    MetricRow(metrics, emptySet())
}

@Composable
private fun BestScorerCard(scorer: ScorerItem, sportType: SportType) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        shape = RoundedCornerShape(12.dp),
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .background(SquadOrangeLight, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Outlined.EmojiEvents, null, tint = SquadOrange, modifier = Modifier.size(28.dp))
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    stringResource(R.string.manageEvent_best_scorer),
                    fontSize = 10.sp, fontWeight = FontWeight.Bold, color = SquadTextSecondary, letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(scorer.name, fontSize = 17.sp, fontWeight = FontWeight.Bold, color = SquadTextPrimary)
                Text(scorer.teamName, fontSize = 12.sp, color = SquadTextSecondary)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("${scorer.score}", fontSize = 30.sp, fontWeight = FontWeight.ExtraBold, color = SquadOrange)
                Text(stringResource(sportType.scoreLabelRes()), fontSize = 10.sp, color = SquadTextSecondary)
            }
        }
    }
}

@Composable
private fun StandingsTable(standings: List<StandingItem>, hasDraws: Boolean) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        shape = RoundedCornerShape(12.dp),
        shadowElevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(vertical = 8.dp)) {
            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 6.dp)) {
                Text("POS", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = SquadTextSecondary, modifier = Modifier.width(32.dp))
                Text(stringResource(R.string.manageEvent_standings_team), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = SquadTextSecondary, modifier = Modifier.weight(1f))
                Text("J",   fontSize = 10.sp, fontWeight = FontWeight.Bold, color = SquadTextSecondary, modifier = Modifier.width(26.dp), textAlign = TextAlign.Center)
                Text("V",   fontSize = 10.sp, fontWeight = FontWeight.Bold, color = SquadTextSecondary, modifier = Modifier.width(26.dp), textAlign = TextAlign.Center)
                if (hasDraws)
                    Text("E", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = SquadTextSecondary, modifier = Modifier.width(26.dp), textAlign = TextAlign.Center)
                Text("D",   fontSize = 10.sp, fontWeight = FontWeight.Bold, color = SquadTextSecondary, modifier = Modifier.width(26.dp), textAlign = TextAlign.Center)
                Text("PTS", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = SquadTextSecondary, modifier = Modifier.width(34.dp), textAlign = TextAlign.Center)
            }
            HorizontalDivider(color = SquadGrayLight)
            standings.forEachIndexed { index, item ->
                StandingRow(item, isFirst = index == 0, hasDraws = hasDraws)
                if (index < standings.lastIndex)
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 14.dp), color = SquadGrayLight)
            }
        }
    }
}

@Composable
private fun StandingRow(item: StandingItem, isFirst: Boolean, hasDraws: Boolean) {
    val accent = if (isFirst) SquadOrange else SquadTextSecondary
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(if (isFirst) SquadOrangeLight.copy(alpha = 0.5f) else Color.Transparent)
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("${item.position}°", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = accent, modifier = Modifier.width(32.dp))
        Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(26.dp).background(if (isFirst) SquadOrange else SquadGrayLight, RoundedCornerShape(4.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(item.teamAbbr.take(3), fontSize = 7.sp, fontWeight = FontWeight.ExtraBold, color = if (isFirst) Color.White else SquadTextSecondary)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(item.teamName, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = SquadTextPrimary, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
        Text("${item.played}", fontSize = 13.sp, color = SquadTextSecondary, modifier = Modifier.width(26.dp), textAlign = TextAlign.Center)
        Text("${item.wins}",   fontSize = 13.sp, color = SquadTextSecondary, modifier = Modifier.width(26.dp), textAlign = TextAlign.Center)
        if (hasDraws)
            Text("${item.draws}", fontSize = 13.sp, color = SquadTextSecondary, modifier = Modifier.width(26.dp), textAlign = TextAlign.Center)
        Text("${item.losses}", fontSize = 13.sp, color = SquadTextSecondary, modifier = Modifier.width(26.dp), textAlign = TextAlign.Center)
        Text("${item.points}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = if (isFirst) SquadOrange else SquadTextPrimary, modifier = Modifier.width(34.dp), textAlign = TextAlign.Center)
    }
}

@Composable
private fun RecentResultsSection(games: List<ScheduledGameItem>) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        shape = RoundedCornerShape(12.dp),
        shadowElevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(vertical = 4.dp)) {
            games.forEachIndexed { index, game ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.background(SquadGrayLight, RoundedCornerShape(6.dp)).padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(game.month, fontSize = 8.sp, fontWeight = FontWeight.Bold, color = SquadGrayDark)
                        Text(game.day,   fontSize = 14.sp, fontWeight = FontWeight.Bold, color = SquadGrayDark)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(game.homeTeamAbbr.ifBlank { game.homeTeam.take(3) }, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = SquadTextPrimary, modifier = Modifier.width(36.dp), textAlign = TextAlign.End)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("${game.homeScore}  –  ${game.awayScore}", fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, color = SquadTextPrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(game.awayTeamAbbr.ifBlank { game.awayTeam.take(3) }, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = SquadTextPrimary, modifier = Modifier.width(36.dp))
                    Spacer(modifier = Modifier.weight(1f))
                    if (game.venue.isNotBlank()) {
                        Icon(Icons.Outlined.LocationOn, null, tint = SquadGray, modifier = Modifier.size(12.dp))
                        Text(game.venue, fontSize = 10.sp, color = SquadGray, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.widthIn(max = 80.dp))
                    }
                }
                if (index < games.lastIndex) HorizontalDivider(modifier = Modifier.padding(horizontal = 14.dp), color = SquadGrayLight)
            }
        }
    }
}

@Composable
private fun TopScorersList(scorers: List<ScorerItem>, sportType: SportType) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        shape = RoundedCornerShape(12.dp),
        shadowElevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(vertical = 8.dp)) {
            scorers.forEachIndexed { index, scorer ->
                TopScorerRow(rank = index + 1, scorer = scorer, sportType = sportType)
                if (index < scorers.lastIndex) {
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 14.dp), color = SquadGrayLight)
                }
            }
        }
    }
}

@Composable
private fun TopScorerRow(rank: Int, scorer: ScorerItem, sportType: SportType) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val rankColor = when (rank) {
            1    -> Color(0xFFFFD700)
            2    -> Color(0xFFC0C0C0)
            3    -> Color(0xFFCD7F32)
            else -> SquadTextSecondary
        }
        Text(
            text = "$rank",
            fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = rankColor,
            modifier = Modifier.width(28.dp)
        )
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(SquadOrangeLight, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = scorer.name.split(" ").mapNotNull { it.firstOrNull()?.toString() }.take(2).joinToString(""),
                fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SquadOrange
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(scorer.name, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = SquadTextPrimary)
            Text(scorer.teamName, fontSize = 12.sp, color = SquadTextSecondary)
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("${scorer.score}", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = SquadOrange)
            Text(stringResource(sportType.scoreLabelRes()), fontSize = 9.sp, color = SquadTextSecondary)
        }
    }
}
