package com.example.squadup.features.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
    onViewMatchDetailsClick: () -> Unit,
    onSeeAllEventsClick: () -> Unit,
    onJoinEventClick: (String) -> Unit,
    onEventDetailsClick: (String) -> Unit,
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit,
    selectedLanguage: AppLanguage,
    isDarkMode: Boolean,
    onLanguageChange: (AppLanguage) -> Unit,
    onDarkModeChange: (Boolean) -> Unit,
) {
    Scaffold(
        topBar = {
            AppHeader(
                showLogo = true,
                showBackButton = false,
                showNotificationsButton = uiState.isLoggedIn,
                showSettingsButton = true,
                showLoginButton = !uiState.isLoggedIn,
                onNotificationsClick = onNotificationsClick,
                onLoginClick = onLoginClick,
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(SquadBackground)
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 26.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // --- SAUDAÇÃO ---
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

            // --- STATS (apenas organizador) ---
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

            // --- CURRENT MATCH (todos os roles) ---
            if (uiState.currentMatch != null) {
                CurrentMatchCard(
                    title = uiState.currentMatch.title,
                    date = uiState.currentMatch.date,
                    location = uiState.currentMatch.location,
                    sportType = uiState.currentMatch.sportType,
                    onViewDetailsClick = onViewMatchDetailsClick
                )

                Spacer(modifier = Modifier.height(28.dp))
            }

            // --- MY EVENTS (apenas organizador) ---
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

                uiState.myEvents.forEach { event ->
                    OrganizerEventCard(
                        title = event.title,
                        price = event.price,
                        nTeams = event.nTeams,
                        dateLeft = event.dateLeft,
                        registeredCount = event.registeredCount,
                        status = event.status,
                        sportType = event.sportType,
                        modifier = Modifier.clickable { onEventDetailsClick(event.id) }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }

                Spacer(modifier = Modifier.height(28.dp))
            }

            // --- NEARBY EVENTS (todos os roles) ---
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
                        onJoinClick = { onJoinEventClick(event.id) }
                    )
                }
            }

            // --- MY TEAMS (apenas jogador) ---
            if (uiState.isPlayer) {
                Spacer(modifier = Modifier.height(28.dp))

                Text(
                    text = stringResource(R.string.home_my_teams),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = SquadTextPrimary
                )

                Spacer(modifier = Modifier.height(14.dp))

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

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}