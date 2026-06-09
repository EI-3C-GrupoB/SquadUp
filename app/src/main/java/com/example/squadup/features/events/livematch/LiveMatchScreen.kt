package com.example.squadup.features.events.livematch

import androidx.compose.runtime.Composable

@Composable
fun LiveMatchScreen(
    uiState: LiveMatchUiState,
    selectedRoute: String,
    onNavItemClick: (String) -> Unit,
    onBackClick: () -> Unit,
    onStartMatch: () -> Unit,
    onEndMatch: () -> Unit,
    onStopTimer: () -> Unit,
    onResumeTimer: () -> Unit,
    onTabChange: (LiveMatchTab) -> Unit,
    onShowGoalForm: (Boolean) -> Unit,
    onShowInfractionForm: (Boolean) -> Unit,
    onShowSubstitutionForm: (Boolean) -> Unit,
    onShowAdvancedStatsForm: (Boolean) -> Unit,
    onRecordGoal: (Boolean, String, String, Int) -> Unit,
    onRecordInfraction: (Boolean, String, String) -> Unit,
    onRecordSubstitution: (Boolean, String, String) -> Unit,
    onRecordTimeout: (Boolean) -> Unit,
    onRecordAdvancedStat: (Boolean, String, Int) -> Unit,
) {
    when (uiState.phase) {
        LiveMatchPhase.PRE_MATCH -> LiveMatchPreMatch(
            uiState = uiState,
            onBackClick = onBackClick,
            onStartMatch = onStartMatch
        )
        LiveMatchPhase.LIVE,
        LiveMatchPhase.FINISHED -> LiveMatchContent(
            uiState = uiState,
            onBackClick = onBackClick,
            onEndMatch = onEndMatch,
            onStopTimer = onStopTimer,
            onResumeTimer = onResumeTimer,
            onTabChange = onTabChange,
            onShowGoalForm = onShowGoalForm,
            onShowInfractionForm = onShowInfractionForm,
            onShowSubstitutionForm = onShowSubstitutionForm,
            onShowAdvancedStatsForm = onShowAdvancedStatsForm,
            onRecordGoal = onRecordGoal,
            onRecordInfraction = onRecordInfraction,
            onRecordSubstitution = onRecordSubstitution,
            onRecordTimeout = onRecordTimeout,
            onRecordAdvancedStat = onRecordAdvancedStat,
            selectedRoute = selectedRoute,
            onNavItemClick = onNavItemClick,
        )
    }
}
