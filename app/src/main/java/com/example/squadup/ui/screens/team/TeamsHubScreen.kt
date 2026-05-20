package com.example.squadup.ui.screens.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.KeyboardArrowDown
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.squadup.ui.components.AppHeader
import com.example.squadup.ui.components.AppNavBar
import com.example.squadup.ui.components.ExpandedTeamPanel
import com.example.squadup.ui.components.TeamHubCard
import com.example.squadup.ui.components.TeamRosterMemberUi
import com.example.squadup.ui.components.TeamsHubTab
import com.example.squadup.ui.components.TeamsHubTabSelector
import com.example.squadup.ui.theme.SquadBackground
import com.example.squadup.ui.theme.SquadIconSecondary
import com.example.squadup.ui.theme.SquadOrange
import com.example.squadup.ui.theme.SquadSurfaceVariant

@Composable
fun TeamsHubScreen(
    selectedRoute: String,
    selectedTab: TeamsHubTab,
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
    val rosterMembers = listOf(
        TeamRosterMemberUi("Benjamin\nSesko", "Captain", true),
        TeamRosterMemberUi("Bruno\nFernandes", "Member"),
        TeamRosterMemberUi("Casemiro", "Member"),
        TeamRosterMemberUi("Kobbie Mainoo", "Member"),
        TeamRosterMemberUi("Antony", "Member")
    )

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
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(18.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.Top
            ) {
                TeamsHubTabSelector(
                    selectedTab = selectedTab,
                    onTabSelected = onTabSelected,
                    modifier = Modifier.weight(1f)
                )

                Button(
                    onClick = onCreateTeamClick,
                    modifier = Modifier.height(58.dp),
                    shape = RoundedCornerShape(6.dp),
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
                            contentDescription = null
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

                    TeamHubCard(
                        name = "Midnight Hoops",
                        members = "8 members",
                        icon = Icons.Outlined.SportsBasketball,
                        onClick = { onTeamClick("midnight_hoops") }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    ExpandedTeamPanel(
                        teamName = "Midnight Hoops",
                        sport = "Basket\nBall",
                        location = "Downtown\nArena",
                        inviteCode = "CD-982X",
                        members = rosterMembers,
                        isManagingRoster = isManagingRoster,
                        onInviteMembersClick = onInviteMembersClick,
                        onTeamSettingsClick = onTeamSettingsClick,
                        onCopyInviteCodeClick = onCopyInviteCodeClick,
                        onPromoteClick = onPromoteMemberClick,
                        onRemoveClick = onRemoveMemberClick
                    )
                }

                TeamsHubTab.Discover -> {
                    Text(
                        text = "Discover Teams",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = SquadOrange
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    TeamHubCard(
                        name = "The Mavericks",
                        members = "12 members",
                        icon = Icons.Outlined.SportsSoccer,
                        onClick = { onTeamClick("the_mavericks") }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun TeamsHubScreenPreview() {
    var selectedTab by remember { mutableStateOf(TeamsHubTab.MyTeams) }
    var isManagingRoster by remember { mutableStateOf(false) }

    TeamsHubScreen(
        selectedRoute = "teams",
        selectedTab = selectedTab,
        isManagingRoster = isManagingRoster,
        onTabSelected = { selectedTab = it },
        onTeamClick = {},
        onCreateTeamClick = {},
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