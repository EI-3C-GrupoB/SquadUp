@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
package com.example.squadup.features.events.livematch

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.squadup.core.utils.clickableNoRipple
import com.example.squadup.R
import com.example.squadup.core.enums.SportType
import com.example.squadup.core.utils.scoreLabelRes
import com.example.squadup.core.ui.components.AppHeader
import com.example.squadup.core.ui.components.AppNavBar
import com.example.squadup.core.ui.theme.*

@Composable
fun LiveMatchContent(
    uiState: LiveMatchUiState,
    onBackClick: () -> Unit,
    onEndMatch: () -> Unit,
    onStopTimer: () -> Unit,
    onResumeTimer: () -> Unit,
    onTabChange: (LiveMatchTab) -> Unit,
    onShowGoalForm: (Boolean) -> Unit,
    onShowInfractionForm: (Boolean) -> Unit,
    onShowSubstitutionForm: (Boolean) -> Unit,
    onRecordGoal: (Boolean, String, String) -> Unit,
    onRecordInfraction: (Boolean, String, String) -> Unit,
    onRecordSubstitution: (Boolean, String, String) -> Unit,
    onRecordTimeout: (Boolean) -> Unit,
    selectedRoute: String = "",
    onNavItemClick: (String) -> Unit = {},
) {
    val isLive      = uiState.phase == LiveMatchPhase.LIVE
    val isOrganizer = uiState.isOrganizer

    Scaffold(
        topBar = {
            AppHeader(
                showLogo = false,
                showBackButton = true,
                onBackClick = onBackClick,
                title = if (isLive) stringResource(R.string.liveMatch_live_title)
                        else stringResource(R.string.liveMatch_finished_title)
            )
        },
        bottomBar = {
            Column {
            if (isLive && isOrganizer) {
                Surface(shadowElevation = 8.dp) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = if (uiState.isTimerRunning) onStopTimer else onResumeTimer,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, SquadGray)
                        ) {
                            Icon(
                                if (uiState.isTimerRunning) Icons.Outlined.Pause else Icons.Outlined.PlayArrow,
                                null, modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                if (uiState.isTimerRunning) stringResource(R.string.liveMatch_stop_timer)
                                else stringResource(R.string.liveMatch_resume_timer),
                                fontSize = 13.sp
                            )
                        }
                        Button(
                            onClick = onEndMatch,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = SquadError,
                                contentColor = Color.White
                            )
                        ) {
                            Icon(Icons.Outlined.Flag, null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(stringResource(R.string.liveMatch_end_match), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
            AppNavBar(selectedRoute = selectedRoute, onItemClick = onNavItemClick)
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(SquadBackground)
                .padding(innerPadding)
        ) {
            // ── Score Header ──────────────────────────────────────────────────
            ScoreHeader(uiState)

            // ── Tab toggle ────────────────────────────────────────────────────
            LiveMatchTabRow(
                selectedTab = uiState.selectedTab,
                onTabChange = onTabChange
            )

            HorizontalDivider(
                modifier = Modifier.fillMaxWidth(0.9f).align(Alignment.CenterHorizontally),
                color = SquadGrayLight
            )

            // ── Content ───────────────────────────────────────────────────────
            when (uiState.selectedTab) {
                LiveMatchTab.EVENTS -> MatchEventsTab(
                    uiState = uiState,
                    isOrganizer = isOrganizer,
                    onShowGoalForm = onShowGoalForm,
                    onShowInfractionForm = onShowInfractionForm,
                    onShowSubstitutionForm = onShowSubstitutionForm,
                    onRecordTimeout = onRecordTimeout,
                )
                LiveMatchTab.STATS -> MatchStatsTab(uiState = uiState)
            }
        }
    }

    // ── Forms ─────────────────────────────────────────────────────────────────
    if (uiState.showGoalForm) {
        GoalFormSheet(
            uiState = uiState,
            onDismiss = { onShowGoalForm(false) },
            onSave = onRecordGoal
        )
    }
    if (uiState.showInfractionForm) {
        InfractionFormSheet(
            uiState = uiState,
            onDismiss = { onShowInfractionForm(false) },
            onSave = onRecordInfraction
        )
    }
    if (uiState.showSubstitutionForm) {
        SubstitutionFormSheet(
            uiState = uiState,
            onDismiss = { onShowSubstitutionForm(false) },
            onSave = onRecordSubstitution
        )
    }
}

// ─── Score Header ─────────────────────────────────────────────────────────────

@Composable
private fun ScoreHeader(uiState: LiveMatchUiState) {
    val isLive = uiState.phase == LiveMatchPhase.LIVE
    val bgColor = if (isLive) SquadOrange else SquadGrayDark

    Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp)) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = bgColor,
            shape = RoundedCornerShape(16.dp),
            shadowElevation = 4.dp
        ) {
            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
                // Status row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (isLive) {
                        Box(modifier = Modifier.size(8.dp).background(Color.White, CircleShape))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("LIVE", fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                    } else {
                        Text(stringResource(R.string.liveMatch_finished_title).uppercase(), fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    if (isLive) {
                        Text(uiState.timerDisplay, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Score row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                        Box(
                            modifier = Modifier.size(48.dp).background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(uiState.homeTeamAbbr, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(uiState.homeTeamName, fontSize = 10.sp, color = Color.White.copy(alpha = 0.9f), fontWeight = FontWeight.Medium)
                    }
                    Text(
                        "${uiState.homeScore}  –  ${uiState.awayScore}",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                        Box(
                            modifier = Modifier.size(48.dp).background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(uiState.awayTeamAbbr, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(uiState.awayTeamName, fontSize = 10.sp, color = Color.White.copy(alpha = 0.9f), fontWeight = FontWeight.Medium)
                    }
                }
            }
        }
    }
}

// ─── Tab Row ──────────────────────────────────────────────────────────────────

@Composable
private fun LiveMatchTabRow(
    selectedTab: LiveMatchTab,
    onTabChange: (LiveMatchTab) -> Unit
) {
    val tabs = LiveMatchTab.values()
    val n = tabs.size
    val selectedIndex = selectedTab.ordinal

    val horizontalBias by animateFloatAsState(
        targetValue = if (n <= 1) 0f else -1f + selectedIndex * (2f / (n - 1)),
        animationSpec = tween(durationMillis = 250),
        label = "LiveTabIndicator"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp)
            .height(44.dp)
            .background(Color(0xFFE0E0E0), RoundedCornerShape(12.dp))
            .padding(4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(1f / n)
                .fillMaxHeight()
                .align(BiasAlignment(horizontalBias, 0f))
                .background(SquadOrange, RoundedCornerShape(10.dp))
        )
        Row(modifier = Modifier.fillMaxSize()) {
            tabs.forEach { tab ->
                val isSelected = selectedTab == tab
                val labelRes = if (tab == LiveMatchTab.EVENTS) R.string.liveMatch_tab_events else R.string.liveMatch_tab_stats
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clickableNoRipple { onTabChange(tab) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        stringResource(labelRes),
                        fontSize = 13.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) Color.White else SquadTextSecondary
                    )
                }
            }
        }
    }
}

