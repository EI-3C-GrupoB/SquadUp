package com.example.squadup.features.events.calendar

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.squadup.core.app.AppViewModel

@Composable
fun CalendarRoute(
    selectedRoute: String,
    onNavItemClick: (String) -> Unit,
    onBackClick: () -> Unit,
    onMatchDetailsClick: () -> Unit,
    onTravelInfoClick: () -> Unit,
    appViewModel: AppViewModel,
    viewModel: CalendarViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val appUiState by appViewModel.uiState.collectAsStateWithLifecycle()

    CalendarScreen(
        uiState = uiState,
        selectedRoute = selectedRoute,
        onNavItemClick = onNavItemClick,
        onBackClick = onBackClick,
        onPreviousMonthClick = viewModel::onPreviousMonthClick,
        onNextMonthClick = viewModel::onNextMonthClick,
        onTodayClick = viewModel::onTodayClick,
        onDayClick = viewModel::onDayClick,
        onMatchDetailsClick = onMatchDetailsClick,
        onTravelInfoClick = onTravelInfoClick,
        selectedLanguage = appUiState.selectedLanguage,
        isDarkMode = appUiState.isDarkMode,
        onLanguageChange = appViewModel::onLanguageChange,
        onDarkModeChange = appViewModel::onDarkModeChange
    )
}