package com.example.squadup.ui.screens.admin

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.KeyboardArrowRight
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.squadup.navigation.AppRoutes
import com.example.squadup.ui.components.AppNavBar
import com.example.squadup.ui.theme.SquadBackground
import com.example.squadup.ui.theme.SquadGrayDark
import com.example.squadup.ui.theme.SquadOrange
import com.example.squadup.ui.theme.SquadSurface
import com.example.squadup.ui.theme.SquadTextPrimary
import com.example.squadup.ui.theme.SquadTextSecondary
import com.example.squadup.ui.theme.SquadUpTheme

data class ManageEventTeamUi(
    val initials: String,
    val name: String,
    val players: Int,
    val federated: Boolean
)

data class ManageEventPlayerUi(
    val initials: String,
    val name: String,
    val id: String,
    val validated: Boolean
)

private val BorderSoft = Color(0xFFE0E4EA)
private val SelectedTeamBackground = Color(0xFFFFD8C7)
private val CurrentGameOrange = Color(0xFFF56A00)
private val ValidatedGreen = Color(0xFF17B85A)
private val PendingOrange = Color(0xFFFF6B00)

@Composable
fun ManageEventTeamsRoute(
    onNotificationsClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    onTeamsTabClick: () -> Unit = {},
    onGamesTabClick: () -> Unit = {},
    onStatsTabClick: () -> Unit = {},
    onAddTeamClick: () -> Unit = {},
    onAddPlayerClick: () -> Unit = {},
    onDeleteTeamClick: (ManageEventTeamUi) -> Unit = {},
    onDeletePlayerClick: (ManageEventPlayerUi) -> Unit = {},
    onPlayerViewClick: (ManageEventPlayerUi) -> Unit = {},
    onCurrentGameAddClick: () -> Unit = {},
    onNavItemClick: (String) -> Unit = {}
) {
    var searchQuery by rememberSaveable {
        mutableStateOf("")
    }

    var expandedTeam by remember {
        mutableStateOf<ManageEventTeamUi?>(null)
    }

    var editingTeam by remember {
        mutableStateOf<ManageEventTeamUi?>(null)
    }

    val teams = remember {
        defaultManageEventTeams()
    }

    val players = remember {
        defaultTeamPlayers()
    }

    ManageEventTeamsScreen(
        selectedRoute = AppRoutes.EVENTS,
        searchQuery = searchQuery,
        teams = teams,
        expandedTeam = expandedTeam,
        editingTeam = editingTeam,
        players = players,
        onSearchQueryChange = { newValue ->
            searchQuery = newValue
        },
        onNotificationsClick = onNotificationsClick,
        onSettingsClick = onSettingsClick,
        onTeamsTabClick = onTeamsTabClick,
        onGamesTabClick = onGamesTabClick,
        onStatsTabClick = onStatsTabClick,
        onTeamClick = { clickedTeam ->
            val isAlreadyOpen = expandedTeam?.name == clickedTeam.name

            expandedTeam = if (isAlreadyOpen) {
                null
            } else {
                clickedTeam
            }

            editingTeam = null
        },
        onAddTeamClick = onAddTeamClick,
        onAddPlayerClick = onAddPlayerClick,
        onEditTeamClick = { team ->
            expandedTeam = team
            editingTeam = team
        },
        onDeleteTeamClick = { team ->
            onDeleteTeamClick(team)

            if (expandedTeam?.name == team.name) {
                expandedTeam = null
            }

            if (editingTeam?.name == team.name) {
                editingTeam = null
            }
        },
        onDeletePlayerClick = onDeletePlayerClick,
        onPlayerViewClick = onPlayerViewClick,
        onCurrentGameAddClick = onCurrentGameAddClick,
        onNavItemClick = onNavItemClick
    )
}

