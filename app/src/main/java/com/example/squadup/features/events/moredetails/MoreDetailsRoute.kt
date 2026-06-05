package com.example.squadup.features.events.moredetails

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.squadup.core.app.AppViewModel

@Composable
fun MoreDetailsRoute(
    selectedRoute: String,
    onNavItemClick: (String) -> Unit,
    onBackClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    appViewModel: AppViewModel,
    viewModel: MoreDetailsViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val appUiState by appViewModel.uiState.collectAsStateWithLifecycle()

    MoreDetailsScreen(
        uiState = uiState,
        selectedRoute = selectedRoute,
        onNavItemClick = onNavItemClick,
        onNotificationsClick = onNotificationsClick,
        onBackClick = onBackClick,
        selectedLanguage = appUiState.selectedLanguage,
        isDarkMode = appUiState.isDarkMode,
        onLanguageChange = appViewModel::onLanguageChange,
        onDarkModeChange = appViewModel::onDarkModeChange
    )
}