// ─── Events Tab ───────────────────────────────────────────────────────────────

@Composable
private fun MatchEventsTab(
    uiState: LiveMatchUiState,
    isOrganizer: Boolean,
    onShowGoalForm: (Boolean) -> Unit,
    onShowInfractionForm: (Boolean) -> Unit,
    onShowSubstitutionForm: (Boolean) -> Unit,
    onRecordTimeout: (Boolean) -> Unit,
) {
    val isLive = uiState.phase == LiveMatchPhase.LIVE

    Column(modifier = Modifier.fillMaxSize()) {
        // Action buttons (só organizador no LIVE)
        if (isLive && isOrganizer) {
            ActionButtonsRow(
                sportType = uiState.sportType,
                onGoalClick = { onShowGoalForm(true) },
                onInfractionClick = { onShowInfractionForm(true) },
                onSubClick = { onShowSubstitutionForm(true) },
                onTimeoutClick = { onRecordTimeout(true) }
            )
        }

        // Events list
        if (uiState.events.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Outlined.SportsSoccer, null, tint = SquadGray, modifier = Modifier.size(48.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(stringResource(R.string.liveMatch_no_events), fontSize = 14.sp, color = SquadTextSecondary)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(uiState.events, key = { it.id }) { event ->
                    MatchEventRow(event)
                }
            }
        }
    }
}

