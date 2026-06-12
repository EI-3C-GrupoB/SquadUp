package com.example.squadup.features.teams

import androidx.compose.material3.MaterialTheme

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.Key
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.PersonAdd
import androidx.compose.material.icons.outlined.PersonRemove
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import coil.compose.AsyncImage
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.squadup.core.enums.SportType
import com.example.squadup.core.ui.components.AppHeader
import com.example.squadup.core.ui.components.AppNavBar
import com.example.squadup.core.ui.components.EmptyStateCard
import com.example.squadup.core.ui.components.responsiveHorizontalPadding
import com.example.squadup.core.ui.components.responsiveVerticalSpacing
import com.example.squadup.core.ui.theme.SquadGrayLight
import com.example.squadup.core.ui.theme.SquadOrange
import com.example.squadup.core.ui.theme.SquadOrangeLight
import com.example.squadup.core.ui.theme.SquadWhite
import com.example.squadup.core.utils.AppLanguage
import com.example.squadup.core.utils.toIcon
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextButton
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.squadup.core.ui.theme.SquadError
@Composable
fun TeamsScreen(
    uiState: TeamsUiState,
    isLoggedIn: Boolean,
    selectedRoute: String,
    onNavItemClick: (String) -> Unit,
    onNotificationsClick: () -> Unit,
    onCreateTeamClick: () -> Unit,
    onInviteMembersClick: (String) -> Unit,
    onTabSelected: (TeamsTab) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onTeamToggle: (String) -> Unit,
    onTeamSettingsToggle: (String) -> Unit,
    onLoginClick: () -> Unit,
    onAskToJoinClick: (String) -> Unit,
    onPromoteMemberClick: (teamId: String, memberId: String) -> Unit,
    onRemoveMemberClick: (teamId: String, memberId: String) -> Unit,
    onTeamDetailTabChange: (teamId: String, TeamDetailTab) -> Unit,
    onJoinByCodeDialogOpen: () -> Unit,
    onJoinByCodeDialogDismiss: () -> Unit,
    onJoinByCodeChange: (String) -> Unit,
    onJoinByCodeSubmit: () -> Unit,
    isAdmin: Boolean,
    isAdminView: Boolean,
    selectedLanguage: AppLanguage,
    isDarkMode: Boolean,
    onAdminViewChange: (Boolean) -> Unit,
    onLanguageChange: (AppLanguage) -> Unit,
    onDarkModeChange: (Boolean) -> Unit,
    notificationsCount: Int = 0
) {
    val horizontalPadding = responsiveHorizontalPadding(22.dp)
    val topSpacing = responsiveVerticalSpacing(20.dp)

    if (uiState.showJoinByCodeDialog) {
        JoinByCodeDialog(
            code = uiState.joinByCodeValue,
            errorMessage = uiState.joinByCodeError,
            isLoading = uiState.isJoiningByCode,
            onCodeChange = onJoinByCodeChange,
            onDismiss = onJoinByCodeDialogDismiss,
            onSubmit = onJoinByCodeSubmit
        )
    }

    Scaffold(
        topBar = {
            AppHeader(
                showLogo = true,
                title = "Teams",
                showNotificationsButton = isLoggedIn,
                notificationsCount = notificationsCount,
                onNotificationsClick = onNotificationsClick,
                showSettingsButton = true,
// ...
                showLoginButton = !isLoggedIn,
                onLoginClick = onLoginClick,
                isAdmin = isAdmin,
                isAdminView = isAdminView,
                selectedLanguage = selectedLanguage,
                isDarkMode = isDarkMode,
                onAdminViewChange = onAdminViewChange,
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
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = horizontalPadding)
        ) {
            Spacer(modifier = Modifier.height(topSpacing))

            TeamsActionSelector(
                selectedTab = uiState.selectedTab,
                onTabSelected = onTabSelected,
                onCreateTeamClick = onCreateTeamClick,
                isLoggedIn = isLoggedIn
            )

            if (uiState.selectedTab == TeamsTab.DISCOVER) {
                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TeamsSearchField(
                        value = uiState.searchQuery,
                        onValueChange = onSearchQueryChange,
                        modifier = Modifier.weight(1f)
                    )

                    JoinByCodeButton(
                        onClick = onJoinByCodeDialogOpen
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Active Squads",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = SquadOrange,
                modifier = Modifier.padding(start = 2.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (!isLoggedIn && uiState.selectedTab == TeamsTab.MY_TEAMS) {
                    EmptyStateCard(
                        title = "Entra para ver as tuas equipas",
                        message = "Inicia sessão para criares ou juntares-te às tuas próprias equipas e gerires o teu plantel.",
                        icon = Icons.Outlined.Groups,
                        actionText = "Login",
                        onActionClick = onLoginClick
                    )
                } else if (uiState.visibleTeams.isEmpty()) {
                    EmptyStateCard(
                        title = if (uiState.selectedTab == TeamsTab.MY_TEAMS) "No Teams Found" else "No Teams to Discover",
                        message = if (uiState.selectedTab == TeamsTab.MY_TEAMS)
                            "You haven't joined or created any teams yet."
                        else "There are no teams available for discovery right now.",
                        icon = Icons.Outlined.Groups
                    )
                } else {
                    uiState.visibleTeams.forEach { team ->
                        ExpandableTeamCard(
                            uiState = uiState,
                            team = team,
                            selectedTab = uiState.selectedTab,
                            expanded = uiState.expandedTeamId == team.id,
                            settingsActive = uiState.settingsTeamId == team.id,
                            currentDetailTab = uiState.teamDetailTabs[team.id] ?: TeamDetailTab.MEMBERS,
                            onToggle = { onTeamToggle(team.id) },
                            onInviteMembersClick = { onInviteMembersClick(team.id) },
                            onTeamSettingsToggle = { onTeamSettingsToggle(team.id) },
                            onAskToJoinClick = { onAskToJoinClick(team.id) },
                            onPromoteMemberClick = onPromoteMemberClick,
                            onRemoveMemberClick = onRemoveMemberClick,
                            onDetailTabChange = { tab -> onTeamDetailTabChange(team.id, tab) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}

@Composable
private fun TeamsActionSelector(
    selectedTab: TeamsTab,
    onTabSelected: (TeamsTab) -> Unit,
    onCreateTeamClick: () -> Unit,
    isLoggedIn: Boolean
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(15.dp),
                shadowElevation = 1.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TeamsSliderTab(
                        text = "My Teams",
                        selected = selectedTab == TeamsTab.MY_TEAMS,
                        onClick = { onTabSelected(TeamsTab.MY_TEAMS) },
                        modifier = Modifier.weight(1f)
                    )

                    TeamsSliderTab(
                        text = "Discover",
                        selected = selectedTab == TeamsTab.DISCOVER,
                        onClick = { onTabSelected(TeamsTab.DISCOVER) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            if (isLoggedIn) {
                CreateTeamButton(
                    onClick = onCreateTeamClick,
                    modifier = Modifier.width(128.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            thickness = 1.dp,
            color = Color(0xFFCFCFCF)
        )
    }
}

@Composable
private fun TeamsSliderTab(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxHeight()
            .background(
                color = if (selected) SquadOrange else Color.Transparent,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = if (selected) SquadWhite else MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
            maxLines = 1
        )
    }
}

@Composable
private fun CreateTeamButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .height(48.dp)
            .clickable(onClick = onClick),
        color = SquadOrange,
        shape = RoundedCornerShape(12.dp),
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = responsiveHorizontalPadding(12.dp)),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.Add,
                contentDescription = null,
                tint = SquadWhite,
                modifier = Modifier.size(16.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "Create Team",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = SquadWhite,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun TeamsSearchField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        textStyle = TextStyle(
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        ),
        modifier = modifier
            .fillMaxWidth()
            .height(38.dp)
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(10.dp)
            )
            .padding(horizontal = responsiveHorizontalPadding(14.dp)),
        decorationBox = { innerTextField ->
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.Search,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(18.dp)
                )

                Spacer(modifier = Modifier.width(10.dp))

                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.CenterStart
                ) {
                    if (value.isBlank()) {
                        Text(
                            text = "Search sports, teams, or venues...",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1
                        )
                    }

                    innerTextField()
                }
            }
        }
    )
}

@Composable
private fun ExpandableTeamCard(
    uiState: TeamsUiState,
    team: TeamListItem,
    selectedTab: TeamsTab,
    expanded: Boolean,
    settingsActive: Boolean,
    currentDetailTab: TeamDetailTab,
    onToggle: () -> Unit,
    onInviteMembersClick: () -> Unit,
    onTeamSettingsToggle: () -> Unit,
    onAskToJoinClick: () -> Unit,
    onPromoteMemberClick: (teamId: String, memberId: String) -> Unit,
    onRemoveMemberClick: (teamId: String, memberId: String) -> Unit,
    onDetailTabChange: (TeamDetailTab) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        TeamSummaryCard(
            team = team,
            expanded = expanded,
            onToggle = onToggle
        )

        AnimatedVisibility(
            visible = expanded,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            TeamExpandedContent(
                uiState = uiState,
                team = team,
                selectedTab = selectedTab,
                settingsActive = settingsActive,
                currentDetailTab = currentDetailTab,
                onInviteMembersClick = onInviteMembersClick,
                onTeamSettingsToggle = onTeamSettingsToggle,
                onAskToJoinClick = onAskToJoinClick,
                onPromoteMemberClick = onPromoteMemberClick,
                onRemoveMemberClick = onRemoveMemberClick,
                onDetailTabChange = onDetailTabChange
            )
        }
    }
}

@Composable
private fun TeamSummaryCard(
    team: TeamListItem,
    expanded: Boolean,
    onToggle: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp)
            .clickable(onClick = onToggle),
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(
            width = if (expanded) 2.dp else 1.dp,
            color = if (expanded) SquadOrange else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.55f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = responsiveHorizontalPadding(12.dp)),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TeamSportIcon(
                sportType = team.sportType,
                logoUrl = team.logoUrl
            )

            Spacer(modifier = Modifier.width(14.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = team.name,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(2.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Groups,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(13.dp)
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    Text(
                        text = "${team.membersCount} members",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Icon(
                imageVector = if (expanded) {
                    Icons.Default.KeyboardArrowUp
                } else {
                    Icons.Default.KeyboardArrowDown
                },
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

@Composable
private fun TeamExpandedContent(
    uiState: TeamsUiState,
    team: TeamListItem,
    selectedTab: TeamsTab,
    settingsActive: Boolean,
    currentDetailTab: TeamDetailTab,
    onInviteMembersClick: () -> Unit,
    onTeamSettingsToggle: () -> Unit,
    onAskToJoinClick: () -> Unit,
    onPromoteMemberClick: (teamId: String, memberId: String) -> Unit,
    onRemoveMemberClick: (teamId: String, memberId: String) -> Unit,
    onDetailTabChange: (TeamDetailTab) -> Unit
) {
    var memberPendingPromotion by remember { mutableStateOf<TeamRosterMember?>(null) }
    var memberPendingRemoval by remember { mutableStateOf<TeamRosterMember?>(null) }

    val promotionTarget = memberPendingPromotion
    if (promotionTarget != null) {
        AlertDialog(
            onDismissRequest = {
                memberPendingPromotion = null
            },
            title = {
                Text(
                    text = "Promover jogador?",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            text = {
                Text(
                    text = "Tens a certeza que queres promover ${promotionTarget.name} a capitão? O capitão actual deixará de ser capitão e passará a membro.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onPromoteMemberClick(team.id, promotionTarget.id)
                        memberPendingPromotion = null
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SquadOrange,
                        contentColor = SquadWhite
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = "Promover",
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        memberPendingPromotion = null
                    }
                ) {
                    Text(
                        text = "Cancelar",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            },
            containerColor = MaterialTheme.colorScheme.surface
        )
    }

    val removalTarget = memberPendingRemoval
    if (removalTarget != null) {
        AlertDialog(
            onDismissRequest = {
                memberPendingRemoval = null
            },
            title = {
                Text(
                    text = "Remover jogador?",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            text = {
                Text(
                    text = "Tens a certeza que queres remover ${removalTarget.name} da equipa ${team.name}? Este jogador receberá uma notificação.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onRemoveMemberClick(team.id, removalTarget.id)
                        memberPendingRemoval = null
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SquadError,
                        contentColor = SquadWhite
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = "Remover",
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        memberPendingRemoval = null
                    }
                ) {
                    Text(
                        text = "Cancelar",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            },
            containerColor = MaterialTheme.colorScheme.surface
        )
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.45f))
    ) {
        Column {
            TeamExpandedHero(
                team = team,
                selectedTab = selectedTab,
                settingsActive = settingsActive,
                onInviteMembersClick = onInviteMembersClick,
                onTeamSettingsToggle = onTeamSettingsToggle
            )

            if (selectedTab == TeamsTab.MY_TEAMS) {
                TeamDetailTabSlider(
                    currentTab = currentDetailTab,
                    onTabChange = onDetailTabChange
                )
            }

            Column(
                modifier = Modifier.padding(
                    start = 18.dp,
                    end = 18.dp,
                    bottom = 18.dp
                )
            ) {
                if (selectedTab == TeamsTab.DISCOVER) {
                    val isPending = uiState.pendingJoinRequests.contains(team.id)

                    Button(
                        onClick = onAskToJoinClick,
                        enabled = !isPending,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isPending) Color(0xFF9E9E9E) else SquadOrange,
                            contentColor = SquadWhite,
                            disabledContainerColor = Color(0xFF9E9E9E),
                            disabledContentColor = SquadWhite.copy(alpha = 0.8f)
                        )
                    ) {
                        Text(
                            text = if (isPending) "Request Sent" else "Ask to join",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))
                }

                if (selectedTab == TeamsTab.MY_TEAMS && currentDetailTab == TeamDetailTab.MATCHES) {
                    UpcomingMatchesList(matches = team.upcomingMatches)
                } else {
                    RosterHeader(playersCount = team.roster.size)

                    Spacer(modifier = Modifier.height(14.dp))

                    if (team.roster.isEmpty()) {
                        EmptyRosterMessage()
                    } else {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            team.roster.forEach { member ->
                                RosterMemberCard(
                                    member = member,
                                    selectedTab = selectedTab,
                                    settingsActive = settingsActive,
                                    onPromoteClick = {
                                        memberPendingPromotion = member
                                    },
                                    onRemoveClick = {
                                        memberPendingRemoval = member
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TeamExpandedHero(
    team: TeamListItem,
    selectedTab: TeamsTab,
    settingsActive: Boolean,
    onInviteMembersClick: () -> Unit,
    onTeamSettingsToggle: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(145.dp)
                .background(
                    brush = Brush.linearGradient(
                        listOf(
                            Color(0xFF2A1003),
                            Color(0xFF6B2A05),
                            Color(0xFF180802)
                        )
                    )
                )
        ) {
            Icon(
                imageVector = team.sportType.toIcon(),
                contentDescription = null,
                tint = SquadOrange.copy(alpha = 0.10f),
                modifier = Modifier
                    .size(64.dp)
                    .align(Alignment.TopStart)
                    .padding(start = 20.dp, top = 18.dp)
            )

            Icon(
                imageVector = team.sportType.toIcon(),
                contentDescription = null,
                tint = SquadOrange.copy(alpha = 0.13f),
                modifier = Modifier
                    .size(118.dp)
                    .align(Alignment.CenterEnd)
                    .padding(end = 16.dp)
            )
        }

        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = responsiveHorizontalPadding(20.dp))
                    .padding(top = 18.dp, bottom = 22.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(82.dp)
                        .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(22.dp))
                        .padding(7.dp)
                        .background(SquadOrange, RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    if (team.logoUrl != null) {
                        AsyncImage(
                            model = team.logoUrl,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(16.dp)),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(
                            imageVector = team.sportType.toIcon(),
                            contentDescription = null,
                            tint = SquadWhite,
                            modifier = Modifier.size(38.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = team.name,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = when (team.sportType) {
                            SportType.SOCCER -> "Soccer"
                            SportType.BASKETBALL -> "Basketball"
                            SportType.PADDLE -> "Paddle"
                            SportType.VOLLEYBALL -> "Volleyball"
                            SportType.FUTSAL -> "Futsal"
                        },
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = SquadWhite,
                        modifier = Modifier
                            .background(SquadOrange, RoundedCornerShape(999.dp))
                            .padding(horizontal = 18.dp, vertical = 7.dp)
                    )
                }

                if (selectedTab == TeamsTab.MY_TEAMS) {
                    Spacer(modifier = Modifier.height(20.dp))

                    HorizontalDivider(
                        modifier = Modifier.fillMaxWidth(),
                        thickness = 1.dp,
                        color = SquadGrayLight
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        TeamSimpleActionButton(
                            text = "Invite Members",
                            icon = Icons.Outlined.Add,
                            filled = true,
                            active = false,
                            onClick = onInviteMembersClick,
                            modifier = Modifier.weight(1f)
                        )

                        if (team.isCaptain) {
                            TeamSimpleActionButton(
                                text = if (settingsActive) "Settings Active" else "Team Settings",
                                icon = Icons.Outlined.Settings,
                                filled = true,
                                active = settingsActive,
                                onClick = onTeamSettingsToggle,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }
    }
}
@Composable
private fun TeamDetailTabSlider(
    currentTab: TeamDetailTab,
    onTabChange: (TeamDetailTab) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp, vertical = 10.dp)
            .background(MaterialTheme.colorScheme.background, RoundedCornerShape(10.dp))
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        TeamDetailTab.entries.forEach { tab ->
            val selected = currentTab == tab
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(36.dp)
                    .background(
                        color = if (selected) SquadOrange else Color.Transparent,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .clickable { onTabChange(tab) },
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = if (tab == TeamDetailTab.MEMBERS) Icons.Outlined.Groups else Icons.Outlined.CalendarMonth,
                        contentDescription = null,
                        tint = if (selected) SquadWhite else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = if (tab == TeamDetailTab.MEMBERS) "Membros" else "Próximos Jogos",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (selected) SquadWhite else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun UpcomingMatchesList(matches: List<TeamUpcomingMatch>) {
    if (matches.isEmpty()) {
        EmptyStateCard(
            title = "Sem jogos agendados",
            message = "Esta equipa ainda não tem jogos próximos para mostrar.",
            icon = Icons.Outlined.CalendarMonth
        )
        return
    }

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        matches.forEach { match ->
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.45f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .background(SquadOrangeLight, RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.CalendarMonth,
                            contentDescription = null,
                            tint = SquadOrange,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = match.opponentName,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        if (match.eventName.isNotBlank()) {
                            Text(
                                text = match.eventName,
                                fontSize = 11.sp,
                                color = SquadOrange,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        if (match.date.isNotBlank()) {
                            Text(
                                text = match.date,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        if (match.location.isNotBlank()) {
                            Text(
                                text = match.location,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TeamSimpleActionButton(
    text: String,
    icon: ImageVector,
    filled: Boolean,
    active: Boolean = false,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = when {
        active -> Color(0xFF26323F)
        filled -> SquadOrange
        else -> SquadWhite
    }

    val contentColor = when {
        active -> SquadWhite
        filled -> SquadWhite
        else -> SquadOrange
    }

    Surface(
        modifier = modifier
            .height(52.dp)
            .clickable(onClick = onClick),
        color = backgroundColor,
        shape = RoundedCornerShape(12.dp),
        border = if (filled || active) null else BorderStroke(1.5.dp, SquadOrange),
        shadowElevation = if (filled || active) 4.dp else 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = responsiveHorizontalPadding(10.dp)),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(18.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = text,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = contentColor,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun RosterHeader(
    playersCount: Int
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Roster Management",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )

        Text(
            text = "$playersCount Players Active",
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun RosterMemberCard(
    member: TeamRosterMember,
    selectedTab: TeamsTab,
    settingsActive: Boolean,
    onPromoteClick: () -> Unit,
    onRemoveClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 50.dp),
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(6.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.55f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(SquadOrangeLight, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Person,
                    contentDescription = null,
                    tint = SquadOrange,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = member.name,
                fontSize = 13.sp,
                lineHeight = 17.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )

            if (!settingsActive || selectedTab == TeamsTab.DISCOVER) {
                when (member.role) {
                    TeamRosterRole.CAPTAIN -> {
                        RoleBadge(
                            text = "CAPTAIN",
                            background = SquadOrange,
                            textColor = SquadWhite
                        )
                    }

                    TeamRosterRole.MEMBER -> {
                        RoleBadge(
                            text = "MEMBER",
                            background = Color(0xFF9E9E9E),
                            textColor = SquadWhite
                        )
                    }
                }
            } else {
                when (member.role) {
                    TeamRosterRole.CAPTAIN -> {
                        RoleBadge(
                            text = "CAPTAIN",
                            background = SquadOrange,
                            textColor = SquadWhite
                        )

                        Spacer(modifier = Modifier.width(10.dp))

                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    TeamRosterRole.MEMBER -> {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                modifier = Modifier.clickable(onClick = onPromoteClick),
                                color = Color(0xFF26323F),
                                shape = RoundedCornerShape(999.dp)
                            ) {
                                Text(
                                    text = "Promote",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SquadWhite,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }

                            Icon(
                                imageVector = Icons.Outlined.PersonRemove,
                                contentDescription = "Remove",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier
                                    .size(20.dp)
                                    .clickable(onClick = onRemoveClick)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RoleBadge(
    text: String,
    background: Color,
    textColor: Color
) {
    Text(
        text = text,
        fontSize = 9.sp,
        fontWeight = FontWeight.Bold,
        color = textColor,
        modifier = Modifier
            .background(background, RoundedCornerShape(999.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    )
}

@Composable
private fun EmptyRosterMessage() {
    EmptyStateCard(
        title = "No Players",
        message = "There are currently no players on this team. Try inviting your friends!",
        icon = Icons.Outlined.Person
    )
}

@Composable
private fun TeamSportIcon(
    sportType: SportType,
    logoUrl: String? = null
) {
    Box(
        modifier = Modifier
            .size(44.dp)
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(8.dp)),
        contentAlignment = Alignment.Center
    ) {
        if (logoUrl != null) {
            AsyncImage(
                model = logoUrl,
                contentDescription = null,
                modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
        } else {
            Icon(
                imageVector = sportType.toIcon(),
                contentDescription = null,
                tint = SquadOrange,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
private fun JoinByCodeButton(
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .size(38.dp)
            .clickable(onClick = onClick),
        color = SquadOrange,
        shape = RoundedCornerShape(10.dp),
        shadowElevation = 2.dp
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.Key,
                contentDescription = "Join by code",
                tint = SquadWhite,
                modifier = Modifier.size(19.dp)
            )
        }
    }
}

@Composable
private fun JoinByCodeDialog(
    code: String,
    errorMessage: String?,
    isLoading: Boolean,
    onCodeChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onSubmit: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        title = {
            Text(
                text = "Join by invite code",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        text = {
            Column {
                Text(
                    text = "Enter the team invite code to request access.",
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = code,
                    onValueChange = onCodeChange,
                    enabled = !isLoading,
                    singleLine = true,
                    placeholder = {
                        Text(
                            text = "TEA-3502",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = TextStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = errorMessage,
                        fontSize = 13.sp,
                        lineHeight = 18.sp,
                        color = Color(0xFFD32F2F)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onSubmit,
                enabled = !isLoading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = SquadOrange,
                    contentColor = SquadWhite,
                    disabledContainerColor = SquadOrange.copy(alpha = 0.45f),
                    disabledContentColor = SquadWhite.copy(alpha = 0.75f)
                ),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(
                    text = if (isLoading) "Sending..." else "Request",
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = !isLoading
            ) {
                Text(
                    text = "Cancel",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    )
}