@Composable
fun ManageEventTeamsScreen(
    selectedRoute: String,
    searchQuery: String,
    teams: List<ManageEventTeamUi>,
    expandedTeam: ManageEventTeamUi?,
    editingTeam: ManageEventTeamUi?,
    players: List<ManageEventPlayerUi>,
    onSearchQueryChange: (String) -> Unit,
    onNotificationsClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onTeamsTabClick: () -> Unit,
    onGamesTabClick: () -> Unit,
    onStatsTabClick: () -> Unit,
    onTeamClick: (ManageEventTeamUi) -> Unit,
    onAddTeamClick: () -> Unit,
    onAddPlayerClick: () -> Unit,
    onEditTeamClick: (ManageEventTeamUi) -> Unit,
    onDeleteTeamClick: (ManageEventTeamUi) -> Unit,
    onDeletePlayerClick: (ManageEventPlayerUi) -> Unit,
    onPlayerViewClick: (ManageEventPlayerUi) -> Unit,
    onCurrentGameAddClick: () -> Unit,
    onNavItemClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            ManageEventTopBar(
                onNotificationsClick = onNotificationsClick,
                onSettingsClick = onSettingsClick
            )
        },
        bottomBar = {
            AppNavBar(
                selectedRoute = selectedRoute,
                onItemClick = onNavItemClick
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (expandedTeam == null) {
                        onAddTeamClick()
                    } else {
                        onCurrentGameAddClick()
                    }
                },
                containerColor = SquadOrange,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier.size(if (expandedTeam == null) 58.dp else 52.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Add,
                    contentDescription = "Adicionar",
                    modifier = Modifier.size(if (expandedTeam == null) 30.dp else 28.dp)
                )
            }
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
            Text(
                text = "🏆 SUMMER LEAGUE: ELITE FINALS",
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF9A3F00)
            )

            Spacer(modifier = Modifier.height(8.dp))

            EventAdminTabs(
                selectedTab = "Teams",
                onTeamsTabClick = onTeamsTabClick,
                onGamesTabClick = onGamesTabClick,
                onStatsTabClick = onStatsTabClick
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Teams and Players",
                fontSize = 25.sp,
                fontWeight = FontWeight.Medium,
                color = SquadTextPrimary
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Manage all subscriptions, teams and documents of\nevery participant.",
                fontSize = 12.sp,
                lineHeight = 16.sp,
                color = SquadTextSecondary
            )

            Spacer(modifier = Modifier.height(18.dp))

            EventStatsCards()

            Spacer(modifier = Modifier.height(14.dp))

            TeamSearchField(
                value = searchQuery,
                onValueChange = onSearchQueryChange
            )

            Spacer(modifier = Modifier.height(14.dp))

            TeamsAccordionList(
                teams = teams,
                expandedTeam = expandedTeam,
                editingTeam = editingTeam,
                players = players,
                onTeamClick = onTeamClick,
                onAddPlayerClick = onAddPlayerClick,
                onEditTeamClick = onEditTeamClick,
                onDeleteTeamClick = onDeleteTeamClick,
                onDeletePlayerClick = onDeletePlayerClick,
                onPlayerViewClick = onPlayerViewClick
            )
        }
    }
}

