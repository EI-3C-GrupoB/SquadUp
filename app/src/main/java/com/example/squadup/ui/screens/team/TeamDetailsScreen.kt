package com.example.squadup.ui.screens.team

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.squadup.ui.components.AppHeader
import com.example.squadup.ui.components.PrimaryButton
import com.example.squadup.ui.components.TeamDetailsTab
import com.example.squadup.ui.components.TeamMatchUi
import com.example.squadup.ui.components.TeamMatchesList
import com.example.squadup.ui.components.TeamMemberUi
import com.example.squadup.ui.components.TeamMembersList
import com.example.squadup.ui.components.TeamOverviewCard
import com.example.squadup.ui.components.TeamTabSelector
import com.example.squadup.ui.theme.SquadBackground
import com.example.squadup.ui.theme.SquadIconSecondary

@Composable
fun TeamDetailsScreen(
    selectedTab: TeamDetailsTab,
    onTabSelected: (TeamDetailsTab) -> Unit,
    onNotificationsClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onGoBackClick: () -> Unit
) {
    val members = listOf(
        TeamMemberUi("Alex Hunter", "Leader", true),
        TeamMemberUi("Bruno Fernandes", "Member"),
        TeamMemberUi("Casemiro", "Member"),
        TeamMemberUi("Harry Maguire", "Member"),
        TeamMemberUi("Antony", "Member"),
        TeamMemberUi("Matheus Cunha", "Member"),
        TeamMemberUi("Benjamin Sesko", "Member"),
        TeamMemberUi("Kobbie Mainoo", "Member")
    )

    val matches = listOf(
        TeamMatchUi("The Mavericks vs Red Eagles", "Tomorrow", "18:00"),
        TeamMatchUi("The Mavericks vs Blue Dragons", "05/06/2026", "15:00"),
        TeamMatchUi("Lisbon Lions vs The Mavericks", "09/06/2026", "16:30")
    )

    Scaffold(
        topBar = {
            AppHeader(
                showLogo = false,
                title = "Match Overview",
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
            Spacer(modifier = Modifier.height(34.dp))

            TeamOverviewCard(
                teamName = "The\nMavericks",
                details = "12 Members • Soccer",
                badge = "Leader"
            )

            Spacer(modifier = Modifier.height(8.dp))

            TeamTabSelector(
                selectedTab = selectedTab,
                onTabSelected = onTabSelected
            )

            when (selectedTab) {
                TeamDetailsTab.Members -> {
                    TeamMembersList(members = members)
                }

                TeamDetailsTab.Matches -> {
                    TeamMatchesList(matches = matches)
                }
            }

            Spacer(modifier = Modifier.height(38.dp))

            PrimaryButton(
                text = "Go back",
                onClick = onGoBackClick
            )

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun TeamDetailsMembersPreview() {
    var selectedTab by remember {
        mutableStateOf(TeamDetailsTab.Members)
    }

    TeamDetailsScreen(
        selectedTab = selectedTab,
        onTabSelected = { selectedTab = it },
        onNotificationsClick = {},
        onSettingsClick = {},
        onGoBackClick = {}
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun TeamDetailsMatchesPreview() {
    var selectedTab by remember {
        mutableStateOf(TeamDetailsTab.Matches)
    }

    TeamDetailsScreen(
        selectedTab = selectedTab,
        onTabSelected = { selectedTab = it },
        onNotificationsClick = {},
        onSettingsClick = {},
        onGoBackClick = {}
    )
}