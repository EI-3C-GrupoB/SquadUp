package com.example.squadup.features.teams.invite

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.squadup.core.app.AppViewModel

@Composable
fun InviteTeamRoute(
    teamId: String,
    onBackClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    appViewModel: AppViewModel,
    viewModel: InviteTeamViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val appUiState by appViewModel.uiState.collectAsStateWithLifecycle()

    androidx.compose.runtime.LaunchedEffect(teamId) {
        viewModel.loadInviteState(teamId)
    }

    InviteTeamScreen(
        uiState = uiState,
        onBackClick = onBackClick,
        onNotificationsClick = onNotificationsClick,
        onUsernameOrEmailChange = viewModel::onUsernameOrEmailChange,
        onInviteContactClick = viewModel::onInviteContact,
        onSendInviteClick = viewModel::onSendInvite,
        isAdmin = appUiState.isAdmin,
        isAdminView = appUiState.isAdminView,
        selectedLanguage = appUiState.selectedLanguage,
        isDarkMode = appUiState.isDarkMode,
        onAdminViewChange = appViewModel::onAdminViewChange,
        onLanguageChange = appViewModel::onLanguageChange,
        onDarkModeChange = appViewModel::onDarkModeChange
    )
}