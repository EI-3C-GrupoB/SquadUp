package com.example.squadup.features.events.moredetails

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.squadup.core.app.AppViewModel
import com.example.squadup.core.utils.AppLanguage

@Composable
fun MoreDetailsRoute(
    eventId: String,
    selectedRoute: String,
    onNavItemClick: (String) -> Unit,
    onBackClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    onManageEventClick: (Int) -> Unit,
    onPaymentClick: (inscricaoId: String, eventId: String) -> Unit = { _, _ -> },
    onViewTicketClick: (ticketId: String) -> Unit = {},
    appViewModel: AppViewModel,
    viewModel: MoreDetailsViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val appUiState by appViewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(
        eventId,
        appUiState.userId,
        appUiState.userRole
    ) {
        viewModel.loadEvent(
            eventId = eventId,
            currentUserId = appUiState.userId,
            userRole = appUiState.userRole
        )
    }

    MoreDetailsScreen(
        uiState = uiState,
        selectedRoute = selectedRoute,
        onNavItemClick = onNavItemClick,
        onNotificationsClick = onNotificationsClick,
        onBackClick = onBackClick,
        onManageEventClick = onManageEventClick,
        onJoinIndividuallyClick = {
            viewModel.joinIndividually(appUiState.userId)
        },
        onJoinWithTeamClick = {
            viewModel.openTeamPicker(appUiState.userId)
        },
        onDismissTeamPicker = viewModel::closeTeamPicker,
        onTeamSelected = { teamId ->
            viewModel.joinWithTeam(
                teamId = teamId,
                currentUserId = appUiState.userId
            )
        },
        onVerifyCode = viewModel::verifyAccessCode,
        onPaymentClick = {
            val inscricaoId = uiState.userInscricaoId
            val evId = uiState.eventId
            if (inscricaoId != null && evId != null) {
                onPaymentClick(inscricaoId.toString(), evId.toString())
            }
        },
        onViewTicketClick = {
            val ticketId = uiState.userTicketId
            if (ticketId != null) {
                onViewTicketClick(ticketId.toString())
            }
        },
        selectedLanguage = appUiState.selectedLanguage,
        isDarkMode = appUiState.isDarkMode,
        onLanguageChange = appViewModel::onLanguageChange,
        onDarkModeChange = appViewModel::onDarkModeChange
    )
}
