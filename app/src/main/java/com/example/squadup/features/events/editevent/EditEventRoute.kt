package com.example.squadup.features.events.editevent

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.squadup.core.app.AppViewModel

@Composable
fun EditEventRoute(
    eventId: String,
    selectedRoute: String,
    onNavItemClick: (String) -> Unit,
    onBackClick: () -> Unit,
    appViewModel: AppViewModel,
    viewModel: EditEventViewModel = viewModel()
) {
    LaunchedEffect(eventId) { viewModel.loadEvent(eventId) }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val appUiState by appViewModel.uiState.collectAsStateWithLifecycle()

    EditEventScreen(
        uiState = uiState,
        selectedRoute = selectedRoute,
        onNavItemClick = onNavItemClick,
        onBackClick = onBackClick,
        onTitleChange = viewModel::onTitleChange,
        onDescriptionChange = viewModel::onDescriptionChange,
        onAddressChange = viewModel::onAddressChange,
        onStartDateChange = viewModel::onStartDateChange,
        onStartTimeChange = viewModel::onStartTimeChange,
        onEndDateChange = viewModel::onEndDateChange,
        onEndTimeChange = viewModel::onEndTimeChange,
        onEntryFeeChange = viewModel::onEntryFeeChange,
        onMaxTeamsChange = viewModel::onMaxTeamsChange,
        onParticipationLimitChange = viewModel::onParticipationLimitChange,
        onIsPublicChange = viewModel::onIsPublicChange,
        onSave = { viewModel.onSave(onBackClick) },
        selectedLanguage = appUiState.selectedLanguage,
        isDarkMode = appUiState.isDarkMode,
        onLanguageChange = { appViewModel.onLanguageChange(it) },
        onDarkModeChange = { appViewModel.onDarkModeChange(it) }
    )
}
