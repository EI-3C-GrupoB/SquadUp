package com.example.squadup.features.profile.tickets

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ConfirmationNumber
import androidx.compose.material.icons.outlined.History
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.squadup.R
import com.example.squadup.core.ui.components.*
import com.example.squadup.core.ui.theme.*
import com.example.squadup.core.utils.AppLanguage

@Composable
fun MyTicketsScreen(
    uiState: MyTicketsUiState,
    selectedRoute: String,
    onNavItemClick: (String) -> Unit,
    onTabSelected: (TicketTab) -> Unit,
    onNotificationsClick: () -> Unit,
    onBackClick: () -> Unit,
    onViewDetailsClick: (String) -> Unit,
    isAdmin: Boolean,
    isAdminView: Boolean,
    selectedLanguage: AppLanguage,
    isDarkMode: Boolean,
    onAdminViewChange: (Boolean) -> Unit,
    onLanguageChange: (AppLanguage) -> Unit,
    onDarkModeChange: (Boolean) -> Unit,
) {
    Scaffold(
        topBar = {
            AppHeader(
                showLogo = false,
                title = stringResource(R.string.tickets_title),
                showBackButton = true,
                onBackClick = onBackClick,
                onNotificationsClick = onNotificationsClick,
                showNotificationsButton = true,
                showSettingsButton = true,
                isAdmin = isAdmin,
                isAdminView = isAdminView,
                onAdminViewChange = onAdminViewChange,
                selectedLanguage = selectedLanguage,
                isDarkMode = isDarkMode,
                onLanguageChange = onLanguageChange,
                onDarkModeChange = onDarkModeChange
            )
        },
        bottomBar = {
            AppNavBar(selectedRoute = selectedRoute, onItemClick = onNavItemClick)
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(SquadBackground)
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(28.dp))

            TicketTabSelector(
                selectedTab = uiState.selectedTab,
                onTabSelected = onTabSelected
            )

            Spacer(modifier = Modifier.height(26.dp))

            when (uiState.selectedTab) {
                TicketTab.Upcoming -> {
                    if (uiState.upcomingTickets.isEmpty()) {
                        EmptyStateCard(
                            title = "Sem bilhetes próximos",
                            message = "Ainda não tens bilhetes para eventos futuros. Explora os eventos para te inscreveres!",
                            icon = Icons.Outlined.ConfirmationNumber
                        )
                    } else {
                        uiState.upcomingTickets.forEach { ticket ->
                            TicketCard(
                                title = ticket.title,
                                dateTime = ticket.dateTime,
                                location = ticket.location,
                                sportType = ticket.sportType,
                                onViewDetailsClick = { onViewDetailsClick(ticket.id) }
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                }

                TicketTab.History -> {
                    if (uiState.pastTickets.isEmpty()) {
                        EmptyStateCard(
                            title = "Sem histórico",
                            message = "Ainda não tens um histórico de bilhetes. Os teus bilhetes passados aparecerão aqui.",
                            icon = Icons.Outlined.History
                        )
                    } else {
                        Text(
                            text = stringResource(R.string.tickets_past_events),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = SquadTextPrimary
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = stringResource(R.string.tickets_past_events_subtitle),
                            fontSize = 15.sp,
                            color = SquadTextSecondary
                        )

                        Spacer(modifier = Modifier.height(18.dp))

                        uiState.pastTickets.forEach { ticket ->
                            PastTicketCard(
                                title = ticket.title,
                                status = ticket.status,
                                date = ticket.date,
                                location = ticket.location,
                                sportType = ticket.sportType
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedButton(
                            onClick = {},
                            modifier = Modifier.align(Alignment.CenterHorizontally),
                            shape = RoundedCornerShape(999.dp),
                            border = BorderStroke(1.dp, SquadTextSecondary),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = Color.Transparent,
                                contentColor = SquadTextPrimary
                            )
                        ) {
                            Text(
                                text = stringResource(R.string.tickets_load_older),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(26.dp))
        }
    }
}
