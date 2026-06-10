package com.example.squadup.features.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.SportsSoccer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.squadup.R
import com.example.squadup.core.ui.components.*
import com.example.squadup.core.ui.theme.*
import com.example.squadup.core.utils.AppLanguage

@Composable
fun HomeScreen(
    uiState: HomeUiState,
    selectedRoute: String,
    onNavItemClick: (String) -> Unit,
    onNotificationsClick: () -> Unit,
    onAdminPageClick: () -> Unit,
    onViewMatchDetailsClick: (gameId: String) -> Unit,
    onSeeAllEventsClick: () -> Unit,
    onJoinEventClick: (String) -> Unit,
    onEventDetailsClick: (String) -> Unit,
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit,
    selectedLanguage: AppLanguage,
    isDarkMode: Boolean,
    isAdmin: Boolean,
    isAdminView: Boolean,
    onLanguageChange: (AppLanguage) -> Unit,
    onDarkModeChange: (Boolean) -> Unit,
    onAdminViewChange: (Boolean) -> Unit,
    notificationsCount: Int = 0
) {
    Scaffold(
        topBar = {
            AppHeader(
                showLogo = true,
                showBackButton = false,
                showNotificationsButton = uiState.isLoggedIn,
                notificationsCount = notificationsCount,
                onNotificationsClick = onNotificationsClick,
                onAdminPageClick = onAdminPageClick,
                showSettingsButton = true,
                showLoginButton = !uiState.isLoggedIn,
                isAdmin = isAdmin,
                isAdminView = isAdminView,
                onLoginClick = onLoginClick,
                onAdminViewChange = onAdminViewChange,
                selectedLanguage = selectedLanguage,
                isDarkMode = isDarkMode,
                onLanguageChange = onLanguageChange,
                onDarkModeChange = onDarkModeChange
            )
        },
        bottomBar = {
            AppNavBar(
                selectedRoute = selectedRoute,
                onItemClick = onNavItemClick
            )
        }
    ) { innerPadding ->
        if (uiState.isLoading && uiState.displayName.isBlank()) {
            LoadingScreen(message = "A carregar novidades...")
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(SquadBackground)
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 26.dp)
            ) {
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = if (uiState.isLoggedIn) {
                    stringResource(R.string.home_greeting, uiState.displayName)
                } else {
                    stringResource(R.string.home_greeting_guest)
                },
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = SquadTextPrimary
            )

            Text(
                text = when {
                    uiState.isOrganizer && uiState.isPlayer -> stringResource(R.string.home_subtitle_both)
                    uiState.isOrganizer -> stringResource(R.string.home_subtitle_organizer)
                    uiState.isPlayer -> stringResource(R.string.home_subtitle_player)
                    else -> stringResource(R.string.home_subtitle_guest)
                },
                fontSize = 14.sp,
                color = SquadTextSecondary
            )

            Spacer(modifier = Modifier.height(28.dp))

            if (uiState.isOrganizer) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatsCard(
                        label = stringResource(R.string.statsCard_events_created),
                        value = uiState.eventsCreated.toString(),
                        modifier = Modifier.weight(1f)
                    )

                    StatsCard(
                        label = stringResource(R.string.statsCard_active_teams),
                        value = uiState.activeTeams.toString(),
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                StatsCard(
                    label = stringResource(R.string.statsCard_total_revenue),
                    value = "$${uiState.totalRevenue}",
                    style = StatsCardStyle.HIGHLIGHT,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(28.dp))
            }

            if (uiState.currentMatch != null) {
                CurrentMatchCard(
                    title = uiState.currentMatch.title,
                    date = uiState.currentMatch.date,
                    location = uiState.currentMatch.location,
                    sportType = uiState.currentMatch.sportType,
                    isOrganizer = uiState.currentMatch.isOwnedByCurrentUser,
                    onViewDetailsClick = { onViewMatchDetailsClick(uiState.currentMatch.id) }
                )
            } else {
                EmptyStateCard(
                    title = "Sem jogo atual",
                    message = "Ainda não existe nenhum jogo em destaque para mostrar.",
                    icon = Icons.Outlined.SportsSoccer
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            if (uiState.isOrganizer) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = stringResource(R.string.home_my_events),
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = SquadTextPrimary,
                        modifier = Modifier.weight(1f)
                    )

                    Text(
                        text = stringResource(R.string.home_see_all),
                        fontSize = 14.sp,
                        color = SquadOrange,
                        modifier = Modifier.clickable(onClick = onSeeAllEventsClick)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                if (uiState.myEvents.isEmpty()) {
                    EmptyStateCard(
                        title = "Sem eventos criados",
                        message = "Ainda não criaste nenhum evento. Quando criares eventos, eles aparecem aqui.",
                        icon = Icons.Outlined.CalendarMonth,
                        actionText = "Criar evento",
                        onActionClick = onSeeAllEventsClick
                    )
                } else {
                    uiState.myEvents.forEach { event ->
                        OrganizerEventCard(
                            title = event.title,
                            price = event.price,
                            nTeams = event.nTeams,
                            dateLeft = event.dateLeft,
                            registeredCount = event.registeredCount,
                            status = event.status,
                            sportType = event.sportType,
                            modifier = Modifier.clickable {
                                onEventDetailsClick(event.id)
                            }
                        )

                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))
            }

            Row(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = stringResource(R.string.home_nearby_events),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = SquadTextPrimary,
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = stringResource(R.string.home_see_all),
                    fontSize = 14.sp,
                    color = SquadOrange,
                    modifier = Modifier.clickable(onClick = onSeeAllEventsClick)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            if (uiState.nearbyEvents.isEmpty()) {
                EmptyStateCard(
                    title = "Sem eventos próximos",
                    message = "Ainda não existem eventos próximos disponíveis.",
                    icon = Icons.Outlined.CalendarMonth
                )
            } else {
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    uiState.nearbyEvents.forEach { event ->
                        NearbyEventCard(
                            title = event.title,
                            location = event.location,
                            distance = event.distance,
                            intensity = event.intensity,
                            sportType = event.sportType,
                            status = event.status,
                            onJoinClick = {
                                onJoinEventClick(event.id)
                            }
                        )
                    }
                }
            }

            if (uiState.isPlayer) {
                Spacer(modifier = Modifier.height(28.dp))

                Text(
                    text = stringResource(R.string.home_my_teams),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = SquadTextPrimary
                )

                Spacer(modifier = Modifier.height(14.dp))

                if (uiState.teams.isEmpty()) {
                    EmptyStateCard(
                        title = "Sem equipas",
                        message = "Ainda não tens equipas associadas à tua conta.",
                        icon = Icons.Outlined.Groups
                    )
                } else {
                    uiState.teams.forEach { team ->
                        TeamListItem(
                            name = team.name,
                            nMembers = team.nMembers,
                            sportType = team.sportType,
                            badge = team.badge
                        )

                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
}
