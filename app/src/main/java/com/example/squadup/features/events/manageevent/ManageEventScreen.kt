package com.example.squadup.features.events.manageevent

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.squadup.R
import com.example.squadup.core.ui.components.AppHeader
import com.example.squadup.core.ui.components.AppNavBar
import com.example.squadup.core.ui.theme.*
import com.example.squadup.core.utils.AppLanguage
import com.example.squadup.core.utils.clickableNoRipple
import com.example.squadup.features.events.manageevent.manageeventtabs.*

@Composable
fun ManageEventScreen(
    uiState: ManageEventUiState,
    selectedRoute: String,
    onNavItemClick: (String) -> Unit,
    onBackClick: () -> Unit,
    onTabChange: (ManageEventTab) -> Unit,
    onTeamSearchQueryChange: (String) -> Unit,
    onFreeAgentSearchQueryChange: (String) -> Unit,
    onLoadMoreTeams: () -> Unit,
    onLoadMoreFreeAgents: () -> Unit,
    onGameSearchQueryChange: (String) -> Unit,
    onTeamExpand: (String) -> Unit,
    onPlayerRemove: (String, String) -> Unit,
    onAddPlayerClick: (String) -> Unit,
    onEditTeamClick: (String) -> Unit,
    onDeleteTeamClick: (String) -> Unit,
    onEditGameClick: (String) -> Unit,
    onCreateGameClick: () -> Unit,
    onFormTeamsClick: () -> Unit,
    onEditEventClick: () -> Unit,
    onStatusActionClick: () -> Unit,
    onCancelEventClick: () -> Unit,
    onViewAllRegistrationsClick: () -> Unit,
    onManageLiveClick: (String) -> Unit = {},
    isAdmin: Boolean,
    isAdminView: Boolean,
    selectedLanguage: AppLanguage,
    isDarkMode: Boolean,
    onAdminViewChange: (Boolean) -> Unit,
    onLanguageChange: (AppLanguage) -> Unit,
    onDarkModeChange: (Boolean) -> Unit,
) {
    // Tabs adaptativas conforme o formato do evento
    val tabs = if (uiState.isSingleMatch) {
        listOf(ManageEventTab.OVERVIEW, ManageEventTab.MATCH, ManageEventTab.STATS)
    } else {
        listOf(ManageEventTab.OVERVIEW, ManageEventTab.TEAMS, ManageEventTab.GAMES, ManageEventTab.STATS)
    }

    Scaffold(
        topBar = {
            AppHeader(
                showLogo = false,
                showBackButton = true,
                onBackClick = onBackClick,
                title = stringResource(R.string.manageEvent_title),
                showNotificationsButton = true,
                showSettingsButton = true,
                isAdmin = isAdmin,
                isAdminView = isAdminView,
                onAdminViewChange = onAdminViewChange,
                selectedLanguage = selectedLanguage,
                isDarkMode = isDarkMode,
                onLanguageChange = onLanguageChange,
                onDarkModeChange = onDarkModeChange
            )
        },
        bottomBar = {
            AppNavBar(selectedRoute = selectedRoute, onItemClick = onNavItemClick)
        },
        floatingActionButton = {
            if (uiState.selectedTab == ManageEventTab.GAMES) {
                FloatingActionButton(
                    onClick = onCreateGameClick,
                    containerColor = SquadOrange,
                    contentColor = Color.White
                ) {
                    Icon(Icons.Filled.Add, contentDescription = null)
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(SquadBackground)
                .padding(innerPadding)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            EventHeroCard(
                uiState = uiState,
                modifier = Modifier.padding(horizontal = 20.dp)
            )

            ManageEventTabRow(
                tabs = tabs,
                selectedTab = uiState.selectedTab,
                onTabChange = onTabChange
            )

            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .align(Alignment.CenterHorizontally),
                thickness = 1.dp,
                color = SquadGray
            )

            when (uiState.selectedTab) {
                ManageEventTab.OVERVIEW -> OverviewTabContent(
                    uiState = uiState,
                    onFormTeamsClick = onFormTeamsClick,
                    onEditEventClick = onEditEventClick,
                    onStatusActionClick = onStatusActionClick,
                    onCancelEventClick = onCancelEventClick,
                    onViewAllRegistrationsClick = onViewAllRegistrationsClick,
                )
                ManageEventTab.TEAMS -> TeamsTabContent(
                    uiState = uiState,
                    onSearchQueryChange = onTeamSearchQueryChange,
                    onFreeAgentSearchQueryChange = onFreeAgentSearchQueryChange,
                    onLoadMoreTeams = onLoadMoreTeams,
                    onLoadMoreFreeAgents = onLoadMoreFreeAgents,
                    onTeamExpand = onTeamExpand,
                    onAddPlayerClick = onAddPlayerClick,
                    onEditTeamClick = onEditTeamClick,
                    onDeleteTeamClick = onDeleteTeamClick,
                    onPlayerRemove = onPlayerRemove,
                )
                ManageEventTab.GAMES -> GamesTabContent(
                    uiState = uiState,
                    onSearchQueryChange = onGameSearchQueryChange,
                    onEditGameClick = onEditGameClick,
                    onManageLiveClick = onManageLiveClick,
                )
                ManageEventTab.MATCH -> MatchTabContent(
                    uiState = uiState,
                    onManageLiveClick = onManageLiveClick,
                    onEditMatchClick = { onEditGameClick(uiState.scheduledGames.firstOrNull()?.id ?: "") },
                    onTeamExpand = onTeamExpand,
                )
                ManageEventTab.STATS -> StatsTabContent(uiState = uiState)
            }
        }
    }
}

// ─── Tab Row adaptativo ───────────────────────────────────────────────────────

@Composable
private fun ManageEventTabRow(
    tabs: List<ManageEventTab>,
    selectedTab: ManageEventTab,
    onTabChange: (ManageEventTab) -> Unit
) {
    val tabLabelRes = mapOf(
        ManageEventTab.OVERVIEW to R.string.manageEvent_tab_overview,
        ManageEventTab.TEAMS    to R.string.manageEvent_tab_teams,
        ManageEventTab.GAMES    to R.string.manageEvent_tab_games,
        ManageEventTab.STATS    to R.string.manageEvent_tab_stats,
        ManageEventTab.MATCH    to R.string.manageEvent_tab_match,
    )

    val n = tabs.size
    val selectedIndex = tabs.indexOf(selectedTab).coerceAtLeast(0)

    val horizontalBias by animateFloatAsState(
        targetValue = if (n <= 1) 0f else -1f + selectedIndex * (2f / (n - 1)),
        animationSpec = tween(durationMillis = 250),
        label = "TabIndicator"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp)
            .height(44.dp)
            .background(Color(0xFFE0E0E0), RoundedCornerShape(12.dp))
            .padding(4.dp)
    ) {
        // Pill laranja deslizante
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
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clickableNoRipple { onTabChange(tab) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(tabLabelRes[tab] ?: R.string.manageEvent_tab_overview),
                        fontSize = 13.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) Color.White else SquadTextSecondary
                    )
                }
            }
        }
    }
}
