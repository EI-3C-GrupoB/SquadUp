package com.example.squadup.features.notifications

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.squadup.core.app.AppViewModel

@Composable
fun NotificationsRoute(
    selectedRoute: String,
    onNavItemClick: (String) -> Unit,
    onNotificationsClick: () -> Unit,
    onBackClick: () -> Unit,
    appViewModel: AppViewModel,
    viewModel: NotificationsViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val appUiState by appViewModel.uiState.collectAsStateWithLifecycle()

    NotificationsScreen(
        uiState = uiState,
        selectedRoute = selectedRoute,
        onNavItemClick = onNavItemClick,
        onNotificationsClick = onNotificationsClick,
        onRespondToJoinRequest = viewModel::respondToJoinRequest,
        onDeleteNotification = viewModel::deleteNotification,
        onBackClick = onBackClick,
        isAdmin = appUiState.isAdmin,
        isAdminView = appUiState.isAdminView,
        selectedLanguage = appUiState.selectedLanguage,
        isDarkMode = appUiState.isDarkMode,
        onAdminViewChange = appViewModel::onAdminViewChange,
        onLanguageChange = appViewModel::onLanguageChange,
        onDarkModeChange = appViewModel::onDarkModeChange,
        notificationsCount = appUiState.notificationsCount
    )
}