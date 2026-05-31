package com.example.squadup.features.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.squadup.core.app.AppViewModel

@Composable
fun HomeRoute(
    selectedRoute: String,
    onNavItemClick: (String) -> Unit,
    onNotificationsClick: () -> Unit,
    onViewMatchDetailsClick: () -> Unit,
    onSeeAllEventsClick: () -> Unit,
    onJoinEventClick: (String) -> Unit,
    onEventDetailsClick: (String) -> Unit,
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit,
    appViewModel: AppViewModel,
    viewModel: HomeViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val appUiState by appViewModel.uiState.collectAsStateWithLifecycle()

    HomeScreen(
        uiState = uiState,
        selectedRoute = selectedRoute,
        onNavItemClick = onNavItemClick,
        onNotificationsClick = onNotificationsClick,
        onViewMatchDetailsClick = onViewMatchDetailsClick,
        onSeeAllEventsClick = onSeeAllEventsClick,
        onJoinEventClick = onJoinEventClick,
        onEventDetailsClick = onEventDetailsClick,
        onLoginClick = onLoginClick,
        onRegisterClick = onRegisterClick,
        selectedLanguage = appUiState.selectedLanguage,
        isDarkMode = appUiState.isDarkMode,
        isAdmin = appUiState.isAdmin,
        isAdminView = appUiState.isAdminView,
        onLanguageChange = { appViewModel.onLanguageChange(it) },
        onDarkModeChange = { appViewModel.onDarkModeChange(it) },
        onAdminViewChange = { appViewModel.onAdminViewChange(it) }
    )
}