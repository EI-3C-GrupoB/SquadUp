package com.example.squadup.ui.screens.events

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.example.squadup.navigation.AppRoutes
import com.example.squadup.ui.components.AppHeader
import com.example.squadup.ui.components.AppNavBar
import com.example.squadup.ui.components.EventFilterUi
import com.example.squadup.ui.components.EventsSearchField
import com.example.squadup.ui.components.ExploreEventCard
import com.example.squadup.ui.components.ExploreEventUi
import com.example.squadup.ui.components.FeaturedEventCard
import com.example.squadup.ui.components.UpcomingEventUi
import com.example.squadup.ui.components.UpcomingEventsCard
import com.example.squadup.ui.components.defaultExploreEvents
import com.example.squadup.ui.theme.SquadBackground
import com.example.squadup.ui.theme.SquadGrayDark
import com.example.squadup.ui.theme.SquadOrange
import com.example.squadup.ui.theme.SquadTextPrimary

@Composable
fun ExploreEventsScreen(
    selectedRoute: String,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onMapClick: () -> Unit,
    onFilterClick: (EventFilterUi) -> Unit,
    onFeaturedEventClick: () -> Unit,
    onViewCalendarClick: () -> Unit,
    onFilterByTeamsClick: () -> Unit,
    onEventActionClick: (ExploreEventUi) -> Unit,
    onNotificationsClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onNavItemClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val filters = remember {
        listOf(
            EventFilterUi("All Events", selected = true),
            EventFilterUi("Basketball"),
            EventFilterUi("Football"),
            EventFilterUi("Tennis")
        )
    }
    val upcomingEvents = remember {
        listOf(
            UpcomingEventUi("APR", "9", "The Mavericks vs Red Eagles", "Football • 8:00 PM"),
            UpcomingEventUi("APR", "12", "Viper Strike Open", "Paddle • 2:00 PM"),
            UpcomingEventUi("APR", "19", "Sunday Night Lights", "Basketball • 8:00 PM", muted = true)
        )
    }
    val events = remember { defaultExploreEvents() }

    Scaffold(
        modifier = modifier,
        topBar = {
            AppHeader(
                showLogo = true,
                title = "SquadUp",
                showBackButton = false,
                actions = {
                    IconButton(onClick = onNotificationsClick) {
                        Icon(
                            imageVector = Icons.Outlined.Notifications,
                            contentDescription = "Notifications",
                            tint = SquadGrayDark
                        )
                    }

                    IconButton(onClick = onSettingsClick) {
                        Icon(
                            imageVector = Icons.Outlined.Settings,
                            contentDescription = "Settings",
                            tint = SquadGrayDark
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
                .padding(horizontal = 18.dp)
                .padding(top = 18.dp, bottom = 24.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Explore Events",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    color = SquadTextPrimary,
                    modifier = Modifier.weight(1f)
                )

                IconButton(onClick = onMapClick) {
                    Icon(
                        imageVector = Icons.Outlined.Map,
                        contentDescription = "Map",
                        tint = SquadTextPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            EventsSearchField(
                value = searchQuery,
                onValueChange = onSearchQueryChange
            )

            Spacer(modifier = Modifier.height(12.dp))

            com.example.squadup.ui.components.EventFiltersRow(
                filters = filters,
                onFilterClick = onFilterClick
            )

            Spacer(modifier = Modifier.height(12.dp))

            FeaturedEventCard(onClick = onFeaturedEventClick)

            Spacer(modifier = Modifier.height(18.dp))

            UpcomingEventsCard(
                events = upcomingEvents,
                onViewCalendarClick = onViewCalendarClick,
                onFilterByTeamsClick = onFilterByTeamsClick
            )

            Spacer(modifier = Modifier.height(22.dp))

            events.forEach { event ->
                ExploreEventCard(
                    event = event,
                    onActionClick = onEventActionClick
                )

                Spacer(modifier = Modifier.height(22.dp))
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ExploreEventsScreenPreview() {
    var searchQuery by remember { mutableStateOf("") }

    ExploreEventsScreen(
        selectedRoute = AppRoutes.EVENTS,
        searchQuery = searchQuery,
        onSearchQueryChange = { searchQuery = it },
        onMapClick = {},
        onFilterClick = {},
        onFeaturedEventClick = {},
        onViewCalendarClick = {},
        onFilterByTeamsClick = {},
        onEventActionClick = {},
        onNotificationsClick = {},
        onSettingsClick = {},
        onNavItemClick = {}
    )
}
