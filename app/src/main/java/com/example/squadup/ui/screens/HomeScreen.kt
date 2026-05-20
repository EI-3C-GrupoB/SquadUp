package com.example.squadup.ui.screens.main

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.example.squadup.ui.components.TeamListItem
import com.example.squadup.ui.theme.SquadIconSecondary
import com.example.squadup.ui.theme.SquadOrange
import com.example.squadup.ui.theme.SquadTextPrimary

@Composable
fun HomeScreen(
    selectedRoute: String,
    onNavItemClick: (String) -> Unit,
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
                    IconButton(onClick = onNotificationsClick) {
                        Icon(
                            imageVector = Icons.Outlined.Notifications,
                            contentDescription = "Notificações",
                            tint = SquadIconSecondary
                        )
                    }

                    IconButton(onClick = onSettingsClick) {
                        Icon(
                            imageVector = Icons.Outlined.Settings,
                            contentDescription = "Definições",
                            tint = SquadIconSecondary
                        )
                    }
                }
            )
        },
        bottomBar = {
            AppNavBar(
                selectedRoute = selectedRoute,
                onItemClick = onNavItemClick,
                modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars)
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
                text = "Hello, Alex",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = SquadTextPrimary
            )

            Text(
                text = "Ready for your game today?",
                fontSize = 14.sp,
                color = SquadIconSecondary
            )

            Spacer(modifier = Modifier.height(28.dp))

            CurrentMatchCard(
                title = "City Finals: Red\nDragons vs. Blue\nHawks",
                date = "Today, 18:30",
                location = "Central Arena",
                onViewDetailsClick = onViewMatchDetailsClick
            )

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
                    color = SquadOrange
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                NearbyEventCard(
                    title = "Open Tennis Doubles",
                    location = "Westside Club",
                    distance = "2.4km",
                    intensity = 0.65f,
                    onJoinClick = onJoinEventClick
                )

                NearbyEventCard(
                    title = "3x3 Street Basketball",
                    location = "Central Court",
                    distance = "1.1km",
                    intensity = 0.85f,
                    onJoinClick = onJoinEventClick
                )
            }

            Spacer(modifier = Modifier.height(34.dp))

            Text(
                text = "My Teams",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = SquadTextPrimary
            )

            Spacer(modifier = Modifier.height(14.dp))

            TeamListItem(
                name = "The Mavericks",
                details = "12 Members • Soccer",
                badge = "LEADER"
            )

            Spacer(modifier = Modifier.height(12.dp))

            TeamListItem(
                name = "Midnight Hoop",
                details = "8 Members • Basketball",
                badge = null
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HomeScreenPreview() {
    HomeScreen(
        selectedRoute = "home",
        onNavItemClick = {},
        onNotificationsClick = {},
        onSettingsClick = {},
        onViewMatchDetailsClick = {},
        onSeeAllEventsClick = {},
        onJoinEventClick = {}
    )
}