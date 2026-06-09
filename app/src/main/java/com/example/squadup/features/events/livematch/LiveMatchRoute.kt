package com.example.squadup.features.events.livematch

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun LiveMatchRoute(
    gameId: String = "",
    selectedRoute: String,
    onNavItemClick: (String) -> Unit,
    onBackClick: () -> Unit,
    viewModel: LiveMatchViewModel = viewModel()
) {
    LaunchedEffect(gameId) {
        if (gameId.isNotBlank()) viewModel.loadGame(gameId)
    }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LiveMatchScreen(
        uiState = uiState,
        selectedRoute = selectedRoute,
        onNavItemClick = onNavItemClick,
        onBackClick = onBackClick,
        onStartMatch = viewModel::onStartMatch,
        onEndMatch = viewModel::onEndMatch,
        onStopTimer = viewModel::onStopTimer,
        onResumeTimer = viewModel::onResumeTimer,
        onTabChange = viewModel::onTabChange,
        onShowGoalForm = viewModel::onShowGoalForm,
        onShowInfractionForm = viewModel::onShowInfractionForm,
        onShowSubstitutionForm = viewModel::onShowSubstitutionForm,
        onShowAdvancedStatsForm = viewModel::onShowAdvancedStatsForm,
        onRecordGoal = viewModel::onRecordGoal,
        onRecordInfraction = viewModel::onRecordInfraction,
        onRecordSubstitution = viewModel::onRecordSubstitution,
        onRecordTimeout = viewModel::onRecordTimeout,
        onRecordAdvancedStat = viewModel::onRecordAdvancedStat,
    )
}
