package com.example.squadup.features.profile.tickets.details

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.squadup.core.app.AppViewModel

@Composable
fun TicketDetailsRoute(
    selectedRoute: String,
    onNavItemClick: (String) -> Unit,
    onBackClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    onAddToCalendarClick: () -> Unit,
    onShareTicketClick: () -> Unit,
    onSupportClick: () -> Unit,
    appViewModel: AppViewModel,
    viewModel: TicketDetailsViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val appUiState by appViewModel.uiState.collectAsStateWithLifecycle()

    TicketDetailsScreen(
        uiState = uiState,
        selectedRoute = selectedRoute,
        onNavItemClick = onNavItemClick,
        onBackClick = onBackClick,
        onNotificationsClick = onNotificationsClick,
        onAddToCalendarClick = onAddToCalendarClick,
        onShareTicketClick = onShareTicketClick,
        onSupportClick = onSupportClick,
        isAdmin = appUiState.isAdmin,
        isAdminView = appUiState.isAdminView,
        onAdminViewChange = appViewModel::onAdminViewChange,
        selectedLanguage = appUiState.selectedLanguage,
        isDarkMode = appUiState.isDarkMode,
        onLanguageChange = appViewModel::onLanguageChange,
        onDarkModeChange = appViewModel::onDarkModeChange
    )
}
