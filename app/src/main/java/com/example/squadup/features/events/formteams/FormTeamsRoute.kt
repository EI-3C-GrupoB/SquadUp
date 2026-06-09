package com.example.squadup.features.events.formteams

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.squadup.core.app.AppViewModel

@Composable
fun FormTeamsRoute(
    eventId: String,
    selectedRoute: String,
    onNavItemClick: (String) -> Unit,
    onBackClick: () -> Unit,
    appViewModel: AppViewModel,
    viewModel: FormTeamsViewModel = viewModel()
) {
    LaunchedEffect(eventId) {
        viewModel.loadEvent(eventId)
    }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val appUiState by appViewModel.uiState.collectAsStateWithLifecycle()

    FormTeamsScreen(
        uiState = uiState,
        selectedRoute = selectedRoute,
        onNavItemClick = onNavItemClick,
        onBackClick = onBackClick,
        onRandomize = viewModel::onRandomize,
        onSelectPlayerForAssign = viewModel::onSelectPlayerForAssign,
        onAssignPlayerToTeam = viewModel::onAssignPlayerToTeam,
        onUnassignPlayer = viewModel::onUnassignPlayer,
        onAddTeam = viewModel::onAddTeam,
        onRemoveTeam = viewModel::onRemoveTeam,
        onSaveRoster = { viewModel.saveRoster(onBackClick) },
        selectedLanguage = appUiState.selectedLanguage,
        isDarkMode = appUiState.isDarkMode,
        onLanguageChange = { appViewModel.onLanguageChange(it) },
        onDarkModeChange = { appViewModel.onDarkModeChange(it) }
    )
}
