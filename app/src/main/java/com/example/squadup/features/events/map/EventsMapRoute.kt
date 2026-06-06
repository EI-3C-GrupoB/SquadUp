package com.example.squadup.features.events.map

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.squadup.core.app.AppViewModel

@Composable
fun EventsMapRoute(
    onBackClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    onEventClick: (String) -> Unit,
    appViewModel: AppViewModel,
    viewModel: EventsMapViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val appUiState by appViewModel.uiState.collectAsStateWithLifecycle()

    EventsMapScreen(
        uiState = uiState,
        onBackClick = onBackClick,
        onNotificationsClick = onNotificationsClick,
        onSportFilterChange = viewModel::onSportFilterChange,
        onPinSelected = viewModel::onPinSelected,
        onEventClick = onEventClick,
        isAdmin = appUiState.isAdmin,
        isAdminView = appUiState.isAdminView,
        onAdminViewChange = appViewModel::onAdminViewChange,
        selectedLanguage = appUiState.selectedLanguage,
        isDarkMode = appUiState.isDarkMode,
        onLanguageChange = appViewModel::onLanguageChange,
        onDarkModeChange = appViewModel::onDarkModeChange
    )
}