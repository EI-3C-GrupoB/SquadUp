package com.example.squadup.features.events.manageevent.manageeventtabs

import com.example.squadup.core.ui.components.responsiveHorizontalPadding

import androidx.compose.material3.MaterialTheme

import com.example.squadup.features.events.manageevent.*
import com.example.squadup.core.enums.GameStatus

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.squadup.R
import com.example.squadup.core.enums.EventFormat
import com.example.squadup.core.enums.EventStatus
import com.example.squadup.core.ui.theme.*
import com.example.squadup.core.utils.toDisplayName
import com.example.squadup.core.utils.toIcon

@Composable
internal fun EventHeroCard(uiState: ManageEventUiState, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(14.dp),
        shadowElevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Nome + Status badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = uiState.eventName,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                StatusBadge(uiState.status)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Badges: Sport + Format + Public/Private
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                // Sport badge
                Surface(
                    color = uiState.sportType.color.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = uiState.sportType.toIcon(),
                            contentDescription = null,
                            tint = uiState.sportType.color,
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            text = uiState.sportType.toDisplayName(context),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = uiState.sportType.color
                        )
                    }
                }
                // Format badge
                if (uiState.formatName.isNotBlank()) {
                    Surface(
                        color = Color(0xFFE3F2FD),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.EmojiEvents,
                                contentDescription = null,
                                tint = Color(0xFF1565C0),
                                modifier = Modifier.size(12.dp)
                            )
                            Text(
                                text = uiState.formatName,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF1565C0)
                            )
                        }
                    }
                }
                // Public/Private badge
                val privacyColor = if (uiState.isPublic) Color(0xFF2E7D32) else Color(0xFF6A1B9A)
                val privacyBg = if (uiState.isPublic) Color(0xFFE8F5E9) else Color(0xFFF3E5F5)
                Surface(
                    color = privacyBg,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = if (uiState.isPublic) Icons.Outlined.Public else Icons.Outlined.Lock,
                            contentDescription = null,
                            tint = privacyColor,
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            text = if (uiState.isPublic)
                                stringResource(R.string.manageEvent_public)
                            else
                                stringResource(R.string.manageEvent_private),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = privacyColor
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider(color = SquadGray)
            Spacer(modifier = Modifier.height(14.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.LocationOn, null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(13.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = uiState.venue, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Spacer(modifier = Modifier.height(3.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.CalendarMonth, null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(13.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = uiState.dateRange, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
internal fun OverviewTabContent(
    uiState: ManageEventUiState,
    onFormTeamsClick: () -> Unit,
    onEditEventClick: () -> Unit,
    onStatusActionClick: () -> Unit,
    onCancelEventClick: () -> Unit,
    onViewAllRegistrationsClick: () -> Unit,
    onAcceptIndividualRegistration: (Int) -> Unit,
    onRejectIndividualRegistration: (Int) -> Unit,
    onAcceptTeamRegistration: (Int) -> Unit,
    onRejectTeamRegistration: (Int) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = responsiveHorizontalPadding(20.dp))
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // 1 — Métricas adaptativas
        MetricsGrid(uiState)

        // Progress — só para torneios (múltiplos jogos)
        if (!uiState.isSingleMatch) {
            Spacer(modifier = Modifier.height(12.dp))
            MatchProgressCard(
                isSingleMatch = false,
                progress = uiState.matchProgress,
                completedGames = uiState.completedGames,
                totalGames = uiState.totalGames
            )
        }

        val hasManagementTools = uiState.status != EventStatus.FINISHED &&
                uiState.status != EventStatus.CANCELLED

        if (hasManagementTools) {
            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "MANAGEMENT TOOLS",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                letterSpacing = 0.5.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Card de ação contextual (muda com o estado)
            StatusActionCard(
                status = uiState.status,
                onActionClick = onStatusActionClick
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Form Teams — for any event that has player/team management and isn't over
        if ((uiState.allowFreeAgents || uiState.allowTeams) &&
            uiState.status != EventStatus.FINISHED &&
            uiState.status != EventStatus.CANCELLED
        ) {
            Button(
                onClick = onFormTeamsClick,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SquadOrange,
                    contentColor = Color.White
                )
            ) {
                Icon(Icons.Outlined.Groups, null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.manageEvent_form_teams),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
        }

        // Edit Details + Cancel Event (ocultos em FINISHED/CANCELLED)
        if (uiState.status != EventStatus.FINISHED && uiState.status != EventStatus.CANCELLED) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ManagementToolCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Outlined.Edit,
                    label = stringResource(R.string.manageEvent_edit_details),
                    onClick = onEditEventClick
                )
                ManagementToolCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Outlined.Cancel,
                    label = stringResource(R.string.manageEvent_cancel_event),
                    onClick = onCancelEventClick,
                    isDestructive = true
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (uiState.individualRegistrationRequests.isNotEmpty()) {
            Text(
                text = "Pedidos de participação",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(10.dp))

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

            Spacer(modifier = Modifier.height(24.dp))
        }

        // 5 — Inscrições recentes
        if (uiState.teamRegistrationRequests.isNotEmpty()) {
            Text(
                text = "Pedidos de equipa",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(10.dp))

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

            Spacer(modifier = Modifier.height(24.dp))
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.manageEvent_recent_registrations),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = stringResource(R.string.manageEvent_view_all),
                fontSize = 13.sp,
                color = SquadOrange,
                modifier = Modifier.clickable(onClick = onViewAllRegistrationsClick)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(12.dp),
            shadowElevation = 2.dp
        ) {
            Column {
                uiState.recentRegistrations.forEachIndexed { index, item ->
                    RecentRegistrationRow(item)
                    if (index < uiState.recentRegistrations.lastIndex) {
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = responsiveHorizontalPadding(16.dp)),
                            color = SquadGrayLight
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(88.dp))
    }
}

@Composable
internal fun MatchProgressCard(
    isSingleMatch: Boolean,
    progress: Float,
    completedGames: Int,
    totalGames: Int
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = SquadOrange,
        shape = RoundedCornerShape(12.dp)
    ) {
        if (isSingleMatch) {
            // Barra de progresso — jogo singular
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.manageEvent_match_progress),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White.copy(alpha = 0.85f),
                        letterSpacing = 0.5.sp,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = "${(progress * 100).toInt()}%",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(999.dp)),
                    color = Color.White,
                    trackColor = Color.White.copy(alpha = 0.3f)
                )
            }
        } else {
            // Contador de jogos — torneio
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.manageEvent_match_progress),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White.copy(alpha = 0.85f),
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = stringResource(R.string.manageEvent_games_completed, completedGames, totalGames),
                        fontSize = 13.sp,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
                Text(
                    text = "$completedGames",
                    fontSize = 36.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
                Text(
                    text = " / $totalGames",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White.copy(alpha = 0.7f),
                    modifier = Modifier.align(Alignment.Bottom).padding(bottom = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun MetricsGrid(uiState: ManageEventUiState) {
    val freeLabel = stringResource(R.string.events_free)
    val metrics = buildList {
        if (uiState.allowTeams) add(Triple(Icons.Outlined.Groups, stringResource(R.string.manageEvent_teams_label), "${uiState.registeredTeams}/${uiState.maxTeams}"))
        if (uiState.allowFreeAgents) add(Triple(Icons.Outlined.PersonAdd, stringResource(R.string.manageEvent_free_agents_label), "${uiState.freeAgentsCount}"))
        add(Triple(Icons.Outlined.Person, stringResource(R.string.manageEvent_players_label), "${uiState.activePlayers}"))
        add(Triple(Icons.Outlined.Payments, stringResource(R.string.manageEvent_entry_fee_label), uiState.entryFee.ifBlank { freeLabel }))
        if (uiState.waitlistCount > 0) add(Triple(Icons.Outlined.HourglassBottom, stringResource(R.string.manageEvent_waitlist_label), "${uiState.waitlistCount}"))
    }
    val warningIndices = if (uiState.waitlistCount > 0) setOf(metrics.lastIndex) else emptySet()
    MetricRow(metrics, warningIndices)
}

@Composable
private fun StatusActionCard(
    status: EventStatus,
    onActionClick: () -> Unit
) {
    data class ActionConfig(
        val icon: ImageVector,
        val message: String,
        val buttonLabel: String,
        val color: Color
    )

    val config = when (status) {
        EventStatus.DRAFT -> ActionConfig(
            icon = Icons.Outlined.Upload,
            message = stringResource(R.string.manageEvent_action_draft_msg),
            buttonLabel = stringResource(R.string.manageEvent_action_open_registrations),
            color = SquadOrange
        )
        EventStatus.REGISTRATION_OPEN -> ActionConfig(
            icon = Icons.Outlined.LockOpen,
            message = stringResource(R.string.manageEvent_action_reg_open_msg),
            buttonLabel = stringResource(R.string.manageEvent_action_close_registrations),
            color = Color(0xFF1565C0)
        )
        EventStatus.REGISTRATION_CLOSED -> ActionConfig(
            icon = Icons.Outlined.PlayArrow,
            message = stringResource(R.string.manageEvent_action_reg_closed_msg),
            buttonLabel = stringResource(R.string.manageEvent_action_start_warmup),
            color = Color(0xFF2E7D32)
        )
        EventStatus.ONGOING, EventStatus.FINISHED, EventStatus.CANCELLED -> null
    } ?: return

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = config.color.copy(alpha = 0.07f),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, config.color.copy(alpha = 0.2f))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(config.icon, null, tint = config.color, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = config.message,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Button(
                onClick = onActionClick,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = config.color,
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = config.buttonLabel,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun ManagementToolCard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    isDestructive: Boolean = false
) {
    val contentColor = if (isDestructive) SquadError else MaterialTheme.colorScheme.onSurface
    val bgColor = if (isDestructive) Color(0xFFFFF5F5) else Color.White
    Surface(
        modifier = modifier.clickable(onClick = onClick),
        color = bgColor,
        shape = RoundedCornerShape(10.dp),
        shadowElevation = 2.dp,
        border = if (isDestructive) androidx.compose.foundation.BorderStroke(1.dp, SquadError.copy(alpha = 0.3f)) else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(icon, null, tint = contentColor, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = label, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = contentColor)
        }
    }
}

@Composable
private fun RecentRegistrationRow(item: RecentRegistrationItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(SquadOrangeLight, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = item.name.split(" ").mapNotNull { it.firstOrNull()?.toString() }.take(2).joinToString(""),
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = SquadOrange
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = item.name, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
            Text(text = item.position, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Text(text = item.timeAgo, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}