@Composable
private fun ManageEventTopBar(
    onNotificationsClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = SquadSurface,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .padding(start = 18.dp, end = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Manage Events",
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                color = SquadOrange,
                modifier = Modifier.weight(1f)
            )

            IconButton(
                onClick = onNotificationsClick,
                modifier = Modifier.size(34.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Notifications,
                    contentDescription = "Notificações",
                    tint = SquadGrayDark,
                    modifier = Modifier.size(19.dp)
                )
            }

            IconButton(
                onClick = onSettingsClick,
                modifier = Modifier.size(34.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Settings,
                    contentDescription = "Definições",
                    tint = SquadGrayDark,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun EventAdminTabs(
    selectedTab: String,
    onTeamsTabClick: () -> Unit,
    onGamesTabClick: () -> Unit,
    onStatsTabClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
    ) {
        Surface(
            color = Color(0xFFE4E4E4),
            shape = RoundedCornerShape(8.dp)
        ) {
            Row(
                modifier = Modifier.padding(3.dp)
            ) {
                EventTabButton(
                    text = "Teams",
                    selected = selectedTab == "Teams",
                    onClick = onTeamsTabClick
                )

                EventTabButton(
                    text = "Games",
                    selected = selectedTab == "Games",
                    onClick = onGamesTabClick
                )

                EventTabButton(
                    text = "Stats",
                    selected = selectedTab == "Stats",
                    onClick = onStatsTabClick
                )
            }
        }
    }
}

@Composable
private fun EventTabButton(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .height(31.dp)
            .width(64.dp)
            .clickable(onClick = onClick),
        color = if (selected) SquadOrange else Color.Transparent,
        shape = RoundedCornerShape(6.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = if (selected) Color.White else SquadTextPrimary
            )
        }
    }
}

@Composable
private fun EventStatsCards() {
    Column {
        StatCard(
            icon = "👥",
            number = "24",
            label = "Registered teams"
        )

        Spacer(modifier = Modifier.height(12.dp))

        StatCard(
            icon = "♙",
            number = "288",
            label = "Active players"
        )
    }
}

@Composable
private fun StatCard(
    icon: String,
    number: String,
    label: String
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(78.dp),
        color = SquadSurface,
        shape = RoundedCornerShape(9.dp),
        border = BorderStroke(1.dp, BorderSoft)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = icon,
                fontSize = 16.sp,
                color = SquadOrange
            )

            Text(
                text = number,
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold,
                color = SquadTextPrimary
            )

            Text(
                text = label,
                fontSize = 13.sp,
                color = SquadTextSecondary
            )
        }
    }
}

@Composable
private fun TeamSearchField(
    value: String,
    onValueChange: (String) -> Unit
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        textStyle = TextStyle(
            fontSize = 13.sp,
            color = SquadTextPrimary
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(42.dp)
            .background(Color.White, RoundedCornerShape(9.dp))
            .padding(horizontal = 14.dp),
        decorationBox = { innerTextField ->
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.Search,
                    contentDescription = null,
                    tint = SquadTextSecondary,
                    modifier = Modifier.size(19.dp)
                )

                Spacer(modifier = Modifier.width(10.dp))

                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.CenterStart
                ) {
                    if (value.isBlank()) {
                        Text(
                            text = "Search Team.....",
                            fontSize = 13.sp,
                            color = SquadTextSecondary
                        )
                    }

                    innerTextField()
                }
            }
        }
    )
}

