package com.example.squadup.features.events.manageevent

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.squadup.core.app.AppViewModel

@Composable
fun ManageEventRoute(
    eventId: String,
    selectedRoute: String,
    onNavItemClick: (String) -> Unit,
    onBackClick: () -> Unit,
    onFormTeamsClick: () -> Unit,
    onEditEventClick: () -> Unit,
    onCancelEventClick: () -> Unit = {},
    onManageLiveClick: (String) -> Unit = {},
    onCreateGameClick: () -> Unit,
    onEditGameClick: (String) -> Unit,
    appViewModel: AppViewModel,
    viewModel: ManageEventViewModel = viewModel()
) {
    LaunchedEffect(eventId) {
        viewModel.loadEvent(eventId)
    }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val appUiState by appViewModel.uiState.collectAsStateWithLifecycle()

    ManageEventScreen(
        uiState = uiState,
        selectedRoute = selectedRoute,
        onNavItemClick = onNavItemClick,
        onBackClick = onBackClick,
        onTabChange = viewModel::onTabChange,
        onTeamSearchQueryChange = viewModel::onTeamSearchQueryChange,
        onFreeAgentSearchQueryChange = viewModel::onFreeAgentSearchQueryChange,
        onLoadMoreTeams = viewModel::onLoadMoreTeams,
        onLoadMoreFreeAgents = viewModel::onLoadMoreFreeAgents,
        onGameSearchQueryChange = viewModel::onGameSearchQueryChange,
        onTeamExpand = viewModel::onTeamExpand,
        onPlayerRemove = viewModel::onPlayerRemove,
        onAddPlayerClick = {},
        onEditTeamClick = {},
        onDeleteTeamClick = {},
        onEditGameClick = onEditGameClick,
        onCreateGameClick = onCreateGameClick,
        onFormTeamsClick = onFormTeamsClick,
        onEditEventClick = onEditEventClick,
        onStatusActionClick = viewModel::onStatusAction,
        onCancelEventClick = viewModel::onCancelEvent,
        onManageLiveClick = onManageLiveClick,
        onViewAllRegistrationsClick = {},
        isAdmin = appUiState.isAdmin,
        isAdminView = appUiState.isAdminView,
        onAdminViewChange = { appViewModel.onAdminViewChange(it) },
        selectedLanguage = appUiState.selectedLanguage,
        isDarkMode = appUiState.isDarkMode,
        onLanguageChange = { appViewModel.onLanguageChange(it) },
        onDarkModeChange = { appViewModel.onDarkModeChange(it) }
    )
}
