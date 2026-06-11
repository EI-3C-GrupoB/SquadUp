package com.example.squadup.features.events.manageevent.manageeventtabs

import com.example.squadup.core.ui.components.responsiveHorizontalPadding

import androidx.compose.material3.MaterialTheme

import com.example.squadup.features.events.manageevent.*
import com.example.squadup.core.enums.EventFormat
import com.example.squadup.core.enums.GameStatus
import com.example.squadup.core.enums.SportType
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.squadup.R
import com.example.squadup.core.ui.theme.*

@Composable
internal fun StatsTabContent(uiState: ManageEventUiState) {
    val recentFinished = uiState.scheduledGames
        .filter { it.status == GameStatus.FINISHED && it.homeScore != null }
        .sortedByDescending { it.day.toIntOrNull() ?: 0 }
        .take(5)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = responsiveHorizontalPadding(20.dp))
    ) {
        Spacer(Modifier.height(16.dp))

        // ── 1. Resumo do evento (sempre presente) ──────────────────────────
        SectionLabel(stringResource(R.string.stats_section_event))
        Spacer(Modifier.height(8.dp))
        SportSummaryMetrics(uiState)
        Spacer(Modifier.height(20.dp))

        // ── 2. Melhor performer (sempre presente se houver dados) ──────────
        uiState.bestScorer?.let { scorer ->
            SectionLabel(stringResource(uiState.sportType.topPerformerLabelRes()))
            Spacer(Modifier.height(8.dp))
            BestPerformerCard(scorer = scorer, sportType = uiState.sportType)
            Spacer(Modifier.height(20.dp))
        }

        // ── 3. Secções por formato ─────────────────────────────────────────
        when (uiState.eventFormat) {
            EventFormat.SINGLE_MATCH -> SingleMatchSection(uiState, recentFinished)
            EventFormat.LEAGUE       -> LeagueSection(uiState, recentFinished)
            EventFormat.KNOCKOUT     -> KnockoutSection(uiState, recentFinished)
            EventFormat.GROUP_KNOCKOUT -> GroupKnockoutSection(uiState, recentFinished)
            EventFormat.FREE, null   -> FreeSection(uiState, recentFinished)
        }

        Spacer(Modifier.height(88.dp))
    }
}

// ─── Sections by format ──────────────────────────────────────────────────────

@Composable
private fun SingleMatchSection(uiState: ManageEventUiState, recent: List<ScheduledGameItem>) {
    // Single match: just show the result if finished, or "aguarda" if not
    val game = uiState.scheduledGames.firstOrNull()
    if (game != null && game.status == GameStatus.FINISHED && game.homeScore != null) {
        SectionLabel("RESULTADO FINAL")
        Spacer(Modifier.height(8.dp))
        SingleMatchResultCard(game)
        Spacer(Modifier.height(20.dp))
    }
    // Top scorers for this single game
    if (uiState.topScorers.isNotEmpty()) {
        SectionLabel(stringResource(uiState.sportType.topScorersSectionLabel()))
        Spacer(Modifier.height(8.dp))
        TopPerformersList(uiState.topScorers, uiState.sportType)
        Spacer(Modifier.height(20.dp))
    }
    // Discipline (football/futsal)
    if (uiState.showDisciplineSection) {
        DisciplineSection(uiState)
    }
}

@Composable
private fun LeagueSection(uiState: ManageEventUiState, recent: List<ScheduledGameItem>) {
    // Standings table
    if (uiState.showLeagueTable) {
        SectionLabel(stringResource(R.string.stats_section_standings))
        Spacer(Modifier.height(8.dp))
        StandingsTable(uiState)
        Spacer(Modifier.height(20.dp))
    }
    // Recent results
    if (recent.isNotEmpty()) {
        SectionLabel(stringResource(R.string.stats_section_results))
        Spacer(Modifier.height(8.dp))
        RecentResultsSection(recent)
        Spacer(Modifier.height(20.dp))
    }
    // Top scorers / performers
    if (uiState.topScorers.isNotEmpty()) {
        SectionLabel(stringResource(uiState.sportType.topScorersSectionLabel()))
        Spacer(Modifier.height(8.dp))
        TopPerformersList(uiState.topScorers, uiState.sportType)
        Spacer(Modifier.height(20.dp))
    }
    // Discipline (football/futsal/basketball)
    if (uiState.showDisciplineSection) {
        DisciplineSection(uiState)
    }
}

