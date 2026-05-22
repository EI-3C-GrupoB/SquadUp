package com.example.squadup.ui.screens.team

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.SportsBasketball
import androidx.compose.material.icons.outlined.SportsSoccer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.squadup.ui.components.AppHeader
import com.example.squadup.ui.components.AppNavBar
import com.example.squadup.ui.components.ExpandedTeamPanel
import com.example.squadup.ui.components.TeamHubCard
import com.example.squadup.ui.components.TeamRosterMemberUi
import com.example.squadup.ui.components.TeamSearchField
import com.example.squadup.ui.components.TeamsHubTab
import com.example.squadup.ui.components.TeamsHubTabSelector
import com.example.squadup.ui.theme.SquadBackground
import com.example.squadup.ui.theme.SquadGrayDark
import com.example.squadup.ui.theme.SquadOrange

data class TeamHubUi(
    val id: String,
    val name: String,
    val membersText: String,
    val activePlayersText: String,
    val sport: String,
    val location: String,
    val inviteCode: String,
    val icon: ImageVector,
    val members: List<TeamRosterMemberUi>
)

@Composable
fun TeamsHubScreen(
    selectedRoute: String,
    selectedTab: TeamsHubTab,
    selectedTeamId: String?,
    isManagingRoster: Boolean,
    onTabSelected: (TeamsHubTab) -> Unit,
    onTeamClick: (String) -> Unit,
    onCreateTeamClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onInviteMembersClick: () -> Unit,
    onTeamSettingsClick: () -> Unit,
    onCopyInviteCodeClick: () -> Unit,
    onPromoteMemberClick: (TeamRosterMemberUi) -> Unit,
    onRemoveMemberClick: (TeamRosterMemberUi) -> Unit,
    onNavItemClick: (String) -> Unit
) {
    val teams = listOf(
        TeamHubUi(
            id = "lv_ponds",
            name = "LV Ponds",
            membersText = "8 members",
            activePlayersText = "8 Players Active",
            sport = "Basket\nBall",
            location = "Downtown\nArena",
            inviteCode = "LV-982X",
            icon = Icons.Outlined.SportsBasketball,
            members = listOf(
                TeamRosterMemberUi("Lebron Tiago", "Captain", true),
                TeamRosterMemberUi("Bruno Fernandes", "Member")
            )
        ),
        TeamHubUi(
            id = "red_eagles",
            name = "Red Eagles",
            membersText = "12 members",
            activePlayersText = "12 Players Active",
            sport = "Soccer",
            location = "Central\nArena",
            inviteCode = "RE-120A",
            icon = Icons.Outlined.SportsSoccer,
            members = listOf(
                TeamRosterMemberUi("Alex Hunter", "Captain", true),
                TeamRosterMemberUi("Bruno Fernandes", "Member"),
                TeamRosterMemberUi("Casemiro", "Member")
            )
        ),
        TeamHubUi(
            id = "florida_fire",
            name = "Florida Fire",
            membersText = "8 members",
            activePlayersText = "8 Players Active",
            sport = "Basket\nBall",
            location = "Florida\nCourt",
            inviteCode = "FF-204B",
            icon = Icons.Outlined.SportsBasketball,
            members = listOf(
                TeamRosterMemberUi("Mason Silva", "Captain", true),
                TeamRosterMemberUi("Rafael Costa", "Member")
            )
        ),
        TeamHubUi(
            id = "blue_dragons",
            name = "Blue Dragons",
            membersText = "8 members",
            activePlayersText = "8 Players Active",
            sport = "Basket\nBall",
            location = "North\nArena",
            inviteCode = "BD-449C",
            icon = Icons.Outlined.SportsBasketball,
            members = listOf(
                TeamRosterMemberUi("Noah Ramos", "Captain", true),
                TeamRosterMemberUi("Diogo Santos", "Member")
            )
        ),
        TeamHubUi(
            id = "green_lions",
            name = "Green Lions",
            membersText = "12 members",
            activePlayersText = "12 Players Active",
            sport = "Soccer",
            location = "City\nField",
            inviteCode = "GL-761D",
            icon = Icons.Outlined.SportsSoccer,
            members = listOf(
                TeamRosterMemberUi("Miguel Alves", "Captain", true),
                TeamRosterMemberUi("Joao Silva", "Member")
            )
        ),
        TeamHubUi(
            id = "sf_cows",
            name = "SF Cows",
            membersText = "8 members",
            activePlayersText = "8 Players Active",
            sport = "Basket\nBall",
            location = "South\nCourt",
            inviteCode = "SF-322E",
            icon = Icons.Outlined.SportsBasketball,
            members = listOf(
                TeamRosterMemberUi("Andre Lima", "Captain", true),
                TeamRosterMemberUi("Tiago Martins", "Member")
            )
        )
    )

    val selectedTeam = teams.firstOrNull { team ->
        team.id == selectedTeamId
    }

    Scaffold(
        topBar = {
            AppHeader(
                showLogo = true,
                title = "Teams",
                showBackButton = false,
                actions = {
                    IconButton(onClick = onNotificationsClick) {
                        Icon(
                            imageVector = Icons.Outlined.Notifications,
                            contentDescription = "Notificações",
                            tint = SquadGrayDark
                        )
                    }

                    IconButton(onClick = onSettingsClick) {
                        Icon(
                            imageVector = Icons.Outlined.Settings,
                            contentDescription = "Definições",
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
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(18.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.Top
            ) {
                TeamsHubTabSelector(
                    selectedTab = selectedTab,
                    onTabSelected = onTabSelected,
                    modifier = Modifier.weight(1f)
                )

                Button(
                    onClick = onCreateTeamClick,
                    modifier = Modifier
                        .width(96.dp)
                        .height(64.dp),
                    shape = RoundedCornerShape(9.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SquadOrange,
                        contentColor = Color.White
                    )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Add,
                            contentDescription = null,
                            modifier = Modifier.padding(end = 4.dp)
                        )

                        Text(
                            text = "Create\nTeam",
                            fontSize = 12.sp,
                            lineHeight = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            when (selectedTab) {
                TeamsHubTab.MyTeams -> {
                    Text(
                        text = "Active Squads",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = SquadOrange
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    teams.forEach { team ->
                        val isSelected = selectedTeam?.id == team.id

                        TeamHubCard(
                            name = team.name,
                            members = team.membersText,
                            icon = team.icon,
                            selected = isSelected,
                            expanded = isSelected,
                            onClick = {
                                onTeamClick(team.id)
                            }
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        if (isSelected) {
                            ExpandedTeamPanel(
                                teamName = team.name,
                                sport = team.sport,
                                location = team.location,
                                inviteCode = team.inviteCode,
                                members = team.members,
                                isManagingRoster = isManagingRoster,
                                activePlayersText = team.activePlayersText,
                                onInviteMembersClick = onInviteMembersClick,
                                onTeamSettingsClick = onTeamSettingsClick,
                                onCopyInviteCodeClick = onCopyInviteCodeClick,
                                onPromoteClick = onPromoteMemberClick,
                                onRemoveClick = onRemoveMemberClick
                            )

                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }
                }

                TeamsHubTab.Discover -> {
                    TeamSearchField(text = "Hinted search text")

                    Spacer(modifier = Modifier.height(18.dp))

                    Text(
                        text = "Active Squads",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = SquadOrange
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    teams.forEach { team ->
                        val isSelected = selectedTeam?.id == team.id

                        TeamHubCard(
                            name = team.name,
                            members = team.membersText,
                            icon = team.icon,
                            selected = isSelected,
                            expanded = isSelected,
                            onClick = {
                                onTeamClick(team.id)
                            }
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        if (isSelected) {
                            ExpandedTeamPanel(
                                teamName = team.name,
                                sport = team.sport,
                                location = team.location,
                                inviteCode = team.inviteCode,
                                members = team.members,
                                isManagingRoster = false,
                                activePlayersText = team.activePlayersText,
                                showOwnerActions = false,
                                primaryActionText = "Ask to join",
                                onInviteMembersClick = onInviteMembersClick,
                                onTeamSettingsClick = onTeamSettingsClick,
                                onCopyInviteCodeClick = onCopyInviteCodeClick,
                                onPromoteClick = onPromoteMemberClick,
                                onRemoveClick = onRemoveMemberClick
                            )

                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun TeamsHubScreenPreview() {
    var selectedTab by remember { mutableStateOf(TeamsHubTab.Discover) }
    var selectedTeamId by remember { mutableStateOf<String?>("lv_ponds") }
    var isManagingRoster by remember { mutableStateOf(false) }

    TeamsHubScreen(
        selectedRoute = "teams",
        selectedTab = selectedTab,
        selectedTeamId = selectedTeamId,
        isManagingRoster = isManagingRoster,
        onTabSelected = { selectedTab = it },
        onTeamClick = { selectedTeamId = it },
        onCreateTeamClick = {
            selectedTeamId = "lv_ponds"
        },
        onNotificationsClick = {},
        onSettingsClick = {},
        onInviteMembersClick = {},
        onTeamSettingsClick = {
            isManagingRoster = !isManagingRoster
        },
        onCopyInviteCodeClick = {},
        onPromoteMemberClick = {},
        onRemoveMemberClick = {},
        onNavItemClick = {}
    )
}
