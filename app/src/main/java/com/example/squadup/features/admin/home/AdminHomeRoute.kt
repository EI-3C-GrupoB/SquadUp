package com.example.squadup.features.admin.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.squadup.core.app.AppViewModel

@Composable
fun AdminHomeRoute(
    selectedRoute: String,
    onNavItemClick: (String) -> Unit,
    onNotificationsClick: () -> Unit,
    onAdminPageClick: () -> Unit,
    appViewModel: AppViewModel,
    viewModel: AdminHomeViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val appUiState by appViewModel.uiState.collectAsStateWithLifecycle()

    AdminHomeScreen(
        uiState = uiState,
        selectedRoute = selectedRoute,
        onNavItemClick = onNavItemClick,
        onNotificationsClick = onNotificationsClick,
        onAdminPageClick = onAdminPageClick,
        selectedLanguage = appUiState.selectedLanguage,
        isDarkMode = appUiState.isDarkMode,
        isAdmin = appUiState.isAdmin,
        isAdminView = appUiState.isAdminView,
        onLanguageChange = appViewModel::onLanguageChange,
        onDarkModeChange = appViewModel::onDarkModeChange,
        onAdminViewChange = appViewModel::onAdminViewChange,
        notificationsCount = appUiState.notificationsCount
    )
}