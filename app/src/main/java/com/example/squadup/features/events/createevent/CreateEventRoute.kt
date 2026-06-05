package com.example.squadup.features.events.createevent

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.squadup.core.app.AppViewModel

@Composable
fun CreateEventRoute(
    onBackClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    onEventCreated: () -> Unit,
    appViewModel: AppViewModel,
    viewModel: CreateEventViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val appUiState by appViewModel.uiState.collectAsStateWithLifecycle()

    CreateEventScreen(
        uiState = uiState,
        onBackClick = onBackClick,
        onNotificationsClick = onNotificationsClick,
        onNextStep = viewModel::onNextStep,
        onPreviousStep = viewModel::onPreviousStep,
        onGoToStep = viewModel::onGoToStep,
        onEventNameChange = viewModel::onEventNameChange,
        onPrivacyChange = viewModel::onPrivacyChange,
        onSportSelect = viewModel::onSportSelect,
        formatOptions = viewModel.formatsFor(uiState.selectedSport),
        onEventFormatChange = viewModel::onEventFormatChange,
        onFormatChange = viewModel::onFormatChange,
        onMaxTeamsChange = viewModel::onMaxTeamsChange,
        onGeneralRulesChange = viewModel::onGeneralRulesChange,
        onPublicEventToggle = viewModel::onPublicEventToggle,
        onEntryFeeChange = viewModel::onEntryFeeChange,
        onAllowTeamsToggle = viewModel::onAllowTeamsToggle,
        onAllowFreeAgentsToggle = viewModel::onAllowFreeAgentsToggle,
        onVenueChange = viewModel::onVenueChange,
        onEventDateChange = viewModel::onEventDateChange,
        onStartTimeChange = viewModel::onStartTimeChange,
        onEndTimeChange = viewModel::onEndTimeChange,
        onRecurringToggle = viewModel::onRecurringToggle,
        onShowRecurrenceDialog = viewModel::onShowRecurrenceDialog,
        onRecurrenceTypeChange = viewModel::onRecurrenceTypeChange,
        onRecurringDayToggle = viewModel::onRecurringDayToggle,
        onTeamNotifyToggle = viewModel::onTeamNotifyToggle,
        onCreateEvent = onEventCreated,
        isAdmin = appUiState.isAdmin,
        isAdminView = appUiState.isAdminView,
        onAdminViewChange = appViewModel::onAdminViewChange,
        selectedLanguage = appUiState.selectedLanguage,
        isDarkMode = appUiState.isDarkMode,
        onLanguageChange = appViewModel::onLanguageChange,
        onDarkModeChange = appViewModel::onDarkModeChange
    )
}
