package com.example.squadup.features.admin.manageaccounts.edituser

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.squadup.core.app.AppViewModel

@Composable
fun EditUserRoute(
    selectedRoute: String,
    onNavItemClick: (String) -> Unit,
    onBackClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    onSendMessageClick: () -> Unit,
    onDeleteClick: () -> Unit,
    appViewModel: AppViewModel,
    viewModel: EditUserViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val appUiState by appViewModel.uiState.collectAsStateWithLifecycle()

    EditUserScreen(
        uiState = uiState,
        selectedRoute = selectedRoute,
        onNavItemClick = onNavItemClick,
        onBackClick = onBackClick,
        onNotificationsClick = onNotificationsClick,
        onRoleChange = viewModel::onRoleChange,
        onSendMessageClick = onSendMessageClick,
        onToggleSuspend = viewModel::onToggleSuspend,
        onDeleteClick = { viewModel.deleteUser(onDeleteClick) },
        onSaveClick = { viewModel.saveUser(onBackClick) },
        isAdmin = appUiState.isAdmin,
        isAdminView = appUiState.isAdminView,
        onAdminViewChange = appViewModel::onAdminViewChange,
        selectedLanguage = appUiState.selectedLanguage,
        isDarkMode = appUiState.isDarkMode,
        onLanguageChange = appViewModel::onLanguageChange,
        onDarkModeChange = appViewModel::onDarkModeChange
    )
}
