package com.example.squadup.features.events.manageevent

import androidx.compose.material3.MaterialTheme

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.squadup.R
import com.example.squadup.core.ui.components.AppHeader
import com.example.squadup.core.ui.components.AppNavBar
import com.example.squadup.core.ui.components.responsiveHorizontalPadding
import com.example.squadup.core.ui.theme.*
import com.example.squadup.core.utils.AppLanguage
import com.example.squadup.core.utils.clickableNoRipple
import com.example.squadup.features.events.manageevent.manageeventtabs.*

@Composable
fun ManageEventScreen(
    uiState: ManageEventUiState,
    selectedRoute: String,
    onNavItemClick: (String) -> Unit,
    onBackClick: () -> Unit,
    onTabChange: (ManageEventTab) -> Unit,
    onTeamSearchQueryChange: (String) -> Unit,
    onFreeAgentSearchQueryChange: (String) -> Unit,
    onLoadMoreTeams: () -> Unit,
    onLoadMoreFreeAgents: () -> Unit,
    onGameSearchQueryChange: (String) -> Unit,
    onTeamExpand: (String) -> Unit,
    onEditGameClick: (String) -> Unit,
    onCreateGameClick: () -> Unit,
    onDismissCreateGameDialog: () -> Unit,
    onCreateGameHomeTeamChange: (String) -> Unit,
    onCreateGameAwayTeamChange: (String) -> Unit,
    onCreateGameDateChange: (String) -> Unit,
    onCreateGameTimeChange: (String) -> Unit,
    onCreateGameVenueChange: (String) -> Unit,
    onConfirmCreateGame: () -> Unit,
    onDismissEditGameDialog: () -> Unit,
    onEditGameHomeTeamChange: (String) -> Unit,
    onEditGameAwayTeamChange: (String) -> Unit,
    onEditGameDateChange: (String) -> Unit,
    onEditGameTimeChange: (String) -> Unit,
    onEditGameVenueChange: (String) -> Unit,
    onConfirmEditGame: () -> Unit,
    onFormTeamsClick: () -> Unit,
    onEditEventClick: () -> Unit,
    onStatusActionClick: () -> Unit,
    onCancelEventClick: () -> Unit,
    onAcceptIndividualRegistration: (Int) -> Unit,
    onRejectIndividualRegistration: (Int) -> Unit,
    onAcceptTeamRegistration: (Int) -> Unit,
    onRejectTeamRegistration: (Int) -> Unit,
    onViewAllRegistrationsClick: () -> Unit,
    onScanTicketsClick: () -> Unit = {},
    onManageLiveClick: (String) -> Unit = {},
    isAdmin: Boolean,
    isAdminView: Boolean,
    selectedLanguage: AppLanguage,
    isDarkMode: Boolean,
    onAdminViewChange: (Boolean) -> Unit,
    onLanguageChange: (AppLanguage) -> Unit,
    onDarkModeChange: (Boolean) -> Unit,
) {
    // Edit Game Dialog
    if (uiState.showEditGameDialog) {
        EditGameDialog(
            uiState = uiState,
            onDismiss = onDismissEditGameDialog,
            onHomeTeamChange = onEditGameHomeTeamChange,
            onAwayTeamChange = onEditGameAwayTeamChange,
            onDateChange = onEditGameDateChange,
            onTimeChange = onEditGameTimeChange,
            onVenueChange = onEditGameVenueChange,
            onConfirm = onConfirmEditGame
        )
    }

    // Create Game Dialog
    if (uiState.showCreateGameDialog) {
        CreateGameDialog(
            uiState = uiState,
            onDismiss = onDismissCreateGameDialog,
            onHomeTeamChange = onCreateGameHomeTeamChange,
            onAwayTeamChange = onCreateGameAwayTeamChange,
            onDateChange = onCreateGameDateChange,
            onTimeChange = onCreateGameTimeChange,
            onVenueChange = onCreateGameVenueChange,
            onConfirm = onConfirmCreateGame
        )
    }

    // Tabs adaptativas: depende do formato + tipo de participação
    val hasPlayerManagement = uiState.allowTeams || uiState.allowFreeAgents
    val tabs = buildList {
        add(ManageEventTab.OVERVIEW)
        if (hasPlayerManagement) add(ManageEventTab.TEAMS)
        if (uiState.isSingleMatch) add(ManageEventTab.MATCH) else add(ManageEventTab.GAMES)
        add(ManageEventTab.STATS)
    }

    Scaffold(
        topBar = {
            AppHeader(
                showLogo = false,
                showBackButton = true,
                onBackClick = onBackClick,
                title = stringResource(R.string.manageEvent_title),
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
            when (uiState.selectedTab) {
                ManageEventTab.GAMES -> FloatingActionButton(
                    onClick = onCreateGameClick,
                    containerColor = SquadOrange,
                    contentColor = Color.White
                ) {
                    Icon(Icons.Filled.Add, contentDescription = null)
                }
                ManageEventTab.TEAMS -> FloatingActionButton(
                    onClick = onScanTicketsClick,
                    containerColor = SquadOrange,
                    contentColor = Color.White
                ) {
                    Icon(Icons.Outlined.QrCodeScanner, contentDescription = "Scan bilhete")
                }
                else -> {}
            }
        }
    ) { innerPadding ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = SquadOrange)
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            EventHeroCard(
                uiState = uiState,
                modifier = Modifier.padding(horizontal = responsiveHorizontalPadding(20.dp))
            )

            ManageEventTabRow(
                tabs = tabs,
                selectedTab = uiState.selectedTab,
                onTabChange = onTabChange
            )

            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .align(Alignment.CenterHorizontally),
                thickness = 1.dp,
                color = SquadGray
            )

            uiState.errorMessage?.let { message ->
                Text(
                    text = message,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = SquadError,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 8.dp)
                )
            }

            when (uiState.selectedTab) {
                ManageEventTab.OVERVIEW -> OverviewTabContent(
                    uiState = uiState,
                    onFormTeamsClick = onFormTeamsClick,
                    onEditEventClick = onEditEventClick,
                    onStatusActionClick = onStatusActionClick,
                    onCancelEventClick = onCancelEventClick,
                    onViewAllRegistrationsClick = onViewAllRegistrationsClick,
                    onAcceptIndividualRegistration = onAcceptIndividualRegistration,
                    onRejectIndividualRegistration = onRejectIndividualRegistration,
                    onAcceptTeamRegistration = onAcceptTeamRegistration,
                    onRejectTeamRegistration = onRejectTeamRegistration,
                )
                ManageEventTab.TEAMS -> TeamsTabContent(
                    uiState = uiState,
                    onSearchQueryChange = onTeamSearchQueryChange,
                    onFreeAgentSearchQueryChange = onFreeAgentSearchQueryChange,
                    onLoadMoreTeams = onLoadMoreTeams,
                    onLoadMoreFreeAgents = onLoadMoreFreeAgents,
                    onTeamExpand = onTeamExpand,
                    onAcceptIndividualRegistration = onAcceptIndividualRegistration,
                    onRejectIndividualRegistration = onRejectIndividualRegistration,
                    onAcceptTeamRegistration = onAcceptTeamRegistration,
                    onRejectTeamRegistration = onRejectTeamRegistration,
                )
                ManageEventTab.GAMES -> GamesTabContent(
                    uiState = uiState,
                    onSearchQueryChange = onGameSearchQueryChange,
                    onEditGameClick = onEditGameClick,
                    onManageLiveClick = onManageLiveClick,
                )
                ManageEventTab.MATCH -> MatchTabContent(
                    uiState = uiState,
                    onManageLiveClick = onManageLiveClick,
                    onEditMatchClick = { onEditGameClick(uiState.scheduledGames.firstOrNull()?.id ?: "") },
                    onTeamExpand = onTeamExpand,
                    onCreateGameClick = onCreateGameClick,
                )
                ManageEventTab.STATS -> StatsTabContent(uiState = uiState)
            }
        }
    }
}

