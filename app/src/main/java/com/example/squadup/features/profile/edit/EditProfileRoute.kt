package com.example.squadup.features.profile.edit

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.squadup.core.app.AppViewModel

@Composable
fun EditProfileRoute(
    selectedRoute: String,
    onNavItemClick: (String) -> Unit,
    onSaveChangesClick: () -> Unit,
    onDeleteAccountClick: () -> Unit,
    onBackClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    appViewModel: AppViewModel,
    viewModel: EditProfileViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val appUiState by appViewModel.uiState.collectAsStateWithLifecycle()

    EditProfileScreen(
        uiState = uiState,
        selectedRoute = selectedRoute,
        onNavItemClick = onNavItemClick,
        onNameChange = viewModel::onNameChange,
        onUsernameChange = viewModel::onUsernameChange,
        onLocationChange = viewModel::onLocationChange,
        onShowLocationPickerChange = viewModel::onShowLocationPickerChange,
        onPlayStyleChange = viewModel::onPlayStyleChange,
        onNotificationRadiusChange = viewModel::onNotificationRadiusChange,
        onSportToggle = viewModel::onSportToggle,
        onSaveChangesClick = { viewModel.saveProfile(onSaveChangesClick) },
        onDeleteAccountClick = onDeleteAccountClick,
        onBackClick = onBackClick,
        onNotificationsClick = onNotificationsClick,
        selectedLanguage = appUiState.selectedLanguage,
        isDarkMode = appUiState.isDarkMode,
        onLanguageChange = appViewModel::onLanguageChange,
        onDarkModeChange = appViewModel::onDarkModeChange
    )
}