@Composable
private fun ActionButtonsRow(
    sportType: SportType,
    onGoalClick: () -> Unit,
    onInfractionClick: () -> Unit,
    onSubClick: () -> Unit,
    onTimeoutClick: () -> Unit
) {
    val goalLabel = when (sportType) {
        SportType.SOCCER, SportType.FUTSAL -> R.string.liveMatch_action_goal
        SportType.BASKETBALL               -> R.string.liveMatch_action_score
        else                               -> R.string.liveMatch_action_point
    }
    val infractionLabel = when (sportType) {
        SportType.SOCCER, SportType.FUTSAL -> R.string.liveMatch_action_card
        else                               -> R.string.liveMatch_action_fault
    }
    val goalIcon = when (sportType) {
        SportType.SOCCER, SportType.FUTSAL -> Icons.Outlined.SportsSoccer
        SportType.BASKETBALL               -> Icons.Outlined.SportsBasketball
        else                               -> Icons.Outlined.EmojiEvents
    }

    Surface(shadowElevation = 2.dp, color = Color.White) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            ActionButton(icon = goalIcon,       labelRes = goalLabel,                        color = SquadOrange,             onClick = onGoalClick,       modifier = Modifier.weight(1f))
            ActionButton(icon = Icons.Outlined.Flag,        labelRes = infractionLabel,      color = Color(0xFFE53935),       onClick = onInfractionClick, modifier = Modifier.weight(1f))
            ActionButton(icon = Icons.Outlined.SwapHoriz,   labelRes = R.string.liveMatch_action_sub,     color = Color(0xFF1565C0), onClick = onSubClick,        modifier = Modifier.weight(1f))
            ActionButton(icon = Icons.Outlined.Timer,       labelRes = R.string.liveMatch_action_timeout, color = Color(0xFF2E7D32), onClick = onTimeoutClick,    modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun ActionButton(
    icon: ImageVector,
    labelRes: Int,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier,
        color = color.copy(alpha = 0.1f),
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, null, tint = color, modifier = Modifier.size(22.dp))
            Spacer(modifier = Modifier.height(4.dp))
            Text(stringResource(labelRes), fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = color)
        }
    }
}

@Composable
private fun MatchEventRow(event: MatchEventItem) {
    val (icon, color) = when (event.type) {
        MatchEventType.SCORE        -> Icons.Outlined.EmojiEvents  to SquadOrange
        MatchEventType.INFRACTION   -> Icons.Outlined.Flag          to Color(0xFFE53935)
        MatchEventType.SUBSTITUTION -> Icons.Outlined.SwapHoriz     to Color(0xFF1565C0)
        MatchEventType.TIMEOUT      -> Icons.Outlined.Timer         to Color(0xFF2E7D32)
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        shape = RoundedCornerShape(10.dp),
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Minute badge
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(color.copy(alpha = 0.1f), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text("${event.minute}'", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = color)
            }
            Spacer(modifier = Modifier.width(10.dp))
            Icon(icon, null, tint = color, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                if (event.playerName.isNotBlank()) {
                    Text(event.playerName, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = SquadTextPrimary)
                }
                Text(event.description, fontSize = 12.sp, color = SquadTextSecondary)
            }
            // Team badge
            Box(
                modifier = Modifier
                    .background(if (event.isHome) SquadOrangeLight else SquadGrayLight, RoundedCornerShape(4.dp))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(event.teamAbbr, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = if (event.isHome) SquadOrange else SquadTextSecondary)
            }
        }
    }
}

