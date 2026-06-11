package com.example.squadup.features.events.manageevent.manageeventtabs

import com.example.squadup.core.ui.components.responsiveHorizontalPadding

import androidx.compose.material3.MaterialTheme

import com.example.squadup.features.events.manageevent.*
import com.example.squadup.core.enums.GameStatus
import com.example.squadup.core.enums.TeamEventStatus

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.squadup.R
import com.example.squadup.core.ui.theme.*

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun TeamsTabContent(
    uiState: ManageEventUiState,
    onSearchQueryChange: (String) -> Unit,
    onFreeAgentSearchQueryChange: (String) -> Unit,
    onLoadMoreTeams: () -> Unit,
    onLoadMoreFreeAgents: () -> Unit,
    onTeamExpand: (String) -> Unit,
    onAddPlayerClick: (String) -> Unit,
    onEditTeamClick: (String) -> Unit,
    onDeleteTeamClick: (String) -> Unit,
    onPlayerRemove: (String, String) -> Unit,
    onAcceptIndividualRegistration: (Int) -> Unit,
    onRejectIndividualRegistration: (Int) -> Unit,
    onAcceptTeamRegistration: (Int) -> Unit,
    onRejectTeamRegistration: (Int) -> Unit,
) {
    val filteredTeams = if (uiState.teamSearchQuery.isBlank()) uiState.teams
    else uiState.teams.filter { it.name.contains(uiState.teamSearchQuery, ignoreCase = true) }

    val visibleTeams = filteredTeams.take(uiState.teamsDisplayCount)
    val hasMoreTeams = filteredTeams.size > uiState.teamsDisplayCount

    val filteredFreeAgents = if (uiState.freeAgentSearchQuery.isBlank()) uiState.freeAgents
    else uiState.freeAgents.filter {
        it.name.contains(uiState.freeAgentSearchQuery, ignoreCase = true) ||
        it.position.contains(uiState.freeAgentSearchQuery, ignoreCase = true)
    }
    val visibleFreeAgents = filteredFreeAgents.take(uiState.freeAgentsDisplayCount)
    val hasMoreFreeAgents = filteredFreeAgents.size > uiState.freeAgentsDisplayCount

    val teamsMetrics = buildList {
        if (uiState.allowTeams) add(Triple(Icons.Outlined.Groups, stringResource(R.string.manageEvent_teams_label), "${uiState.registeredTeams}/${uiState.maxTeams}"))
        if (uiState.allowFreeAgents) add(Triple(Icons.Outlined.PersonAdd, stringResource(R.string.manageEvent_free_agents_label), "${uiState.freeAgents.size}"))
        add(Triple(Icons.Outlined.Person, stringResource(R.string.manageEvent_players_label), "${uiState.activePlayers}"))
        if (uiState.waitlistItems.isNotEmpty()) add(Triple(Icons.Outlined.HourglassBottom, stringResource(R.string.manageEvent_waitlist_label), "${uiState.waitlistItems.size}"))
    }
    val warningIdx = if (uiState.waitlistItems.isNotEmpty()) setOf(teamsMetrics.lastIndex) else emptySet()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 88.dp)
    ) {
        // Stats metrics
        item {
            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)) {
                MetricRow(teamsMetrics, warningIdx)
            }
        }

        // ── Secção: Equipas Inscritas ─────────────────────────────────────
        if (uiState.individualRegistrationRequests.isNotEmpty()) {
            item {
                Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)) {
                    TabSectionHeader(
                        label = "Pedidos de participação",
                        count = uiState.individualRegistrationRequests.size,
                        isWarning = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(12.dp),
                        shadowElevation = 2.dp
                    ) {
                        Column(modifier = Modifier.padding(vertical = 4.dp)) {
                            uiState.individualRegistrationRequests.forEachIndexed { index, request ->
                                IndividualRegistrationRequestRow(
                                    request = request,
                                    isLoading = uiState.activeRegistrationActionId == request.registrationId,
                                    actionsEnabled = uiState.activeRegistrationActionId == null ||
                                            uiState.activeRegistrationActionId == request.registrationId,
                                    onAccept = { onAcceptIndividualRegistration(request.registrationId) },
                                    onReject = { onRejectIndividualRegistration(request.registrationId) }
                                )
                                if (index < uiState.individualRegistrationRequests.lastIndex) {
                                    HorizontalDivider(
                                        modifier = Modifier.padding(horizontal = responsiveHorizontalPadding(16.dp)),
                                        color = SquadGrayLight
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        if (uiState.teamRegistrationRequests.isNotEmpty()) {
            item {
                Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)) {
                    TabSectionHeader(
                        label = "Pedidos de equipa",
                        count = uiState.teamRegistrationRequests.size,
                        isWarning = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(12.dp),
                        shadowElevation = 2.dp
                    ) {
                        Column(modifier = Modifier.padding(vertical = 4.dp)) {
                            uiState.teamRegistrationRequests.forEachIndexed { index, request ->
                                TeamRegistrationRequestRow(
                                    request = request,
                                    isLoading = uiState.activeRegistrationActionId == request.registrationId,
                                    actionsEnabled = uiState.activeRegistrationActionId == null ||
                                            uiState.activeRegistrationActionId == request.registrationId,
                                    onAccept = { onAcceptTeamRegistration(request.registrationId) },
                                    onReject = { onRejectTeamRegistration(request.registrationId) }
                                )
                                if (index < uiState.teamRegistrationRequests.lastIndex) {
                                    HorizontalDivider(
                                        modifier = Modifier.padding(horizontal = responsiveHorizontalPadding(16.dp)),
                                        color = SquadGrayLight
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        if (uiState.allowTeams) {
            stickyHeader {
                Column(modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(horizontal = 20.dp, vertical = 8.dp)
                ) {
                    TabSectionHeader(
                        label = stringResource(R.string.manageEvent_section_teams),
                        count = filteredTeams.size
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = uiState.teamSearchQuery,
                        onValueChange = onSearchQueryChange,
                        placeholder = { Text(stringResource(R.string.manageEvent_search_team), color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp) },
                        leadingIcon = { Icon(Icons.Outlined.Search, null, tint = MaterialTheme.colorScheme.onSurfaceVariant) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SquadOrange,
                            unfocusedBorderColor = Color.Transparent,
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface
                        )
                    )
                }
            }

            items(visibleTeams, key = { it.id }) { team ->
                TeamAccordionCard(
                    team = team,
                    isExpanded = uiState.expandedTeamId == team.id,
                    onExpand = { onTeamExpand(team.id) },
                    onAddPlayer = { onAddPlayerClick(team.id) },
                    onEditTeam = { onEditTeamClick(team.id) },
                    onDeleteTeam = { onDeleteTeamClick(team.id) },
                    onRemovePlayer = { playerId -> onPlayerRemove(team.id, playerId) },
                    modifier = Modifier.padding(horizontal = responsiveHorizontalPadding(20.dp))
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            if (hasMoreTeams) {
                item { LoadMoreButton(remaining = filteredTeams.size - visibleTeams.size, onClick = onLoadMoreTeams) }
            }
        }

        // ── Secção: Agentes Livres ────────────────────────────────────────
        if (uiState.allowFreeAgents && uiState.freeAgents.isNotEmpty()) {
            stickyHeader {
                Column(modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(horizontal = 20.dp, vertical = 8.dp)
                ) {
                    TabSectionHeader(
                        label = stringResource(R.string.manageEvent_section_free_agents),
                        count = filteredFreeAgents.size
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(SquadOrangeLight, RoundedCornerShape(8.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Outlined.Info, null, tint = SquadOrange, modifier = Modifier.size(13.dp))
                        Text(stringResource(R.string.manageEvent_free_agents_hint), fontSize = 11.sp, color = SquadOrange)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = uiState.freeAgentSearchQuery,
                        onValueChange = onFreeAgentSearchQueryChange,
                        placeholder = { Text(stringResource(R.string.manageEvent_search_free_agent), color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp) },
                        leadingIcon = { Icon(Icons.Outlined.Search, null, tint = MaterialTheme.colorScheme.onSurfaceVariant) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SquadOrange,
                            unfocusedBorderColor = Color.Transparent,
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface
                        )
                    )
                }
            }

            item {
                Surface(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = responsiveHorizontalPadding(20.dp)),
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(12.dp),
                    shadowElevation = 2.dp
                ) {
                    Column(modifier = Modifier.padding(vertical = 4.dp)) {
                        visibleFreeAgents.forEachIndexed { index, agent ->
                            FreeAgentRow(agent)
                            if (index < visibleFreeAgents.lastIndex) {
                                HorizontalDivider(modifier = Modifier.padding(horizontal = responsiveHorizontalPadding(16.dp)), color = SquadGrayLight)
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            if (hasMoreFreeAgents) {
                item { LoadMoreButton(remaining = filteredFreeAgents.size - visibleFreeAgents.size, onClick = onLoadMoreFreeAgents) }
            }
        }

        // ── Secção: Lista de Espera ───────────────────────────────────────
        if (uiState.waitlistItems.isNotEmpty()) {
            stickyHeader {
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(horizontal = 20.dp, vertical = 8.dp)
                ) {
                    TabSectionHeader(
                        label = stringResource(R.string.manageEvent_section_waitlist),
                        count = uiState.waitlistItems.size,
                        isWarning = true
                    )
                }
            }

            item {
                Surface(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = responsiveHorizontalPadding(20.dp)),
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(12.dp),
                    shadowElevation = 2.dp
                ) {
                    Column(modifier = Modifier.padding(vertical = 4.dp)) {
                        uiState.waitlistItems.forEachIndexed { index, item ->
                            WaitlistRow(item)
                            if (index < uiState.waitlistItems.lastIndex) {
                                HorizontalDivider(modifier = Modifier.padding(horizontal = responsiveHorizontalPadding(16.dp)), color = SquadGrayLight)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
internal fun LoadMoreButton(remaining: Int, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        TextButton(onClick = onClick) {
            Icon(Icons.Outlined.ExpandMore, null, modifier = Modifier.size(16.dp), tint = SquadOrange)
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = stringResource(R.string.manageEvent_load_more, remaining),
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = SquadOrange
            )
        }
    }
    Spacer(modifier = Modifier.height(4.dp))
}

@Composable
internal fun TabSectionHeader(
    label: String,
    count: Int,
    modifier: Modifier = Modifier,
    isWarning: Boolean = false
) {
    val color = if (isWarning) Color(0xFFFFA000) else MaterialTheme.colorScheme.onSurfaceVariant
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(label, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = color, letterSpacing = 0.5.sp)
        Box(
            modifier = Modifier
                .background(if (isWarning) Color(0xFFFFF3CD) else SquadOrangeLight, RoundedCornerShape(999.dp))
                .padding(horizontal = 8.dp, vertical = 2.dp)
        ) {
            Text("$count", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (isWarning) Color(0xFFFFA000) else SquadOrange)
        }
    }
}

@Composable
private fun TeamEventStatusBadge(status: TeamEventStatus) {
    val (labelRes, color) = when (status) {
        TeamEventStatus.PENDING       -> R.string.manageEvent_team_status_pending       to Color(0xFFFFA000)
        TeamEventStatus.INCOMPLETE    -> R.string.manageEvent_team_status_incomplete    to SquadError
        TeamEventStatus.CONFIRMED     -> R.string.manageEvent_team_status_confirmed     to Color(0xFF2E7D32)
        TeamEventStatus.WITHDRAWN     -> R.string.manageEvent_team_status_withdrawn     to SquadGrayDark
        TeamEventStatus.ELIMINATED    -> R.string.manageEvent_team_status_eliminated    to Color(0xFF607D8B)
        TeamEventStatus.DISQUALIFIED  -> R.string.manageEvent_team_status_disqualified  to SquadError
    }
    Text(
        text = stringResource(labelRes),
        fontSize = 9.sp,
        fontWeight = FontWeight.Bold,
        color = color,
        modifier = Modifier
            .background(color.copy(alpha = 0.1f), RoundedCornerShape(4.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp)
    )
}

@Composable
private fun TeamAccordionCard(
    team: ManageTeamItem,
    isExpanded: Boolean,
    onExpand: () -> Unit,
    onAddPlayer: () -> Unit,
    onEditTeam: () -> Unit,
    onDeleteTeam: () -> Unit,
    onRemovePlayer: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(12.dp),
        shadowElevation = 2.dp
    ) {
        Column {
            // Header — sempre visível
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onExpand)
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(SquadOrange, RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(team.abbreviation, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(team.name, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                        TeamEventStatusBadge(team.eventStatus)
                    }
                    Text("${team.playerCount} Players • ${team.badge}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Icon(
                    imageVector = if (isExpanded) Icons.Outlined.KeyboardArrowUp else Icons.Outlined.KeyboardArrowDown,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(22.dp)
                )
            }

            // Conteúdo expandido
            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Column(modifier = Modifier.padding(horizontal = responsiveHorizontalPadding(14.dp))) {
                    HorizontalDivider(color = SquadGrayLight)
                    Spacer(modifier = Modifier.height(10.dp))

                    // Localização + ações
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Outlined.LocationOn, null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(13.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(team.location, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.weight(1f))
                        IconButton(onClick = onEditTeam, modifier = Modifier.size(32.dp)) {
                            Icon(Icons.Outlined.Edit, null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(16.dp))
                        }
                        IconButton(onClick = onDeleteTeam, modifier = Modifier.size(32.dp)) {
                            Icon(Icons.Outlined.Delete, null, tint = SquadError, modifier = Modifier.size(16.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Header da lista de jogadores
                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Players",
                            fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = stringResource(R.string.manageEvent_add_player),
                            fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = SquadOrange,
                            modifier = Modifier.clickable(onClick = onAddPlayer)
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(modifier = Modifier.fillMaxWidth()) {
                        Text("Name", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.weight(1f))
                        Text("State", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.width(90.dp))
                    }

                    HorizontalDivider(color = SquadGrayLight, modifier = Modifier.padding(vertical = 6.dp))

                    team.players.forEach { player ->
                        PlayerRow(player = player, onRemove = { onRemovePlayer(player.id) })
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
private fun PlayerRow(player: ManagePlayerItem, onRemove: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(30.dp)
                .background(SquadOrangeLight, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(player.initials, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = SquadOrange)
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(player.name, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.weight(1f))
        IconButton(onClick = onRemove, modifier = Modifier.size(28.dp)) {
            Icon(Icons.Outlined.Close, null, tint = Color(0xFFD32F2F), modifier = Modifier.size(16.dp))
        }
    }
}

@Composable
private fun FreeAgentRow(agent: FreeAgentItem) {
    val expLabel = when (agent.experienceLevel) {
        1 -> stringResource(R.string.manageEvent_exp_beginner)
        2 -> stringResource(R.string.manageEvent_exp_intermediate)
        else -> stringResource(R.string.manageEvent_exp_advanced)
    }
    val expColor = when (agent.experienceLevel) {
        1 -> Color(0xFF2E7D32)
        2 -> Color(0xFF1565C0)
        else -> SquadOrange
    }
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(36.dp).background(SquadOrangeLight, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(agent.initials, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SquadOrange)
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(agent.name, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
            Text(agent.position, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Surface(color = expColor.copy(alpha = 0.1f), shape = RoundedCornerShape(6.dp)) {
            Text(
                text = expLabel,
                fontSize = 10.sp, fontWeight = FontWeight.Bold, color = expColor,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
            )
        }
    }
}

@Composable
private fun WaitlistRow(item: WaitlistItem) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Posição na fila
        Box(
            modifier = Modifier
                .size(28.dp)
                .background(Color(0xFFFFF3CD), RoundedCornerShape(6.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text("${item.waitlistPosition}", fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFFFFA000))
        }
        Spacer(modifier = Modifier.width(10.dp))
        Box(
            modifier = Modifier.size(36.dp).background(SquadGrayLight, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(item.initials, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(item.name, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
            Text(
                text = if (item.isTeam) stringResource(R.string.manageEvent_waitlist_team) else stringResource(R.string.manageEvent_waitlist_player),
                fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        // Promote
        Surface(
            modifier = Modifier.clickable { },
            color = Color(0xFFE8F5E9),
            shape = RoundedCornerShape(6.dp)
        ) {
            Text(
                text = stringResource(R.string.manageEvent_waitlist_promote),
                fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32),
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
        Spacer(modifier = Modifier.width(6.dp))
        IconButton(onClick = {}, modifier = Modifier.size(28.dp)) {
            Icon(Icons.Outlined.Close, null, tint = SquadError, modifier = Modifier.size(14.dp))
        }
    }
}

@Composable
private fun CurrentGameBannerCard(game: CurrentGameBanner, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = SquadOrange,
        shape = RoundedCornerShape(14.dp)
    ) {
        Box(modifier = Modifier.padding(16.dp)) {
            Column {
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = game.label,
                        fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White.copy(alpha = 0.85f),
                        letterSpacing = 0.5.sp, modifier = Modifier.weight(1f)
                    )
                    Text(game.venue, fontSize = 10.sp, color = Color.White.copy(alpha = 0.85f))
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    // Home team
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                        Box(
                            modifier = Modifier
                                .size(52.dp)
                                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.2f), RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(game.homeTeamAbbr, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(game.homeTeamName, fontSize = 9.sp, fontWeight = FontWeight.Medium, color = Color.White.copy(alpha = 0.9f), textAlign = TextAlign.Center)
                    }
                    Text("VS", fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = Color.White, modifier = Modifier.padding(horizontal = responsiveHorizontalPadding(16.dp)))
                    // Away team
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                        Box(
                            modifier = Modifier
                                .size(52.dp)
                                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.2f), RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(game.awayTeamAbbr, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(game.awayTeamName, fontSize = 9.sp, fontWeight = FontWeight.Medium, color = Color.White.copy(alpha = 0.9f), textAlign = TextAlign.Center)
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }
            // Botão + no canto inferior direito
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(MaterialTheme.colorScheme.surface, CircleShape)
                    .align(Alignment.BottomEnd)
                    .clickable { },
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.Add, null, tint = SquadOrange, modifier = Modifier.size(20.dp))
            }
        }
    }
}