package com.example.squadup.features.events.formteams

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.squadup.core.ui.components.AppHeader
import com.example.squadup.core.ui.components.AppNavBar
import com.example.squadup.core.ui.theme.*
import com.example.squadup.core.utils.AppLanguage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormTeamsScreen(
    uiState: FormTeamsUiState,
    selectedRoute: String,
    onNavItemClick: (String) -> Unit,
    onBackClick: () -> Unit,
    onRandomize: () -> Unit,
    onSelectPlayerForAssign: (FormTeamPlayer?) -> Unit,
    onAssignPlayerToTeam: (Int) -> Unit,
    onUnassignPlayer: (FormTeamPlayer) -> Unit,
    onAddTeam: () -> Unit,
    onRemoveTeam: (Int) -> Unit,
    onSaveRoster: () -> Unit,
    selectedLanguage: AppLanguage,
    isDarkMode: Boolean,
    onLanguageChange: (AppLanguage) -> Unit,
    onDarkModeChange: (Boolean) -> Unit,
) {
    // Player assign bottom sheet
    val playerToAssign = uiState.selectedPlayerForAssign
    if (playerToAssign != null) {
        ModalBottomSheet(
            onDismissRequest = { onSelectPlayerForAssign(null) },
            containerColor = SquadBackground
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 28.dp)
            ) {
                Text(
                    "Atribuir a equipa",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = SquadTextPrimary
                )
                Text(
                    playerToAssign.name,
                    fontSize = 14.sp,
                    color = SquadTextSecondary
                )
                Spacer(Modifier.height(16.dp))
                uiState.teams.forEachIndexed { index, team ->
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable { onAssignPlayerToTeam(index) },
                        color = SquadSurface,
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF0E1DC))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .background(SquadOrange, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    ('A' + index).toString(),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                            Spacer(Modifier.width(12.dp))
                            Column(Modifier.weight(1f)) {
                                Text(team.name, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = SquadTextPrimary)
                                Text("${team.players.size}/${team.capacity} jogadores", fontSize = 12.sp, color = SquadTextSecondary)
                            }
                            Icon(Icons.Outlined.ChevronRight, null, tint = SquadOrange, modifier = Modifier.size(20.dp))
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))
                TextButton(onClick = { onSelectPlayerForAssign(null) }, modifier = Modifier.fillMaxWidth()) {
                    Text("Cancelar", color = SquadTextSecondary, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }

    Scaffold(
        topBar = {
            AppHeader(
                showLogo = false,
                title = "Formar Equipas",
                showBackButton = true,
                onBackClick = onBackClick,
                showSettingsButton = true,
                selectedLanguage = selectedLanguage,
                isDarkMode = isDarkMode,
                onLanguageChange = onLanguageChange,
                onDarkModeChange = onDarkModeChange
            )
        },
        bottomBar = { AppNavBar(selectedRoute = selectedRoute, onItemClick = onNavItemClick) }
    ) { innerPadding ->
        when {
            uiState.isLoading -> Box(
                Modifier.fillMaxSize().background(SquadBackground).padding(innerPadding),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator(color = SquadOrange) }

            uiState.errorMessage != null -> Box(
                Modifier.fillMaxSize().background(SquadBackground).padding(innerPadding).padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(uiState.errorMessage, color = SquadTextSecondary, fontSize = 14.sp)
            }

            else -> Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(SquadBackground)
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                // Save Roster button at top
                Button(
                    onClick = onSaveRoster,
                    enabled = uiState.canSave,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SquadOrange,
                        contentColor = Color.White,
                        disabledContainerColor = SquadSurface,
                        disabledContentColor = SquadTextSecondary
                    )
                ) {
                    if (uiState.isSaving) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp,
                            color = Color.White
                        )
                    } else {
                        Text("Guardar Equipa", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(Modifier.height(16.dp))

                // Registration overview card
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = SquadSurface,
                    shape = RoundedCornerShape(12.dp),
                    shadowElevation = 1.dp,
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF0E1DC))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "VISÃO GERAL",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = SquadTextSecondary,
                            letterSpacing = 0.8.sp
                        )
                        Spacer(Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(Modifier.weight(1f)) {
                                Text(
                                    uiState.totalAthletes.toString(),
                                    fontSize = 32.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SquadTextPrimary
                                )
                                Text("Atletas totais", fontSize = 13.sp, color = SquadTextSecondary)
                            }
                            Surface(
                                color = Color(0xFFFFF3E0),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    "+${uiState.unassignedPlayers.size} não atribuídos",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = SquadOrange,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(20.dp))

                // Unassigned players section
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Jogadores não atribuídos", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = SquadTextPrimary)
                        if (uiState.unassignedPlayers.isNotEmpty()) {
                            Text(
                                "${uiState.unassignedPlayers.size} restantes",
                                fontSize = 12.sp,
                                color = SquadOrange,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                    Button(
                        onClick = onRandomize,
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SquadOrange, contentColor = Color.White),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Icon(Icons.Outlined.Shuffle, null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Aleatório", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(Modifier.height(12.dp))

                if (uiState.unassignedPlayers.isEmpty()) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = Color(0xFFE8F5E9),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Outlined.CheckCircle, null, tint = Color(0xFF2E7D32), modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(10.dp))
                            Text("Todos os jogadores foram atribuídos.", fontSize = 13.sp, color = Color(0xFF2E7D32), fontWeight = FontWeight.SemiBold)
                        }
                    }
                } else {
                    uiState.unassignedPlayers.forEach { player ->
                        PlayerChip(
                            player = player,
                            onClick = { onSelectPlayerForAssign(player) }
                        )
                        Spacer(Modifier.height(8.dp))
                    }
                }

                Spacer(Modifier.height(24.dp))

                if (uiState.awaitingPaymentPlayers.isNotEmpty()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                "Aguarda Pagamento",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = SquadTextPrimary
                            )
                            Text(
                                "${uiState.awaitingPaymentPlayers.size} jogadores",
                                fontSize = 12.sp,
                                color = SquadTextSecondary,
                                fontWeight = FontWeight.Normal
                            )
                        }
                    }

                    Spacer(Modifier.height(8.dp))

                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = Color(0xFFF5F5F5),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            uiState.awaitingPaymentPlayers.forEachIndexed { index, player ->
                                if (index > 0) Spacer(Modifier.height(6.dp))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        modifier = Modifier.size(32.dp),
                                        shape = CircleShape,
                                        color = Color(0xFFBDBDBD)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(
                                                text = player.initials,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            )
                                        }
                                    }
                                    Spacer(Modifier.width(10.dp))
                                    Text(
                                        text = player.name,
                                        fontSize = 13.sp,
                                        color = SquadTextSecondary,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Spacer(Modifier.weight(1f))
                                    Surface(
                                        color = Color(0xFFFFF8E1),
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            "Pendente",
                                            fontSize = 11.sp,
                                            color = Color(0xFFF57F17),
                                            fontWeight = FontWeight.SemiBold,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(24.dp))
                }

                // Teams section
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Equipas activas", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = SquadTextPrimary)
                    TextButton(onClick = onAddTeam) {
                        Icon(Icons.Outlined.Add, null, tint = SquadOrange, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Adicionar", fontSize = 13.sp, color = SquadOrange, fontWeight = FontWeight.SemiBold)
                    }
                }

                Spacer(Modifier.height(12.dp))

                uiState.teams.forEachIndexed { index, team ->
                    TeamCard(
                        team = team,
                        teamIndex = index,
                        onRemoveTeam = { onRemoveTeam(index) },
                        onUnassignPlayer = onUnassignPlayer
                    )
                    Spacer(Modifier.height(12.dp))
                }

                // Tip
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color(0xFFFFF3E0),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(Icons.Outlined.Info, null, tint = SquadOrange, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(10.dp))
                        Text(
                            "Toca num jogador para o atribuir a uma equipa. Usa o botão Aleatório para distribuição automática equilibrada.",
                            fontSize = 12.sp,
                            color = SquadTextSecondary,
                            lineHeight = 17.sp
                        )
                    }
                }

                Spacer(Modifier.height(28.dp))
            }
        }
    }
}