@Composable
private fun TeamsAccordionList(
    teams: List<ManageEventTeamUi>,
    expandedTeam: ManageEventTeamUi?,
    editingTeam: ManageEventTeamUi?,
    players: List<ManageEventPlayerUi>,
    onTeamClick: (ManageEventTeamUi) -> Unit,
    onAddPlayerClick: () -> Unit,
    onEditTeamClick: (ManageEventTeamUi) -> Unit,
    onDeleteTeamClick: (ManageEventTeamUi) -> Unit,
    onDeletePlayerClick: (ManageEventPlayerUi) -> Unit,
    onPlayerViewClick: (ManageEventPlayerUi) -> Unit
) {
    Column {
        teams.forEachIndexed { index, team ->
            val isExpanded = expandedTeam?.name == team.name
            val isEditing = editingTeam?.name == team.name

            TeamListItem(
                team = team,
                expanded = isExpanded,
                onClick = { onTeamClick(team) }
            )

            if (isExpanded) {
                Spacer(modifier = Modifier.height(12.dp))

                if (isEditing) {
                    EditTeamCard(
                        team = team,
                        players = players,
                        onAddPlayerClick = onAddPlayerClick,
                        onDeleteTeamClick = { onDeleteTeamClick(team) },
                        onDeletePlayerClick = onDeletePlayerClick,
                        onPlayerViewClick = onPlayerViewClick
                    )
                } else {
                    TeamDetailsCard(
                        team = team,
                        players = players,
                        onAddPlayerClick = onAddPlayerClick,
                        onEditTeamClick = { onEditTeamClick(team) },
                        onPlayerViewClick = onPlayerViewClick
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                CurrentGameCard()
            }

            if (index < teams.lastIndex) {
                Spacer(modifier = Modifier.height(10.dp))
            }
        }
    }
}

@Composable
private fun TeamListItem(
    team: ManageEventTeamUi,
    expanded: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(74.dp)
            .clickable(onClick = onClick),
        color = if (expanded) SelectedTeamBackground else SquadSurface,
        shape = RoundedCornerShape(9.dp),
        border = BorderStroke(1.dp, BorderSoft)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TeamInitialsBox(
                initials = team.initials,
                background = if (expanded) Color.White else Color(0xFFF0F3F8),
                textColor = if (expanded) SquadOrange else Color(0xFF95A1B5),
                size = 43.dp,
                fontSize = 16.sp,
                rounded = 7.dp
            )

            Spacer(modifier = Modifier.width(14.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = team.name,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = SquadTextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(3.dp))

                Text(
                    text = "${team.players} Jogadores • Federados",
                    fontSize = 11.sp,
                    color = SquadTextSecondary
                )
            }

            Icon(
                imageVector = if (expanded) {
                    Icons.Outlined.KeyboardArrowDown
                } else {
                    Icons.Outlined.KeyboardArrowRight
                },
                contentDescription = null,
                tint = if (expanded) SquadOrange else Color(0xFFB8C2D0),
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

@Composable
private fun TeamDetailsCard(
    team: ManageEventTeamUi,
    players: List<ManageEventPlayerUi>,
    onAddPlayerClick: () -> Unit,
    onEditTeamClick: () -> Unit,
    onPlayerViewClick: (ManageEventPlayerUi) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = SquadSurface,
        shape = RoundedCornerShape(9.dp),
        border = BorderStroke(1.dp, BorderSoft)
    ) {
        Column {
            TeamCardHeader(
                team = team,
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Edit,
                        contentDescription = "Editar equipa",
                        tint = SquadTextPrimary,
                        modifier = Modifier
                            .size(24.dp)
                            .clickable(onClick = onEditTeamClick)
                    )
                }
            )

            HorizontalDivider(color = BorderSoft)

            TeamPlayersSection(
                players = players,
                editable = false,
                onAddPlayerClick = onAddPlayerClick,
                onDeletePlayerClick = {},
                onPlayerViewClick = onPlayerViewClick
            )
        }
    }
}

@Composable
private fun EditTeamCard(
    team: ManageEventTeamUi,
    players: List<ManageEventPlayerUi>,
    onAddPlayerClick: () -> Unit,
    onDeleteTeamClick: () -> Unit,
    onDeletePlayerClick: (ManageEventPlayerUi) -> Unit,
    onPlayerViewClick: (ManageEventPlayerUi) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = SquadSurface,
        shape = RoundedCornerShape(9.dp),
        border = BorderStroke(1.dp, BorderSoft)
    ) {
        Column {
            TeamCardHeader(
                team = team,
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Delete,
                        contentDescription = "Eliminar equipa",
                        tint = SquadTextPrimary,
                        modifier = Modifier
                            .size(24.dp)
                            .clickable(onClick = onDeleteTeamClick)
                    )
                }
            )

            HorizontalDivider(color = BorderSoft)

            TeamPlayersSection(
                players = players,
                editable = true,
                onAddPlayerClick = onAddPlayerClick,
                onDeletePlayerClick = onDeletePlayerClick,
                onPlayerViewClick = onPlayerViewClick
            )
        }
    }
}

