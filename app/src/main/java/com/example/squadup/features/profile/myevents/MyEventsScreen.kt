package com.example.squadup.features.profile.myevents

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.BroadcastOnHome
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.squadup.R
import com.example.squadup.core.enums.EventStatus
import com.example.squadup.core.ui.components.AppHeader
import com.example.squadup.core.ui.components.AppNavBar
import com.example.squadup.core.ui.components.EmptyStateCard
import com.example.squadup.core.ui.components.SquadFab
import com.example.squadup.core.ui.theme.*
import com.example.squadup.core.utils.AppLanguage
import com.example.squadup.core.utils.toIcon
import com.example.squadup.core.utils.toLabel

@Composable
fun MyEventsScreen(
    uiState: MyEventsUiState,
    selectedRoute: String,
    onNavItemClick: (String) -> Unit,
    onBackClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onFilterChange: (MyEventsFilter) -> Unit,
    onManageEventClick: (String) -> Unit,
    onViewResultsClick: (String) -> Unit,
    onCreateEventClick: () -> Unit,
    isAdmin: Boolean,
    isAdminView: Boolean,
    selectedLanguage: AppLanguage,
    isDarkMode: Boolean,
    onAdminViewChange: (Boolean) -> Unit,
    onLanguageChange: (AppLanguage) -> Unit,
    onDarkModeChange: (Boolean) -> Unit,
) {
    val context = LocalContext.current

    Scaffold(
        topBar = {
            AppHeader(
                showLogo = false,
                title = stringResource(R.string.myEvents_title),
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
        },
        floatingActionButton = {
            SquadFab(onClick = onCreateEventClick)
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(SquadBackground)
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(bottom = 80.dp) // espaço para o FAB
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Search bar
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = onSearchQueryChange,
                placeholder = {
                    Text(
                        text = stringResource(R.string.myEvents_search_placeholder),
                        color = SquadTextSecondary,
                        fontSize = 14.sp
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Search,
                        contentDescription = null,
                        tint = SquadTextSecondary
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = SquadOrange,
                    unfocusedBorderColor = Color.Transparent,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                )
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Filter chips
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                MyEventsFilter.entries.forEach { filter ->
                    val selected = uiState.selectedFilter == filter
                    val label = when (filter) {
                        MyEventsFilter.All       -> stringResource(R.string.myEvents_filter_all)
                        MyEventsFilter.Active    -> stringResource(R.string.myEvents_filter_active)
                        MyEventsFilter.Completed -> stringResource(R.string.myEvents_filter_completed)
                    }
                    FilterChip(
                        selected = selected,
                        onClick = { onFilterChange(filter) },
                        label = {
                            Text(
                                text = label,
                                fontSize = 13.sp,
                                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = SquadOrange,
                            selectedLabelColor = Color.White,
                            containerColor = Color.White,
                            labelColor = SquadTextSecondary
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = selected,
                            selectedBorderColor = SquadOrange,
                            borderColor = SquadGray
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Event list
            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = SquadOrange)
                }
            } else if (uiState.errorMessage != null) {
                EmptyStateCard(
                    title = "Erro",
                    message = uiState.errorMessage,
                    icon = Icons.Default.CalendarMonth,
                    actionText = "Tentar novamente",
                    onActionClick = {}
                )
            } else if (uiState.filteredEvents.isEmpty()) {
                EmptyStateCard(
                    title = "Sem eventos",
                    message = "Ainda não tens eventos nesta categoria. Cria um evento para começar!",
                    icon = Icons.Default.CalendarMonth,
                    actionText = "Criar Evento",
                    onActionClick = onCreateEventClick
                )
            } else {
                uiState.filteredEvents.forEach { event ->
                    when (event.status) {
                        EventStatus.FINISHED, EventStatus.CANCELLED -> {
                            MyEventCompletedCard(
                                event = event,
                                onViewResultsClick = { onViewResultsClick(event.id) }
                            )
                        }
                        else -> {
                            MyEventActiveCard(
                                event = event,
                                onManageClick = { onManageEventClick(event.id) }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
private fun MyEventActiveCard(
    event: MyEventItem,
    onManageClick: () -> Unit
) {
    val context = LocalContext.current

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        shape = RoundedCornerShape(12.dp),
        shadowElevation = 2.dp
    ) {
        Column {
            // Image area with sport color + status badge
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .background(event.sportType.color)
            ) {
                Icon(
                    imageVector = event.sportType.toIcon(),
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.25f),
                    modifier = Modifier
                        .size(130.dp)
                        .align(Alignment.Center)
                )
                
                val badgeColor = when(event.status) {
                    EventStatus.REGISTRATION_OPEN -> Color(0xFF00BFA5) // Teal
                    EventStatus.ONGOING -> SquadOrange
                    else -> SquadGrayDark
                }

                Surface(
                    color = badgeColor,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(16.dp)
                ) {
                    Text(
                        text = event.status.toLabel(context),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }

            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = event.title,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = SquadTextPrimary
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Info row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    InfoColumn(
                        label = stringResource(R.string.myEvents_teams_label),
                        value = event.teamsCount.toString()
                    )
                    InfoColumn(
                        label = stringResource(R.string.myEvents_date_label),
                        value = event.date
                    )
                    InfoColumn(
                        label = stringResource(R.string.myEvents_venue_label),
                        value = event.location
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (event.status == EventStatus.ONGOING && event.matchesInProgress > 0) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(SquadOrange, RoundedCornerShape(999.dp))
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = stringResource(R.string.myEvents_matches_in_progress, event.matchesInProgress),
                                fontSize = 13.sp,
                                color = SquadTextSecondary
                            )
                        }
                        
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable(onClick = onManageClick)
                        ) {
                            Text(
                                text = stringResource(R.string.myEvents_live),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = SquadOrange
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Outlined.BroadcastOnHome,
                                contentDescription = null,
                                tint = SquadOrange,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    } else {
                        // Registration open - show participant count / avatars placeholder
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Groups,
                                contentDescription = null,
                                tint = SquadTextSecondary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "+${event.registeredCount}",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = SquadTextSecondary
                            )
                        }
                        
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable(onClick = onManageClick)
                        ) {
                            Text(
                                text = stringResource(R.string.myEvents_manage),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = SquadOrange
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.AutoMirrored.Outlined.ArrowForward,
                                contentDescription = null,
                                tint = SquadOrange,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MyEventCompletedCard(
    event: MyEventItem,
    onViewResultsClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        shape = RoundedCornerShape(12.dp),
        shadowElevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = event.title,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = SquadTextPrimary,
                    modifier = Modifier.weight(1f)
                )
                
                Surface(
                    color = Color(0xFFE8EDF2),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "COMPLETED",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF5F738C),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Groups,
                        contentDescription = null,
                        tint = SquadTextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = stringResource(R.string.myEvents_players, event.playersCount),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = SquadTextSecondary
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CalendarMonth,
                        contentDescription = null,
                        tint = SquadTextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = event.date,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = SquadTextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = onViewResultsClick,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFE8EDF2),
                    contentColor = Color(0xFF1F1F1F)
                ),
                contentPadding = PaddingValues(vertical = 12.dp)
            ) {
                Text(
                    text = stringResource(R.string.myEvents_view_results),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun InfoColumn(label: String, value: String) {
    Column {
        Text(
            text = label.uppercase(),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFAAAAAA),
            letterSpacing = 0.5.sp
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            color = SquadTextPrimary
        )
    }
}

