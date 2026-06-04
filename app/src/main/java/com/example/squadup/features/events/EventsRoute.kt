package com.example.squadup.features.events

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.squadup.core.app.AppViewModel

@Composable
fun EventsRoute(
    selectedRoute: String,
    onNavItemClick: (String) -> Unit,
    onNotificationsClick: () -> Unit,
    onEventClick: (String) -> Unit,
    onViewCalendarClick: () -> Unit,
    onFilterByMyTeamsClick: () -> Unit,
    onMapClick: () -> Unit,
    onCreateEventClick: () -> Unit,
    appViewModel: AppViewModel,
    viewModel: EventsViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val appUiState by appViewModel.uiState.collectAsStateWithLifecycle()

    EventsScreen(
        uiState = uiState,
        selectedRoute = selectedRoute,
        onNavItemClick = onNavItemClick,
        onNotificationsClick = onNotificationsClick,
        onSearchQueryChange = viewModel::onSearchQueryChange,
        onSportFilterChange = viewModel::onSportFilterChange,
        onEventClick = onEventClick,
        onViewCalendarClick = onViewCalendarClick,
        onFilterByMyTeamsClick = onFilterByMyTeamsClick,
        onMapClick = onMapClick,
        onCreateEventClick = onCreateEventClick,
        isAdmin = appUiState.isAdmin,
        isAdminView = appUiState.isAdminView,
        onAdminViewChange = appViewModel::onAdminViewChange,
        selectedLanguage = appUiState.selectedLanguage,
        isDarkMode = appUiState.isDarkMode,
        onLanguageChange = appViewModel::onLanguageChange,
        onDarkModeChange = appViewModel::onDarkModeChange
    )
}