// ─── Tab Row adaptativo ───────────────────────────────────────────────────────

@Composable
private fun ManageEventTabRow(
    tabs: List<ManageEventTab>,
    selectedTab: ManageEventTab,
    onTabChange: (ManageEventTab) -> Unit
) {
    val tabLabelRes = mapOf(
        ManageEventTab.OVERVIEW to R.string.manageEvent_tab_overview,
        ManageEventTab.TEAMS    to R.string.manageEvent_tab_teams,
        ManageEventTab.GAMES    to R.string.manageEvent_tab_games,
        ManageEventTab.STATS    to R.string.manageEvent_tab_stats,
        ManageEventTab.MATCH    to R.string.manageEvent_tab_match,
    )

    val n = tabs.size
    val selectedIndex = tabs.indexOf(selectedTab).coerceAtLeast(0)

    val horizontalBias by animateFloatAsState(
        targetValue = if (n <= 1) 0f else -1f + selectedIndex * (2f / (n - 1)),
        animationSpec = tween(durationMillis = 250),
        label = "TabIndicator"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp)
            .height(44.dp)
            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(12.dp))
            .padding(4.dp)
    ) {
        // Pill laranja deslizante
        Box(
            modifier = Modifier
                .fillMaxWidth(1f / n)
                .fillMaxHeight()
                .align(BiasAlignment(horizontalBias, 0f))
                .background(SquadOrange, RoundedCornerShape(10.dp))
        )

        Row(modifier = Modifier.fillMaxSize()) {
            tabs.forEach { tab ->
                val isSelected = selectedTab == tab
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clickableNoRipple { onTabChange(tab) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(tabLabelRes[tab] ?: R.string.manageEvent_tab_overview),
                        fontSize = 13.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

// ─── Edit Game Dialog ─────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditGameDialog(
    uiState: ManageEventUiState,
    onDismiss: () -> Unit,
    onHomeTeamChange: (String) -> Unit,
    onAwayTeamChange: (String) -> Unit,
    onDateChange: (String) -> Unit,
    onTimeChange: (String) -> Unit,
    onVenueChange: (String) -> Unit,
    onConfirm: () -> Unit,
) {
    val sportLabel = when (uiState.sportType) {
        com.example.squadup.core.enums.SportType.SOCCER -> "Futebol"
        com.example.squadup.core.enums.SportType.FUTSAL -> "Futsal"
        com.example.squadup.core.enums.SportType.BASKETBALL -> "Basquetebol"
        com.example.squadup.core.enums.SportType.VOLLEYBALL -> "Voleibol"
        com.example.squadup.core.enums.SportType.PADDLE -> "Padel"
    }

    var homeExpanded by remember { mutableStateOf(false) }
    var awayExpanded by remember { mutableStateOf(false) }

    val homeTeamName = uiState.teams.firstOrNull { it.id == uiState.editGameHomeTeamId }?.name ?: "Selecionar equipa"
    val awayTeamName = uiState.teams.firstOrNull { it.id == uiState.editGameAwayTeamId }?.name ?: "Selecionar equipa"

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.background,
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = responsiveHorizontalPadding(20.dp))
                .padding(bottom = 32.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Editar Jogo", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    Text(sportLabel, fontSize = 13.sp, color = SquadOrange, fontWeight = FontWeight.SemiBold)
                }
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Outlined.Close, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            Spacer(Modifier.height(20.dp))

            Text("Equipa Casa", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
            Spacer(Modifier.height(6.dp))
            ExposedDropdownMenuBox(expanded = homeExpanded, onExpandedChange = { homeExpanded = it }) {
                OutlinedTextField(
                    value = homeTeamName,
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = homeExpanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor(),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = SquadOrange, unfocusedBorderColor = MaterialTheme.colorScheme.outline)
                )
                ExposedDropdownMenu(expanded = homeExpanded, onDismissRequest = { homeExpanded = false }) {
                    uiState.teams.forEach { team ->
                        DropdownMenuItem(
                            text = { Text(team.name) },
                            onClick = { onHomeTeamChange(team.id); homeExpanded = false }
                        )
                    }
                }
            }

            Spacer(Modifier.height(14.dp))

            Text("Equipa Visitante", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
            Spacer(Modifier.height(6.dp))
            ExposedDropdownMenuBox(expanded = awayExpanded, onExpandedChange = { awayExpanded = it }) {
                OutlinedTextField(
                    value = awayTeamName,
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = awayExpanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor(),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = SquadOrange, unfocusedBorderColor = MaterialTheme.colorScheme.outline)
                )
                ExposedDropdownMenu(expanded = awayExpanded, onDismissRequest = { awayExpanded = false }) {
                    uiState.teams.forEach { team ->
                        DropdownMenuItem(
                            text = { Text(team.name) },
                            onClick = { onAwayTeamChange(team.id); awayExpanded = false }
                        )
                    }
                }
            }

            Spacer(Modifier.height(14.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Column(Modifier.weight(1f)) {
                    Text("Data", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                    Spacer(Modifier.height(6.dp))
                    OutlinedTextField(
                        value = uiState.editGameDate,
                        onValueChange = onDateChange,
                        placeholder = { Text("AAAA-MM-DD", fontSize = 13.sp, color = SquadGray) },
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = SquadOrange, unfocusedBorderColor = MaterialTheme.colorScheme.outline)
                    )
                }
                Column(Modifier.weight(1f)) {
                    Text("Hora", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                    Spacer(Modifier.height(6.dp))
                    OutlinedTextField(
                        value = uiState.editGameTime,
                        onValueChange = { v ->
                            val digits = v.filter { it.isDigit() }.take(4)
                            val formatted = if (digits.length >= 3) "${digits.take(2)}:${digits.drop(2)}" else digits
                            onTimeChange(formatted)
                        },
                        placeholder = { Text("HH:MM", fontSize = 13.sp, color = SquadGray) },
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = SquadOrange, unfocusedBorderColor = MaterialTheme.colorScheme.outline)
                    )
                }
            }

            Spacer(Modifier.height(14.dp))

            Text("Local (opcional)", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
            Spacer(Modifier.height(6.dp))
            OutlinedTextField(
                value = uiState.editGameVenue,
                onValueChange = onVenueChange,
                placeholder = { Text("Local do jogo", fontSize = 13.sp, color = SquadGray) },
                singleLine = true,
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Outlined.LocationOn, null, tint = SquadGray, modifier = Modifier.size(18.dp)) },
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = SquadOrange, unfocusedBorderColor = MaterialTheme.colorScheme.outline)
            )

            uiState.editGameError?.let { err ->
                Spacer(Modifier.height(10.dp))
                Text(err, fontSize = 13.sp, color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.SemiBold)
            }

            Spacer(Modifier.height(20.dp))

            Button(
                onClick = onConfirm,
                enabled = !uiState.isEditingGame,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SquadOrange, contentColor = Color.White)
            ) {
                if (uiState.isEditingGame) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp, color = Color.White)
                } else {
                    Icon(Icons.Outlined.Edit, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Guardar Alterações", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// ─── Create Game Dialog ───────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CreateGameDialog(
    uiState: ManageEventUiState,
    onDismiss: () -> Unit,
    onHomeTeamChange: (String) -> Unit,
    onAwayTeamChange: (String) -> Unit,
    onDateChange: (String) -> Unit,
    onTimeChange: (String) -> Unit,
    onVenueChange: (String) -> Unit,
    onConfirm: () -> Unit,
) {
    val sportLabel = when (uiState.sportType) {
        com.example.squadup.core.enums.SportType.SOCCER -> "Futebol"
        com.example.squadup.core.enums.SportType.FUTSAL -> "Futsal"
        com.example.squadup.core.enums.SportType.BASKETBALL -> "Basquetebol"
        com.example.squadup.core.enums.SportType.VOLLEYBALL -> "Voleibol"
        com.example.squadup.core.enums.SportType.PADDLE -> "Padel"
    }

    var homeExpanded by remember { mutableStateOf(false) }
    var awayExpanded by remember { mutableStateOf(false) }

    val homeTeamName = uiState.teams.firstOrNull { it.id == uiState.createGameHomeTeamId }?.name ?: "Selecionar equipa"
    val awayTeamName = uiState.teams.firstOrNull { it.id == uiState.createGameAwayTeamId }?.name ?: "Selecionar equipa"

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.background,
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = responsiveHorizontalPadding(20.dp))
                .padding(bottom = 32.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Criar Jogo", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    Text(sportLabel, fontSize = 13.sp, color = SquadOrange, fontWeight = FontWeight.SemiBold)
                }
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Outlined.Close, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            Spacer(Modifier.height(20.dp))

            // Home team
            Text("Equipa Casa", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
            Spacer(Modifier.height(6.dp))
            ExposedDropdownMenuBox(expanded = homeExpanded, onExpandedChange = { homeExpanded = it }) {
                OutlinedTextField(
                    value = homeTeamName,
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = homeExpanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor(),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = SquadOrange, unfocusedBorderColor = MaterialTheme.colorScheme.outline)
                )
                ExposedDropdownMenu(expanded = homeExpanded, onDismissRequest = { homeExpanded = false }) {
                    uiState.teams.forEach { team ->
                        DropdownMenuItem(
                            text = { Text(team.name) },
                            onClick = { onHomeTeamChange(team.id); homeExpanded = false }
                        )
                    }
                }
            }

            Spacer(Modifier.height(14.dp))

            // Away team
            Text("Equipa Visitante", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
            Spacer(Modifier.height(6.dp))
            ExposedDropdownMenuBox(expanded = awayExpanded, onExpandedChange = { awayExpanded = it }) {
                OutlinedTextField(
                    value = awayTeamName,
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = awayExpanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor(),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = SquadOrange, unfocusedBorderColor = MaterialTheme.colorScheme.outline)
                )
                ExposedDropdownMenu(expanded = awayExpanded, onDismissRequest = { awayExpanded = false }) {
                    uiState.teams.forEach { team ->
                        DropdownMenuItem(
                            text = { Text(team.name) },
                            onClick = { onAwayTeamChange(team.id); awayExpanded = false }
                        )
                    }
                }
            }

            Spacer(Modifier.height(14.dp))

            // Date + Time row
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Column(Modifier.weight(1f)) {
                    Text("Data", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                    Spacer(Modifier.height(6.dp))
                    OutlinedTextField(
                        value = uiState.createGameDate,
                        onValueChange = onDateChange,
                        placeholder = { Text("AAAA-MM-DD", fontSize = 13.sp, color = SquadGray) },
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = SquadOrange, unfocusedBorderColor = MaterialTheme.colorScheme.outline)
                    )
                }
                Column(Modifier.weight(1f)) {
                    Text("Hora", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                    Spacer(Modifier.height(6.dp))
                    OutlinedTextField(
                        value = uiState.createGameTime,
                        onValueChange = onCreateGameTimeChange@{ v ->
                            // auto-insert colon: "1430" → "14:30"
                            val digits = v.filter { it.isDigit() }.take(4)
                            val formatted = if (digits.length >= 3) "${digits.take(2)}:${digits.drop(2)}" else digits
                            onTimeChange(formatted)
                        },
                        placeholder = { Text("HH:MM", fontSize = 13.sp, color = SquadGray) },
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = SquadOrange, unfocusedBorderColor = MaterialTheme.colorScheme.outline)
                    )
                }
            }

            Spacer(Modifier.height(14.dp))

            // Venue
            Text("Local (opcional)", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
            Spacer(Modifier.height(6.dp))
            OutlinedTextField(
                value = uiState.createGameVenue,
                onValueChange = onVenueChange,
                placeholder = { Text("Local do jogo", fontSize = 13.sp, color = SquadGray) },
                singleLine = true,
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Outlined.LocationOn, null, tint = SquadGray, modifier = Modifier.size(18.dp)) },
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = SquadOrange, unfocusedBorderColor = MaterialTheme.colorScheme.outline)
            )

            // Error
            uiState.createGameError?.let { err ->
                Spacer(Modifier.height(10.dp))
                Text(err, fontSize = 13.sp, color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.SemiBold)
            }

            Spacer(Modifier.height(20.dp))

            Button(
                onClick = onConfirm,
                enabled = !uiState.isCreatingGame,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SquadOrange, contentColor = Color.White)
            ) {
                if (uiState.isCreatingGame) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp, color = Color.White)
                } else {
                    Icon(Icons.Outlined.Add, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Criar Jogo", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
