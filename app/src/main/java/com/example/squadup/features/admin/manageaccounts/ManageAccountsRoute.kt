package com.example.squadup.features.admin.manageaccounts

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.squadup.core.app.AppViewModel

@Composable
fun ManageAccountsRoute(
    selectedRoute: String,
    onNavItemClick: (String) -> Unit,
    onBackClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    onUserClick: (String) -> Unit,
    onCreateUserClick: () -> Unit,
    onPreviousClick: () -> Unit,
    onNextClick: () -> Unit,
    appViewModel: AppViewModel,
    viewModel: ManageAccountsViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val appUiState by appViewModel.uiState.collectAsStateWithLifecycle()

    ManageAccountsScreen(
        uiState = uiState,
        selectedRoute = selectedRoute,
        onNavItemClick = onNavItemClick,
        onBackClick = onBackClick,
        onNotificationsClick = onNotificationsClick,
        onUserClick = onUserClick,
        onCreateUserClick = onCreateUserClick,
        onSearchQueryChange = viewModel::onSearchQueryChange,
        onFilterClick = viewModel::onFilterDialogOpen,
        onTogglePendingRole = viewModel::onTogglePendingRole,
        onApplyFilter = viewModel::onApplyFilter,
        onFilterDismiss = viewModel::onFilterDialogDismiss,
        onSortByName = viewModel::onSortByName,
        onSortByRole = viewModel::onSortByRole,
        onPreviousClick = onPreviousClick,
        onNextClick = onNextClick,
        isAdmin = appUiState.isAdmin,
        isAdminView = appUiState.isAdminView,
        onAdminViewChange = appViewModel::onAdminViewChange,
        selectedLanguage = appUiState.selectedLanguage,
        isDarkMode = appUiState.isDarkMode,
        onLanguageChange = appViewModel::onLanguageChange,
        onDarkModeChange = appViewModel::onDarkModeChange
    )
}