// ─── Stats Tab ────────────────────────────────────────────────────────────────

@Composable
private fun MatchStatsTab(uiState: LiveMatchUiState) {
    val s = uiState.homeStats
    val a = uiState.awayStats
    val events = uiState.events

    // Infrações derivadas dos eventos registados (separadas por tipo)
    val infractionEvents = events.filter { it.type == MatchEventType.INFRACTION }
    val infractionTypes  = infractionEvents.map { it.description }.distinct()

    val rows = buildList {
        add(Triple(stringResource(R.string.liveMatch_stat_shots),      s.shots,      a.shots))
        add(Triple(stringResource(R.string.liveMatch_stat_shots_goal), s.shotsOnGoal,a.shotsOnGoal))
        add(Triple(stringResource(R.string.liveMatch_stat_corners),    s.corners,    a.corners))
        add(Triple(stringResource(R.string.liveMatch_stat_fouls),      s.fouls,      a.fouls))
        // Infrações por tipo dos eventos registados
        if (infractionTypes.isEmpty()) {
            add(Triple(stringResource(R.string.stats_event_infractions), 0, 0))
        } else {
            infractionTypes.forEach { type ->
                val hCount = infractionEvents.count { it.isHome && it.description == type }
                val aCount = infractionEvents.count { !it.isHome && it.description == type }
                add(Triple(type.split(" ").joinToString(" ") { w -> w.lowercase().replaceFirstChar { it.uppercase() } }, hCount, aCount))
            }
        }
        add(Triple(stringResource(R.string.liveMatch_stat_offsides), s.offsides, a.offsides))
        add(Triple(stringResource(R.string.liveMatch_stat_saves),    s.saves,    a.saves))
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Surface(modifier = Modifier.fillMaxWidth(), color = Color.White, shape = RoundedCornerShape(12.dp), shadowElevation = 2.dp) {
            Column {
                Row(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
                    Box(modifier = Modifier.size(32.dp).background(SquadOrange, RoundedCornerShape(6.dp)), contentAlignment = Alignment.Center) {
                        Text(uiState.homeTeamAbbr, fontSize = 9.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Box(modifier = Modifier.size(32.dp).background(SquadGrayLight, RoundedCornerShape(6.dp)), contentAlignment = Alignment.Center) {
                        Text(uiState.awayTeamAbbr, fontSize = 9.sp, fontWeight = FontWeight.ExtraBold, color = SquadTextSecondary)
                    }
                }
                HorizontalDivider(color = SquadGrayLight)
                rows.forEachIndexed { index, (label, homeVal, awayVal) ->
                    StatCompareRow(label, homeVal, awayVal)
                    if (index < rows.lastIndex) HorizontalDivider(modifier = Modifier.padding(horizontal = 12.dp), color = SquadGrayLight)
                }
            }
        }
    }
}

@Composable
private fun StatCompareRow(label: String, homeVal: Int, awayVal: Int) {
    val total = (homeVal + awayVal).coerceAtLeast(1)
    val homeRatio = homeVal.toFloat() / total

    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("$homeVal", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = SquadOrange, modifier = Modifier.width(28.dp))
        Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(label, fontSize = 11.sp, color = SquadTextSecondary)
            Spacer(modifier = Modifier.height(4.dp))
            // Bar
            Box(modifier = Modifier.fillMaxWidth().height(4.dp).background(SquadGrayLight, RoundedCornerShape(999.dp))) {
                Box(modifier = Modifier.fillMaxWidth(homeRatio).fillMaxHeight().background(SquadOrange, RoundedCornerShape(999.dp)))
            }
        }
        Text("$awayVal", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = SquadGrayDark, modifier = Modifier.width(28.dp), textAlign = androidx.compose.ui.text.style.TextAlign.End)
    }
}