@Composable
private fun KnockoutSection(uiState: ManageEventUiState, recent: List<ScheduledGameItem>) {
    // Show all finished results (bracket-style results list)
    if (recent.isNotEmpty()) {
        SectionLabel(stringResource(R.string.stats_knockout_results))
        Spacer(Modifier.height(8.dp))
        RecentResultsSection(recent)
        Spacer(Modifier.height(20.dp))
    }
    // Top performers
    if (uiState.topScorers.isNotEmpty()) {
        SectionLabel(stringResource(uiState.sportType.topScorersSectionLabel()))
        Spacer(Modifier.height(8.dp))
        TopPerformersList(uiState.topScorers, uiState.sportType)
        Spacer(Modifier.height(20.dp))
    }
    if (uiState.showDisciplineSection) DisciplineSection(uiState)
}

@Composable
private fun GroupKnockoutSection(uiState: ManageEventUiState, recent: List<ScheduledGameItem>) {
    // Group standings
    if (uiState.showLeagueTable) {
        SectionLabel(stringResource(R.string.stats_group_stage))
        Spacer(Modifier.height(8.dp))
        StandingsTable(uiState)
        Spacer(Modifier.height(20.dp))
    }
    // Knockout results (all finished games, sorted by date desc)
    val knockoutResults = recent.take(5)
    if (knockoutResults.isNotEmpty()) {
        SectionLabel(stringResource(R.string.stats_knockout_results))
        Spacer(Modifier.height(8.dp))
        RecentResultsSection(knockoutResults)
        Spacer(Modifier.height(20.dp))
    }
    // Top scorers
    if (uiState.topScorers.isNotEmpty()) {
        SectionLabel(stringResource(uiState.sportType.topScorersSectionLabel()))
        Spacer(Modifier.height(8.dp))
        TopPerformersList(uiState.topScorers, uiState.sportType)
        Spacer(Modifier.height(20.dp))
    }
    if (uiState.showDisciplineSection) DisciplineSection(uiState)
}

@Composable
private fun FreeSection(uiState: ManageEventUiState, recent: List<ScheduledGameItem>) {
    if (recent.isNotEmpty()) {
        SectionLabel(stringResource(R.string.stats_section_results))
        Spacer(Modifier.height(8.dp))
        RecentResultsSection(recent)
        Spacer(Modifier.height(20.dp))
    }
    if (uiState.topScorers.isNotEmpty()) {
        SectionLabel(stringResource(uiState.sportType.topScorersSectionLabel()))
        Spacer(Modifier.height(8.dp))
        TopPerformersList(uiState.topScorers, uiState.sportType)
        Spacer(Modifier.height(20.dp))
    }
    if (uiState.showDisciplineSection) DisciplineSection(uiState)
}

// ─── Sport-specific summary metrics ──────────────────────────────────────────

