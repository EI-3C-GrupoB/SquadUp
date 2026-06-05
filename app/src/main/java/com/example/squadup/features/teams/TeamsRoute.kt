package com.example.squadup.features.teams

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.squadup.core.app.AppViewModel

@Composable
fun TeamsRoute(
    selectedRoute: String,
    onNavItemClick: (String) -> Unit,
    onCreateTeamClick: () -> Unit,
    onInviteMembersClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    appViewModel: AppViewModel,
    viewModel: TeamsViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val appUiState by appViewModel.uiState.collectAsStateWithLifecycle()

    TeamsScreen(
        uiState = uiState,
        selectedRoute = selectedRoute,
        onNavItemClick = onNavItemClick,
        onCreateTeamClick = onCreateTeamClick,
        onInviteMembersClick = onInviteMembersClick,
        onNotificationsClick = onNotificationsClick,
        onTabSelected = viewModel::onTabSelected,
        onSearchQueryChange = viewModel::onSearchQueryChange,
        onTeamToggle = viewModel::onTeamToggle,
        onTeamSettingsToggle = viewModel::onTeamSettingsToggle,
        isAdmin = appUiState.isAdmin,
        isAdminView = appUiState.isAdminView,
        selectedLanguage = appUiState.selectedLanguage,
        isDarkMode = appUiState.isDarkMode,
        onAdminViewChange = appViewModel::onAdminViewChange,
        onLanguageChange = appViewModel::onLanguageChange,
        onDarkModeChange = appViewModel::onDarkModeChange
    )
}