@Composable
private fun TeamCardHeader(
    team: ManageEventTeamUi,
    trailingIcon: @Composable () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp, vertical = 18.dp),
        verticalAlignment = Alignment.Top
    ) {
        TeamInitialsBox(
            initials = team.initials,
            background = SquadOrange,
            textColor = Color.White,
            size = 52.dp,
            fontSize = 19.sp,
            rounded = 7.dp
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = team.name,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = SquadTextPrimary
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.LocationOn,
                    contentDescription = null,
                    tint = SquadTextSecondary,
                    modifier = Modifier.size(16.dp)
                )

                Text(
                    text = "Lisboa,\nPT",
                    fontSize = 11.sp,
                    lineHeight = 14.sp,
                    color = SquadTextSecondary
                )
            }
        }

        trailingIcon()
    }
}

@Composable
private fun TeamPlayersSection(
    players: List<ManageEventPlayerUi>,
    editable: Boolean,
    onAddPlayerClick: () -> Unit,
    onDeletePlayerClick: (ManageEventPlayerUi) -> Unit,
    onPlayerViewClick: (ManageEventPlayerUi) -> Unit
) {
    Column(
        modifier = Modifier.padding(horizontal = 18.dp, vertical = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Team",
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = SquadTextPrimary,
                modifier = Modifier.weight(1f)
            )

            Text(
                text = "+ Add player",
                fontSize = 13.sp,
                color = SquadOrange,
                modifier = Modifier.clickable(onClick = onAddPlayerClick)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Name",
                fontSize = 13.sp,
                color = SquadTextSecondary,
                modifier = Modifier.weight(1f)
            )

            Text(
                text = "State",
                fontSize = 13.sp,
                color = SquadTextSecondary,
                modifier = Modifier.width(if (editable) 104.dp else 88.dp)
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        players.forEachIndexed { index, player ->
            PlayerRow(
                player = player,
                editable = editable,
                onViewClick = { onPlayerViewClick(player) },
                onDeleteClick = { onDeletePlayerClick(player) }
            )

            if (index < players.lastIndex) {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun TeamInitialsBox(
    initials: String,
    background: Color,
    textColor: Color,
    size: Dp,
    fontSize: TextUnit,
    rounded: Dp
) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(RoundedCornerShape(rounded))
            .background(background),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initials,
            fontSize = fontSize,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
    }
}

@Composable
private fun PlayerRow(
    player: ManageEventPlayerUi,
    editable: Boolean,
    onViewClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(32.dp),
            shape = CircleShape,
            color = if (player.initials == "JS") Color(0xFF111827) else Color(0xFFE9EDF4)
        ) {
            Box(
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = player.initials,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (player.initials == "JS") Color.White else SquadTextSecondary
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = player.name,
                fontSize = 13.sp,
                lineHeight = 15.sp,
                fontWeight = FontWeight.Medium,
                color = SquadTextPrimary
            )

            Text(
                text = "ID: #${player.id}",
                fontSize = 10.sp,
                color = SquadTextSecondary
            )
        }

        Row(
            modifier = Modifier.width(if (editable) 116.dp else 96.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .background(
                        color = if (player.validated) ValidatedGreen else PendingOrange,
                        shape = CircleShape
                    )
            )

            Spacer(modifier = Modifier.width(4.dp))

            Text(
                text = if (player.validated) "VALIDADO" else "PENDENTE",
                fontSize = 8.sp,
                fontWeight = FontWeight.Bold,
                color = if (player.validated) ValidatedGreen else PendingOrange
            )

            Spacer(modifier = Modifier.width(4.dp))

            Icon(
                imageVector = Icons.Outlined.Visibility,
                contentDescription = "Ver jogador",
                tint = SquadTextSecondary,
                modifier = Modifier
                    .size(17.dp)
                    .clickable(onClick = onViewClick)
            )

            if (editable) {
                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = "−",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Medium,
                    color = SquadTextPrimary,
                    modifier = Modifier.clickable(onClick = onDeleteClick)
                )
            }
        }
    }
}