@Composable
private fun SportSummaryMetrics(uiState: ManageEventUiState) {
    val s = uiState.eventSummaryStats
    val scoreLabel = stringResource(uiState.sportType.scoreLabelRes()).uppercase()

    val metrics: List<Triple<ImageVector, String, String>> = when (uiState.sportType) {
        SportType.SOCCER, SportType.FUTSAL -> buildList {
            if (!uiState.isSingleMatch) add(Triple(Icons.Outlined.CalendarMonth, "JOGOS", "${uiState.gamesPlayed}"))
            add(Triple(Icons.Outlined.EmojiEvents, "GOLOS", "${s.totalScore}"))
            add(Triple(Icons.Outlined.Style, stringResource(R.string.stats_yellow_cards), "${s.yellowCards}"))
            add(Triple(Icons.Outlined.Block, stringResource(R.string.stats_red_cards), "${s.redCards}"))
        }
        SportType.BASKETBALL -> buildList {
            if (!uiState.isSingleMatch) add(Triple(Icons.Outlined.CalendarMonth, "JOGOS", "${uiState.gamesPlayed}"))
            add(Triple(Icons.Outlined.EmojiEvents, "PONTOS", "${s.totalScore}"))
            add(Triple(Icons.Outlined.PanTool, stringResource(R.string.stats_personal_fouls), "${s.personalFouls}"))
            add(Triple(Icons.Outlined.Warning, stringResource(R.string.stats_technical_fouls), "${s.technicalFouls}"))
        }
        SportType.VOLLEYBALL -> buildList {
            if (!uiState.isSingleMatch) add(Triple(Icons.Outlined.CalendarMonth, "JOGOS", "${uiState.gamesPlayed}"))
            add(Triple(Icons.Outlined.EmojiEvents, "PONTOS", "${s.totalScore}"))
            add(Triple(Icons.Outlined.ViewStream, stringResource(R.string.stats_sets_played), "${s.totalSetsWon}"))
            add(Triple(Icons.Outlined.Flag, stringResource(R.string.stats_event_infractions), "${s.totalInfractions}"))
        }
        SportType.PADDLE -> buildList {
            if (!uiState.isSingleMatch) add(Triple(Icons.Outlined.CalendarMonth, "JOGOS", "${uiState.gamesPlayed}"))
            add(Triple(Icons.Outlined.EmojiEvents, "VITÓRIAS", "${s.totalScore}"))
            val pairs = uiState.standings.size
            if (pairs > 0) add(Triple(Icons.Outlined.Groups, "DUPLAS", "$pairs"))
        }
    }

    MetricRow(metrics, emptySet())

    // Soccer/Futsal: clean sheets badge
    if ((uiState.sportType == SportType.SOCCER || uiState.sportType == SportType.FUTSAL) && s.cleanSheets > 0) {
        Spacer(Modifier.height(8.dp))
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color(0xFFE8F5E9),
            shape = RoundedCornerShape(8.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Outlined.Shield, null, tint = Color(0xFF2E7D32), modifier = Modifier.size(16.dp))
                Text(
                    "${s.cleanSheets} ${stringResource(R.string.stats_clean_sheets)}",
                    fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF2E7D32)
                )
            }
        }
    }
}

// ─── Discipline section ───────────────────────────────────────────────────────

@Composable
private fun DisciplineSection(uiState: ManageEventUiState) {
    val s = uiState.eventSummaryStats
    val hasData = when (uiState.sportType) {
        SportType.SOCCER, SportType.FUTSAL -> s.yellowCards > 0 || s.redCards > 0
        SportType.BASKETBALL -> s.personalFouls > 0 || s.technicalFouls > 0
        else -> s.totalInfractions > 0
    }
    if (!hasData) return

    val sectionLabel = if (uiState.sportType == SportType.BASKETBALL)
        stringResource(R.string.stats_section_discipline_basketball)
    else
        stringResource(R.string.stats_section_discipline)

    SectionLabel(sectionLabel)
    Spacer(Modifier.height(8.dp))

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(12.dp),
        shadowElevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            when (uiState.sportType) {
                SportType.SOCCER, SportType.FUTSAL -> {
                    DisciplineRow(
                        icon = Icons.Outlined.Style,
                        iconColor = Color(0xFFFFC107),
                        label = stringResource(R.string.infraction_yellow_card),
                        value = "${s.yellowCards}"
                    )
                    if (s.yellowCards > 0 && s.redCards > 0) HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), color = SquadGrayLight)
                    if (s.redCards > 0) DisciplineRow(
                        icon = Icons.Outlined.Block,
                        iconColor = Color(0xFFD32F2F),
                        label = stringResource(R.string.infraction_red_card),
                        value = "${s.redCards}"
                    )
                }
                SportType.BASKETBALL -> {
                    DisciplineRow(
                        icon = Icons.Outlined.PanTool,
                        iconColor = SquadOrange,
                        label = stringResource(R.string.infraction_personal_foul),
                        value = "${s.personalFouls}"
                    )
                    if (s.technicalFouls > 0) {
                        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), color = SquadGrayLight)
                        DisciplineRow(
                            icon = Icons.Outlined.Warning,
                            iconColor = Color(0xFFD32F2F),
                            label = stringResource(R.string.infraction_technical_foul),
                            value = "${s.technicalFouls}"
                        )
                    }
                }
                else -> {
                    DisciplineRow(
                        icon = Icons.Outlined.Flag,
                        iconColor = SquadOrange,
                        label = stringResource(R.string.stats_event_infractions),
                        value = "${s.totalInfractions}"
                    )
                }
            }

            // Top infractors
            if (s.topInfractors.isNotEmpty()) {
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = SquadGrayLight)
                Text(
                    stringResource(R.string.stats_top_infractors),
                    fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant, letterSpacing = 0.5.sp
                )
                Spacer(Modifier.height(4.dp))
                s.topInfractors.take(3).forEachIndexed { i, player ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("${i + 1}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.width(20.dp))
                        Text(player.name, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.weight(1f))
                        Text("${player.score}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = SquadOrange)
                    }
                }
            }
        }
    }
    Spacer(Modifier.height(20.dp))
}