@Composable
private fun PlayerChip(
    player: FormTeamPlayer,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        color = Color.White,
        shape = RoundedCornerShape(10.dp),
        shadowElevation = 1.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF0E1DC))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(36.dp).background(SquadOrange.copy(alpha = 0.15f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(player.initials, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = SquadOrange)
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(player.name, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = SquadTextPrimary)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    repeat(3) { i ->
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .background(
                                    if (i < player.experienceLevel) SquadOrange else SquadGray,
                                    CircleShape
                                )
                        )
                        if (i < 2) Spacer(Modifier.width(3.dp))
                    }
                    Spacer(Modifier.width(6.dp))
                    Text(
                        when (player.experienceLevel) { 1 -> "Iniciante"; 2 -> "Intermédio"; else -> "Avançado" },
                        fontSize = 11.sp,
                        color = SquadTextSecondary
                    )
                }
            }
            Icon(Icons.Outlined.Add, null, tint = SquadOrange, modifier = Modifier.size(20.dp))
        }
    }
}

@Composable
private fun TeamCard(
    team: FormTeam,
    teamIndex: Int,
    onRemoveTeam: () -> Unit,
    onUnassignPlayer: (FormTeamPlayer) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        shape = RoundedCornerShape(12.dp),
        shadowElevation = 2.dp
    ) {
        Column {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SquadOrange, RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    team.name.uppercase(),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    "${team.players.size}/${team.capacity}",
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.85f),
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.width(8.dp))
                IconButton(onClick = onRemoveTeam, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Outlined.Delete, null, tint = Color.White.copy(alpha = 0.7f), modifier = Modifier.size(16.dp))
                }
            }

            Column(modifier = Modifier.padding(12.dp)) {
                if (team.players.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxWidth().height(44.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Sem jogadores atribuídos", fontSize = 13.sp, color = SquadTextSecondary)
                    }
                } else {
                    team.players.forEach { player ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(30.dp)
                                    .background(SquadOrange.copy(alpha = 0.12f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(player.initials, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SquadOrange)
                            }
                            Spacer(Modifier.width(10.dp))
                            Text(player.name, fontSize = 13.sp, color = SquadTextPrimary, modifier = Modifier.weight(1f))
                            IconButton(
                                onClick = { onUnassignPlayer(player) },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(Icons.Outlined.Remove, null, tint = SquadTextSecondary, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}
