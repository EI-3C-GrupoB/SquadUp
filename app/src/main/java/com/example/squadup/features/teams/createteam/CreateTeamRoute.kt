package com.example.squadup.features.teams.createteam

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.platform.LocalContext
import com.example.squadup.core.app.AppViewModel

@Composable
fun CreateTeamRoute(
    onBackClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    onCreateTeamClick: () -> Unit,
    appViewModel: AppViewModel,
    viewModel: CreateTeamViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val appUiState by appViewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    CreateTeamScreen(
        uiState = uiState,
        onBackClick = onBackClick,
        onNotificationsClick = onNotificationsClick,
        onCreateTeamClick = { 
            viewModel.createTeam(
                onSuccess = onCreateTeamClick, 
                context = context
            ) 
        },
        onTeamNameChange = viewModel::onTeamNameChange,
        onSportTypeSelected = viewModel::onSportTypeSelected,
        onTeamDescriptionChange = viewModel::onTeamDescriptionChange,
        onPrivateTeamChange = viewModel::onPrivateTeamChange,
        onLogoUriChange = viewModel::onLogoUriChange,
        isAdmin = appUiState.isAdmin,
        isAdminView = appUiState.isAdminView,
        selectedLanguage = appUiState.selectedLanguage,
        isDarkMode = appUiState.isDarkMode,
        onAdminViewChange = appViewModel::onAdminViewChange,
        onLanguageChange = appViewModel::onLanguageChange,
        onDarkModeChange = appViewModel::onDarkModeChange
    )
}