@Composable
private fun DisciplineRow(icon: ImageVector, iconColor: Color, label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, null, tint = iconColor, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(10.dp))
        Text(label, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.weight(1f))
        Text(value, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = iconColor)
    }
}

// ─── Single match result card ─────────────────────────────────────────────────

@Composable
private fun SingleMatchResultCard(game: ScheduledGameItem) {
    val homeWon = (game.homeScore ?: 0) > (game.awayScore ?: 0)
    val awayWon = (game.awayScore ?: 0) > (game.homeScore ?: 0)

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(14.dp),
        shadowElevation = 3.dp
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                    Box(
                        modifier = Modifier.size(52.dp)
                            .background(if (homeWon) SquadOrange else SquadGrayLight, RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(game.homeTeamAbbr.ifBlank { game.homeTeam.take(2) }, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold,
                            color = if (homeWon) Color.White else MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Spacer(Modifier.height(6.dp))
                    Text(game.homeTeam, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface, textAlign = TextAlign.Center, maxLines = 2)
                    if (homeWon) {
                        Spacer(Modifier.height(4.dp))
                        Surface(color = SquadOrange, shape = RoundedCornerShape(4.dp)) {
                            Text("VENCEDOR", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = Color.White,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                        }
                    }
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(horizontal = responsiveHorizontalPadding(12.dp))) {
                    Text("${game.homeScore}  —  ${game.awayScore}", fontSize = 32.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onSurface)
                    Text("FINAL", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant, letterSpacing = 1.sp)
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                    Box(
                        modifier = Modifier.size(52.dp)
                            .background(if (awayWon) SquadOrange else SquadGrayLight, RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(game.awayTeamAbbr.ifBlank { game.awayTeam.take(2) }, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold,
                            color = if (awayWon) Color.White else MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Spacer(Modifier.height(6.dp))
                    Text(game.awayTeam, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface, textAlign = TextAlign.Center, maxLines = 2)
                    if (awayWon) {
                        Spacer(Modifier.height(4.dp))
                        Surface(color = SquadOrange, shape = RoundedCornerShape(4.dp)) {
                            Text("VENCEDOR", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = Color.White,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                        }
                    }
                }
            }

            if (!homeWon && !awayWon) {
                Spacer(Modifier.height(8.dp))
                Surface(color = SquadGrayLight, shape = RoundedCornerShape(6.dp)) {
                    Text("EMPATE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp))
                }
            }
        }
    }
}

// ─── Standings table (sport-aware columns) ────────────────────────────────────

@Composable
private fun StandingsTable(uiState: ManageEventUiState) {
    val hasDraws = uiState.sportHasDraws
    val showGoals = uiState.standingsShowGoals
    val showSets = uiState.standingsShowSets
    val ptsLabel = uiState.standingsPtsLabel

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(12.dp),
        shadowElevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(vertical = 8.dp)) {
            // Header
            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 6.dp)) {
                Text("#",  fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.width(24.dp))
                Text("EQUIPA", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.weight(1f))
                Text("J",  fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.width(22.dp), textAlign = TextAlign.Center)
                Text("V",  fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.width(22.dp), textAlign = TextAlign.Center)
                if (hasDraws)
                    Text("E", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.width(22.dp), textAlign = TextAlign.Center)
                Text("D",  fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.width(22.dp), textAlign = TextAlign.Center)
                if (showGoals) {
                    Text(stringResource(R.string.stats_goals_for),    fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.width(24.dp), textAlign = TextAlign.Center)
                    Text(stringResource(R.string.stats_goals_against), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.width(24.dp), textAlign = TextAlign.Center)
                    Text(stringResource(R.string.stats_goal_diff),     fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.width(24.dp), textAlign = TextAlign.Center)
                }
                if (showSets) {
                    Text(stringResource(R.string.stats_sets_won), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.width(24.dp), textAlign = TextAlign.Center)
                }
                Text(ptsLabel, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.width(28.dp), textAlign = TextAlign.Center)
            }
            HorizontalDivider(color = SquadGrayLight)
            uiState.standings.forEachIndexed { index, item ->
                StandingRow(item, isFirst = index == 0, hasDraws = hasDraws, showGoals = showGoals, showSets = showSets, ptsLabel = ptsLabel)
                if (index < uiState.standings.lastIndex)
                    HorizontalDivider(modifier = Modifier.padding(horizontal = responsiveHorizontalPadding(10.dp)), color = SquadGrayLight)
            }
        }
    }
}

@Composable
private fun StandingRow(
    item: StandingItem,
    isFirst: Boolean,
    hasDraws: Boolean,
    showGoals: Boolean,
    showSets: Boolean,
    ptsLabel: String
) {
    val accent = if (isFirst) SquadOrange else MaterialTheme.colorScheme.onSurfaceVariant
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(if (isFirst) SquadOrangeLight.copy(alpha = 0.4f) else Color.Transparent)
            .padding(horizontal = 10.dp, vertical = 9.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("${item.position}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = accent, modifier = Modifier.width(24.dp))
        Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Box(
                modifier = Modifier.size(22.dp).background(if (isFirst) SquadOrange else SquadGrayLight, RoundedCornerShape(4.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(item.teamAbbr.take(3), fontSize = 6.sp, fontWeight = FontWeight.ExtraBold, color = if (isFirst) Color.White else MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Text(item.teamName, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
        Text("${item.played}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.width(22.dp), textAlign = TextAlign.Center)
        Text("${item.wins}",   fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.width(22.dp), textAlign = TextAlign.Center)
        if (hasDraws)
            Text("${item.draws}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.width(22.dp), textAlign = TextAlign.Center)
        Text("${item.losses}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.width(22.dp), textAlign = TextAlign.Center)
        if (showGoals) {
            Text("${item.goalsFor}",  fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.width(24.dp), textAlign = TextAlign.Center)
            Text("${item.goalsAgainst}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.width(24.dp), textAlign = TextAlign.Center)
            val diffColor = when {
                item.goalDiff > 0  -> Color(0xFF2E7D32)
                item.goalDiff < 0  -> Color(0xFFD32F2F)
                else               -> MaterialTheme.colorScheme.onSurfaceVariant
            }
            Text(
                if (item.goalDiff > 0) "+${item.goalDiff}" else "${item.goalDiff}",
                fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = diffColor,
                modifier = Modifier.width(24.dp), textAlign = TextAlign.Center
            )
        }
        if (showSets) {
            Text("${item.setsWon}-${item.setsLost}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.width(24.dp), textAlign = TextAlign.Center)
        }
        Text("${item.points}", fontSize = 13.sp, fontWeight = FontWeight.ExtraBold,
            color = if (isFirst) SquadOrange else MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.width(28.dp), textAlign = TextAlign.Center)
    }
}

// ─── Recent results ───────────────────────────────────────────────────────────

@Composable
private fun RecentResultsSection(games: List<ScheduledGameItem>) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(12.dp),
        shadowElevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(vertical = 4.dp)) {
            games.forEachIndexed { index, game ->
                val homeWon = (game.homeScore ?: 0) > (game.awayScore ?: 0)
                val awayWon = (game.awayScore ?: 0) > (game.homeScore ?: 0)
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
                    Spacer(Modifier.width(10.dp))
                    Text(
                        game.homeTeamAbbr.ifBlank { game.homeTeam.take(3) },
                        fontSize = 13.sp, fontWeight = if (homeWon) FontWeight.ExtraBold else FontWeight.Normal,
                        color = if (homeWon) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.width(36.dp), textAlign = TextAlign.End
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("${game.homeScore}  –  ${game.awayScore}", fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onSurface)
                    Spacer(Modifier.width(8.dp))
                    Text(
                        game.awayTeamAbbr.ifBlank { game.awayTeam.take(3) },
                        fontSize = 13.sp, fontWeight = if (awayWon) FontWeight.ExtraBold else FontWeight.Normal,
                        color = if (awayWon) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.width(36.dp)
                    )
                    Spacer(Modifier.weight(1f))
                    if (game.venue.isNotBlank()) {
                        Icon(Icons.Outlined.LocationOn, null, tint = SquadGray, modifier = Modifier.size(12.dp))
                        Text(game.venue, fontSize = 10.sp, color = SquadGray, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.widthIn(max = 80.dp))
                    }
                }
                if (index < games.lastIndex) HorizontalDivider(modifier = Modifier.padding(horizontal = responsiveHorizontalPadding(14.dp)), color = SquadGrayLight)
            }
        }
    }
}

// ─── Best performer card ──────────────────────────────────────────────────────

@Composable
private fun BestPerformerCard(scorer: ScorerItem, sportType: SportType) {
    val sportColor = sportType.accentColor()
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(12.dp),
        shadowElevation = 2.dp
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(52.dp).background(sportColor.copy(alpha = 0.12f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Outlined.EmojiEvents, null, tint = sportColor, modifier = Modifier.size(28.dp))
            }
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    stringResource(sportType.topPerformerLabelRes()),
                    fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant, letterSpacing = 0.5.sp
                )
                Spacer(Modifier.height(4.dp))
                Text(scorer.name, fontSize = 17.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                if (scorer.teamName.isNotBlank())
                    Text(scorer.teamName, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("${scorer.score}", fontSize = 30.sp, fontWeight = FontWeight.ExtraBold, color = sportColor)
                Text(stringResource(sportType.scoreLabelRes()), fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

// ─── Top performers list ──────────────────────────────────────────────────────

@Composable
private fun TopPerformersList(scorers: List<ScorerItem>, sportType: SportType) {
    val sportColor = sportType.accentColor()
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(12.dp),
        shadowElevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(vertical = 8.dp)) {
            scorers.take(10).forEachIndexed { index, scorer ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 9.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val rankColor = when (index + 1) {
                        1 -> Color(0xFFFFD700)
                        2 -> Color(0xFFC0C0C0)
                        3 -> Color(0xFFCD7F32)
                        else -> MaterialTheme.colorScheme.onSurfaceVariant
                    }
                    Text("${index + 1}", fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, color = rankColor, modifier = Modifier.width(26.dp))
                    Box(
                        modifier = Modifier.size(34.dp).background(sportColor.copy(alpha = 0.1f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            scorer.name.split(" ").mapNotNull { it.firstOrNull()?.toString() }.take(2).joinToString(""),
                            fontSize = 10.sp, fontWeight = FontWeight.Bold, color = sportColor
                        )
                    }
                    Spacer(Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(scorer.name, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                        if (scorer.teamName.isNotBlank())
                            Text(scorer.teamName, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("${scorer.score}", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = sportColor)
                        Text(stringResource(sportType.scoreLabelRes()), fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                if (index < scorers.take(10).lastIndex)
                    HorizontalDivider(modifier = Modifier.padding(horizontal = responsiveHorizontalPadding(14.dp)), color = SquadGrayLight)
            }
        }
    }
}

// ─── Helpers ──────────────────────────────────────────────────────────────────

@Composable
private fun SectionLabel(label: String) {
    Text(label, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant, letterSpacing = 0.6.sp)
}

private fun SportType.accentColor(): Color = when (this) {
    SportType.SOCCER     -> Color(0xFF2F9D73)
    SportType.FUTSAL     -> Color(0xFF1A6B3C)
    SportType.BASKETBALL -> Color(0xFFD4611A)
    SportType.VOLLEYBALL -> Color(0xFF9B27AF)
    SportType.PADDLE     -> Color(0xFF1A7BD4)
}

private fun SportType.topScorersSectionLabel(): Int = when (this) {
    SportType.SOCCER, SportType.FUTSAL -> R.string.manageEvent_top_scorers
    SportType.BASKETBALL               -> R.string.manageEvent_top_scorers
    SportType.VOLLEYBALL               -> R.string.stats_section_top_performers
    SportType.PADDLE                   -> R.string.stats_section_top_performers
}