@Composable
private fun CurrentGameCard() {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp),
        color = CurrentGameOrange,
        shape = RoundedCornerShape(9.dp),
        border = BorderStroke(1.dp, Color(0xFF8C3A00))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 18.dp, vertical = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "N E X T   G A M E  •  L I V E\nI N   2 H",
                    fontSize = 9.sp,
                    lineHeight = 12.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.6.sp,
                    color = SquadTextPrimary,
                    modifier = Modifier.weight(1f)
                )

                Surface(
                    color = Color.White.copy(alpha = 0.18f),
                    shape = RoundedCornerShape(999.dp)
                ) {
                    Text(
                        text = "Arena\nCentral",
                        fontSize = 9.sp,
                        lineHeight = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CurrentGameTeam(
                    initials = "SLB",
                    name = "BENFICA\nSTARS",
                    modifier = Modifier.weight(1f)
                )

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "VS",
                        fontSize = 42.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )

                    Box(
                        modifier = Modifier
                            .width(48.dp)
                            .height(3.dp)
                            .background(Color(0xFF803600), RoundedCornerShape(999.dp))
                    )
                }

                CurrentGameTeam(
                    initials = "FCP",
                    name = "FC PORTO\nELITE",
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun CurrentGameTeam(
    initials: String,
    name: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            modifier = Modifier.size(54.dp),
            color = Color.White.copy(alpha = 0.22f),
            shape = CircleShape
        ) {
            Box(
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = initials,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = name,
            fontSize = 10.sp,
            lineHeight = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            textAlign = TextAlign.Center
        )
    }
}

fun defaultManageEventTeams(): List<ManageEventTeamUi> {
    return listOf(
        ManageEventTeamUi(
            initials = "SLB",
            name = "Benfica Stars",
            players = 12,
            federated = true
        ),
        ManageEventTeamUi(
            initials = "FCP",
            name = "FC Porto Elite",
            players = 11,
            federated = true
        ),
        ManageEventTeamUi(
            initials = "SCP",
            name = "Sporting Lions",
            players = 14,
            federated = true
        ),
        ManageEventTeamUi(
            initials = "SCB",
            name = "Braga Warriors",
            players = 12,
            federated = true
        )
    )
}

fun defaultTeamPlayers(): List<ManageEventPlayerUi> {
    return listOf(
        ManageEventPlayerUi(
            initials = "JS",
            name = "João\nSilva",
            id = "44291",
            validated = true
        ),
        ManageEventPlayerUi(
            initials = "RL",
            name = "Ricardo\nLopes",
            id = "44295",
            validated = true
        ),
        ManageEventPlayerUi(
            initials = "MC",
            name = "Miguel\nCosta",
            id = "44310",
            validated = false
        )
    )
}

@Preview(
    name = "Stateful route",
    showBackground = true,
    showSystemUi = true
)
@Composable
fun ManageEventTeamsRoutePreview() {
    SquadUpTheme {
        ManageEventTeamsRoute()
    }
}

@Preview(
    name = "Editing mode",
    showBackground = true,
    showSystemUi = true
)
@Composable
fun ManageEventTeamsEditingPreview() {
    SquadUpTheme {
        val team = defaultManageEventTeams().first()

        ManageEventTeamsScreen(
            selectedRoute = AppRoutes.EVENTS,
            searchQuery = "",
            teams = defaultManageEventTeams(),
            expandedTeam = team,
            editingTeam = team,
            players = defaultTeamPlayers(),
            onSearchQueryChange = {},
            onNotificationsClick = {},
            onSettingsClick = {},
            onTeamsTabClick = {},
            onGamesTabClick = {},
            onStatsTabClick = {},
            onTeamClick = {},
            onAddTeamClick = {},
            onAddPlayerClick = {},
            onEditTeamClick = {},
            onDeleteTeamClick = {},
            onDeletePlayerClick = {},
            onPlayerViewClick = {},
            onCurrentGameAddClick = {},
            onNavItemClick = {}
        )
    }
}