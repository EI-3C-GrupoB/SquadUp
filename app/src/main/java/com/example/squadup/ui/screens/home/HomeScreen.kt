package com.example.squadup.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Login
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.PersonAdd
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.squadup.ui.components.AppHeader
import com.example.squadup.ui.components.AppNavBar
import com.example.squadup.ui.components.CurrentMatchCard
import com.example.squadup.ui.components.NearbyEventCard
import com.example.squadup.ui.components.PrimaryButton
import com.example.squadup.ui.components.TeamListItem
import com.example.squadup.ui.theme.SquadOrange
import com.example.squadup.ui.theme.SquadTextPrimary
import com.example.squadup.ui.theme.SquadTextSecondary

@Composable
fun HomeScreen(
    uiState: HomeUiState,
    selectedRoute: String,
    onNavItemClick: (String) -> Unit,
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onViewMatchDetailsClick: () -> Unit,
    onSeeAllEventsClick: () -> Unit,
    onJoinEventClick: () -> Unit
) {
    Scaffold(
        topBar = {
            AppHeader(
                showLogo = true,
                showBackButton = false,
                actions = {
                    if (uiState.isLoggedIn) {
                        IconButton(onClick = onNotificationsClick) {
                            Icon(
                                imageVector = Icons.Outlined.Notifications,
                                contentDescription = "Notifications",
                                tint = SquadTextPrimary
                            )
                        }

                        IconButton(onClick = onSettingsClick) {
                            Icon(
                                imageVector = Icons.Outlined.Settings,
                                contentDescription = "Settings",
                                tint = SquadTextPrimary
                            )
                        }
                    }
                }
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
                .background(Color(0xFFF8F8F8))
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 26.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = if (uiState.isLoggedIn) {
                    "Hello, ${uiState.displayName}"
                } else {
                    "Hello, Player"
                },
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = SquadTextPrimary
            )

            Text(
                text = if (uiState.isLoggedIn) {
                    "Ready for your game today?"
                } else {
                    "Find local sports events and squads near you."
                },
                fontSize = 14.sp,
                color = SquadTextSecondary
            )

            Spacer(modifier = Modifier.height(28.dp))

            if (uiState.isLoggedIn && uiState.currentMatch != null) {
                CurrentMatchCard(
                    title = uiState.currentMatch.title,
                    date = uiState.currentMatch.date,
                    location = uiState.currentMatch.location,
                    onViewDetailsClick = onViewMatchDetailsClick
                )
            } else {
                GuestHomeActions(
                    onLoginClick = onLoginClick,
                    onRegisterClick = onRegisterClick
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            Row {
                Text(
                    text = "Nearby Events",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = SquadTextPrimary,
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = "See all",
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
                        onJoinClick = onJoinEventClick
                    )
                }
            }

            if (uiState.isLoggedIn) {
                Spacer(modifier = Modifier.height(34.dp))

                Text(
                    text = "My Teams",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = SquadTextPrimary
                )

                Spacer(modifier = Modifier.height(14.dp))

                uiState.teams.forEach { team ->
                    TeamListItem(
                        name = team.name,
                        details = team.details,
                        badge = team.badge
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun GuestHomeActions(
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        PrimaryButton(
            text = "Sign in to join events",
            onClick = onLoginClick,
            trailingIcon = Icons.Outlined.Login
        )

        OutlinedButton(
            onClick = onRegisterClick,
            modifier = Modifier.height(56.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = SquadOrange
            )
        ) {
            Icon(
                imageVector = Icons.Outlined.PersonAdd,
                contentDescription = null
            )

            Spacer(modifier = Modifier.padding(horizontal = 4.dp))

            Text(text = "Create account")
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HomeScreenPreview() {
    HomeScreen(
        uiState = HomeUiState(
            isLoggedIn = true,
            displayName = "Alex",
            currentMatch = HomeMatch(
                title = "City Finals: Red\nDragons vs. Blue\nHawks",
                date = "Today, 18:30",
                location = "Central Arena"
            ),
            nearbyEvents = listOf(
                HomeEvent(
                    title = "Open Tennis Doubles",
                    location = "Westside Club",
                    distance = "2.4km",
                    intensity = 0.65f
                )
            ),
            teams = listOf(
                HomeTeam(
                    name = "The Mavericks",
                    details = "12 Members - Soccer",
                    badge = "LEADER"
                )
            )
        ),
        selectedRoute = "home",
        onNavItemClick = {},
        onLoginClick = {},
        onRegisterClick = {},
        onNotificationsClick = {},
        onSettingsClick = {},
        onViewMatchDetailsClick = {},
        onSeeAllEventsClick = {},
        onJoinEventClick = {}
    )
}
