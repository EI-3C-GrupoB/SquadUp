package com.example.squadup.ui.screens.tickets

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.squadup.ui.components.AppHeader
import com.example.squadup.ui.components.AppNavBar
import com.example.squadup.ui.components.PastTicketCard
import com.example.squadup.ui.components.ReferralBanner
import com.example.squadup.ui.components.TicketCard
import com.example.squadup.ui.components.TicketTab
import com.example.squadup.ui.components.TicketTabSelector
import com.example.squadup.ui.theme.SquadBackground
import com.example.squadup.ui.theme.SquadIconSecondary
import com.example.squadup.ui.theme.SquadOrangeDark
import com.example.squadup.ui.theme.SquadTextPrimary
import com.example.squadup.ui.theme.SquadTextSecondary

@Composable
fun MyTicketsScreen(
    selectedRoute: String,
    selectedTab: TicketTab,
    onTabSelected: (TicketTab) -> Unit,
    onBackClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onViewDetailsClick: () -> Unit,
    onReferClick: () -> Unit,
    onNavItemClick: (String) -> Unit
) {
    Scaffold(
        topBar = {
            AppHeader(
                showLogo = false,
                title = "My Tickets",
                showBackButton = true,
                onBackClick = onBackClick,
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
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(28.dp))

            TicketTabSelector(
                selectedTab = selectedTab,
                onTabSelected = onTabSelected
            )

            Spacer(modifier = Modifier.height(26.dp))

            when (selectedTab) {
                TicketTab.Upcoming -> {
                    TicketCard(
                        title = "Evening Tennis\nSingles",
                        dateTime = "Oct 24 • 6:30 PM",
                        location = "Riverside Sports Complex",
                        seatInfo = "Court 4 • Gate B",
                        accentColor = SquadOrangeDark,
                        onViewDetailsClick = onViewDetailsClick
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    TicketCard(
                        title = "Elite Basketball\nLeague",
                        dateTime = "Oct 28 • 8:00 PM",
                        location = "North Arena Stadium",
                        seatInfo = "Section A12 • Row 4",
                        accentColor = Color(0xFF5365D8),
                        onViewDetailsClick = onViewDetailsClick
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    ReferralBanner(
                        onReferClick = onReferClick
                    )
                }

                TicketTab.History -> {
                    Text(
                        text = "Past Events",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = SquadTextPrimary
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "View your completed matches and activities",
                        fontSize = 15.sp,
                        color = SquadTextSecondary
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    PastTicketCard(
                        title = "Summer\nBasketball\nOpen",
                        status = "Played",
                        date = "Sept 15, 2023",
                        location = "Downtown Arena",
                        imageColors = listOf(
                            Color(0xFF1F1F1F),
                            Color(0xFF6D6D6D),
                            Color(0xFFB06A3C)
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    PastTicketCard(
                        title = "Pickleball\nSocial",
                        status = "Completed",
                        date = "Aug 22, 2023",
                        location = "Sunset Courts",
                        imageColors = listOf(
                            Color(0xFF4D6A63),
                            Color(0xFFD6B23F),
                            Color(0xFF2F3F3B)
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    PastTicketCard(
                        title = "Charity\nSoccer\nCup",
                        status = "Played",
                        date = "July 04, 2023",
                        location = "Grand Stadium",
                        imageColors = listOf(
                            Color(0xFF334155),
                            Color(0xFF94A3B8),
                            Color(0xFF1F2937)
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    PastTicketCard(
                        title = "Morning\nHIIT\nSession",
                        status = "Completed",
                        date = "June 12, 2023",
                        location = "Pulse Fitness Hub",
                        imageColors = listOf(
                            Color(0xFF9CA3AF),
                            Color(0xFFE5E7EB),
                            Color(0xFF6B7280)
                        )
                    )

                    Spacer(modifier = Modifier.height(28.dp))

                    OutlinedButton(
                        onClick = {},
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        shape = RoundedCornerShape(999.dp),
                        border = BorderStroke(
                            width = 1.dp,
                            color = SquadTextSecondary
                        ),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = Color.Transparent,
                            contentColor = SquadTextPrimary
                        )
                    ) {
                        Text(
                            text = "Load older tickets",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(26.dp))
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun MyTicketsScreenPreview() {
    var selectedTab by remember { mutableStateOf(TicketTab.History) }

    MyTicketsScreen(
        selectedRoute = "profile",
        selectedTab = selectedTab,
        onTabSelected = { selectedTab = it },
        onBackClick = {},
        onNotificationsClick = {},
        onSettingsClick = {},
        onViewDetailsClick = {},
        onReferClick = {},
        onNavItemClick = {}
    )
}