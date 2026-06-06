package com.example.squadup.features.profile

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.squadup.core.app.AppViewModel

@Composable
fun ProfileRoute(
    selectedRoute: String,
    onNavItemClick: (String) -> Unit,
    onNotificationsClick: () -> Unit,
    onTicketsClick: () -> Unit,
    onMyEventsClick: () -> Unit,
    onManageAccountsClick: () -> Unit,
    onEditProfileClick: () -> Unit,
    onChangePasswordClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onLoginClick: () -> Unit,
    appViewModel: AppViewModel,
    viewModel: ProfileViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val appUiState by appViewModel.uiState.collectAsStateWithLifecycle()

    ProfileScreen(
        uiState = uiState,
        selectedRoute = selectedRoute,
        onNavItemClick = onNavItemClick,
        isAdmin = appUiState.isAdmin,
        isAdminView = appUiState.isAdminView,
        onAdminViewChange = { appViewModel.onAdminViewChange(it) },
        onTicketsClick = onTicketsClick,
        onMyEventsClick = onMyEventsClick,
        onManageAccountsClick = onManageAccountsClick,
        onEditProfileClick = onEditProfileClick,
        onChangePasswordClick = onChangePasswordClick,
        onLogoutClick = { viewModel.logout(onLogoutClick) },
        onNotificationsClick = onNotificationsClick,
        onLoginClick = onLoginClick,
        selectedLanguage = appUiState.selectedLanguage,
        isDarkMode = appUiState.isDarkMode,
        onLanguageChange = { appViewModel.onLanguageChange(it) },
        onDarkModeChange = { appViewModel.onDarkModeChange(it) }
    )
}
