@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
package com.example.squadup.features.events.livematch

import com.example.squadup.core.ui.components.responsiveHorizontalPadding

import androidx.compose.material3.MaterialTheme

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.squadup.R
import com.example.squadup.core.enums.SportType
import com.example.squadup.core.enums.FootballInfraction
import com.example.squadup.core.enums.BasketballInfraction
import com.example.squadup.core.enums.VolleyballInfraction
import com.example.squadup.core.enums.PaddleInfraction
import com.example.squadup.core.ui.theme.*

// ─── Shared team selector ─────────────────────────────────────────────────────

@Composable
private fun TeamSelector(
    homeAbbr: String,
    awayAbbr: String,
    isHome: Boolean,
    onSelect: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        listOf(true to homeAbbr, false to awayAbbr).forEach { (home, abbr) ->
            val selected = isHome == home
            Surface(
                modifier = Modifier.weight(1f).clickable { onSelect(home) },
                color = if (selected) SquadOrange else SquadGrayLight,
                shape = RoundedCornerShape(8.dp)
            ) {
                Box(modifier = Modifier.padding(10.dp), contentAlignment = Alignment.Center) {
                    Text(abbr, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = if (selected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

@Composable
private fun FormPlayerDropdown(
    label: String,
    players: List<LiveMatchPlayer>,
    selected: LiveMatchPlayer?,
    onSelect: (LiveMatchPlayer) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Column {
        Text(label, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant, letterSpacing = 0.5.sp)
        Spacer(modifier = Modifier.height(4.dp))
        ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
            OutlinedTextField(
                value = selected?.name ?: "",
                onValueChange = {},
                readOnly = true,
                placeholder = { Text(stringResource(R.string.liveMatch_form_optional), color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp) },
                trailingIcon = { Icon(Icons.Outlined.KeyboardArrowDown, null) },
                modifier = Modifier.fillMaxWidth().menuAnchor(),
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = SquadOrange, unfocusedBorderColor = SquadGrayLight)
            )
            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                players.forEach { player ->
                    DropdownMenuItem(
                        text = { Text(player.name) },
                        onClick = { onSelect(player); expanded = false }
                    )
                }
            }
        }
    }
}

@Composable
private fun FormSection(label: String, content: @Composable () -> Unit) {
    Text(label, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant, letterSpacing = 0.5.sp)
    Spacer(modifier = Modifier.height(6.dp))
    content()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FormBottomSheet(
    title: String,
    onDismiss: () -> Unit,
    onSave: () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(modifier = Modifier.padding(horizontal = responsiveHorizontalPadding(20.dp))) {
            Text(title, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
            Spacer(modifier = Modifier.height(20.dp))
            content()
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onSave,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SquadOrange, contentColor = Color.White)
            ) {
                Text(stringResource(R.string.liveMatch_form_save), fontSize = 15.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

// ─── Goal / Score Form (sport-aware) ─────────────────────────────────────────

@Composable
fun GoalFormSheet(
    uiState: LiveMatchUiState,
    onDismiss: () -> Unit,
    onSave: (isHome: Boolean, playerName: String, description: String, delta: Int) -> Unit
) {
    when (uiState.sportType) {
        SportType.BASKETBALL -> BasketballScoreFormSheet(uiState, onDismiss, onSave)
        SportType.VOLLEYBALL, SportType.PADDLE -> SimpleScoreFormSheet(uiState, onDismiss, onSave)
        else -> FootballScoreFormSheet(uiState, onDismiss, onSave)
    }
}

@Composable
private fun FootballScoreFormSheet(
    uiState: LiveMatchUiState,
    onDismiss: () -> Unit,
    onSave: (isHome: Boolean, playerName: String, description: String, delta: Int) -> Unit
) {
    var isHome by remember { mutableStateOf(true) }
    var scorer by remember { mutableStateOf<LiveMatchPlayer?>(null) }
    var assist by remember { mutableStateOf<LiveMatchPlayer?>(null) }
    var goalType by remember { mutableStateOf("Jogo aberto") }
    val goalTypes = listOf("Jogo aberto", "Cabeceamento", "Livre", "Penálti", "Autogolo")
    val players = if (isHome) uiState.homePlayers else uiState.awayPlayers

    FormBottomSheet(
        title = stringResource(R.string.liveMatch_form_goal_title),
        onDismiss = onDismiss,
        onSave = {
            val desc = "$goalType${if (assist != null) " • Assist: ${assist!!.name}" else ""}"
            onSave(isHome, scorer?.name ?: "", desc, 1)
        }
    ) {
        FormSection(stringResource(R.string.liveMatch_form_team)) {
            TeamSelector(uiState.homeTeamAbbr, uiState.awayTeamAbbr, isHome) { isHome = it; scorer = null; assist = null }
        }
        Spacer(Modifier.height(14.dp))
        FormSection(stringResource(R.string.liveMatch_form_goal_scorer)) {
            FormPlayerDropdown("", players, scorer) { scorer = it }
        }
        Spacer(Modifier.height(14.dp))
        FormSection(stringResource(R.string.liveMatch_form_goal_type)) {
            var expanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
                OutlinedTextField(value = goalType, onValueChange = {}, readOnly = true,
                    trailingIcon = { Icon(Icons.Outlined.KeyboardArrowDown, null) },
                    modifier = Modifier.fillMaxWidth().menuAnchor(), shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = SquadOrange, unfocusedBorderColor = SquadGrayLight))
                ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    goalTypes.forEach { type -> DropdownMenuItem(text = { Text(type) }, onClick = { goalType = type; expanded = false }) }
                }
            }
        }
        Spacer(Modifier.height(14.dp))
        FormSection(stringResource(R.string.liveMatch_form_assist)) {
            FormPlayerDropdown("", players, assist) { assist = it }
        }
    }
}

@Composable
private fun BasketballScoreFormSheet(
    uiState: LiveMatchUiState,
    onDismiss: () -> Unit,
    onSave: (isHome: Boolean, playerName: String, description: String, delta: Int) -> Unit
) {
    var isHome by remember { mutableStateOf(true) }
    var scorer by remember { mutableStateOf<LiveMatchPlayer?>(null) }
    var selectedDelta by remember { mutableStateOf(2) }
    val players = if (isHome) uiState.homePlayers else uiState.awayPlayers
    val scoreOptions = listOf(1 to "Livre (+1)", 2 to "2 Pontos (+2)", 3 to "3 Pontos (+3)")

    FormBottomSheet(
        title = "Registar Pontos",
        onDismiss = onDismiss,
        onSave = {
            val desc = scoreOptions.firstOrNull { it.first == selectedDelta }?.second ?: "+$selectedDelta"
            onSave(isHome, scorer?.name ?: "", desc, selectedDelta)
        }
    ) {
        FormSection(stringResource(R.string.liveMatch_form_team)) {
            TeamSelector(uiState.homeTeamAbbr, uiState.awayTeamAbbr, isHome) { isHome = it; scorer = null }
        }
        Spacer(Modifier.height(14.dp))
        FormSection("TIPO DE PONTUAÇÃO") {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                scoreOptions.forEach { (delta, label) ->
                    val selected = selectedDelta == delta
                    Surface(
                        onClick = { selectedDelta = delta },
                        modifier = Modifier.weight(1f),
                        color = if (selected) SquadOrange else SquadGrayLight,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("+$delta", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold,
                                color = if (selected) Color.White else MaterialTheme.colorScheme.onSurface)
                            Text(label.substringBefore(" ("), fontSize = 9.sp,
                                color = if (selected) Color.White.copy(alpha = 0.85f) else MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                        }
                    }
                }
            }
        }
        Spacer(Modifier.height(14.dp))
        FormSection(stringResource(R.string.liveMatch_form_goal_scorer)) {
            FormPlayerDropdown("", players, scorer) { scorer = it }
        }
    }
}

@Composable
private fun SimpleScoreFormSheet(
    uiState: LiveMatchUiState,
    onDismiss: () -> Unit,
    onSave: (isHome: Boolean, playerName: String, description: String, delta: Int) -> Unit
) {
    var isHome by remember { mutableStateOf(true) }
    var scorer by remember { mutableStateOf<LiveMatchPlayer?>(null) }
    val players = if (isHome) uiState.homePlayers else uiState.awayPlayers
    val title = if (uiState.sportType == SportType.VOLLEYBALL) "Registar Ponto" else "Registar Ponto/Set"

    FormBottomSheet(title = title, onDismiss = onDismiss, onSave = {
        onSave(isHome, scorer?.name ?: "", "Ponto", 1)
    }) {
        FormSection(stringResource(R.string.liveMatch_form_team)) {
            TeamSelector(uiState.homeTeamAbbr, uiState.awayTeamAbbr, isHome) { isHome = it; scorer = null }
        }
        Spacer(Modifier.height(14.dp))
        FormSection(stringResource(R.string.liveMatch_form_player)) {
            FormPlayerDropdown("", players, scorer) { scorer = it }
        }
    }
}

// ─── Infraction Form ──────────────────────────────────────────────────────────

@Composable
fun InfractionFormSheet(
    uiState: LiveMatchUiState,
    onDismiss: () -> Unit,
    onSave: (isHome: Boolean, playerName: String, description: String) -> Unit
) {
    var isHome by remember { mutableStateOf(true) }
    var player by remember { mutableStateOf<LiveMatchPlayer?>(null) }
    var infractionType by remember { mutableStateOf("") }

    val players = if (isHome) uiState.homePlayers else uiState.awayPlayers
    val infractionTypes = when (uiState.sportType) {
        SportType.SOCCER, SportType.FUTSAL -> FootballInfraction.entries.map { it.name.replace("_", " ") }
        SportType.BASKETBALL               -> BasketballInfraction.entries.map { it.name.replace("_", " ") }
        SportType.VOLLEYBALL               -> VolleyballInfraction.entries.map { it.name.replace("_", " ") }
        SportType.PADDLE                   -> PaddleInfraction.entries.map { it.name.replace("_", " ") }
    }
    if (infractionType.isBlank() && infractionTypes.isNotEmpty()) infractionType = infractionTypes.first()

    FormBottomSheet(
        title = stringResource(R.string.liveMatch_form_infraction_title),
        onDismiss = onDismiss,
        onSave = { onSave(isHome, player?.name ?: "", infractionType) }
    ) {
        FormSection(stringResource(R.string.liveMatch_form_team)) {
            TeamSelector(uiState.homeTeamAbbr, uiState.awayTeamAbbr, isHome) { isHome = it; player = null }
        }
        Spacer(modifier = Modifier.height(14.dp))

        FormSection(stringResource(R.string.liveMatch_form_player)) {
            FormPlayerDropdown("", players, player) { player = it }
        }
        Spacer(modifier = Modifier.height(14.dp))

        FormSection(stringResource(R.string.liveMatch_form_infraction_type)) {
            var expanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
                OutlinedTextField(
                    value = infractionType,
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { Icon(Icons.Outlined.KeyboardArrowDown, null) },
                    modifier = Modifier.fillMaxWidth().menuAnchor(),
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = SquadOrange, unfocusedBorderColor = SquadGrayLight)
                )
                ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    infractionTypes.forEach { type ->
                        DropdownMenuItem(text = { Text(type) }, onClick = { infractionType = type; expanded = false })
                    }
                }
            }
        }
    }
}

// ─── Substitution Form ────────────────────────────────────────────────────────

private data class SubPair(val playerOut: LiveMatchPlayer? = null, val playerIn: LiveMatchPlayer? = null)

@Composable
fun SubstitutionFormSheet(
    uiState: LiveMatchUiState,
    onDismiss: () -> Unit,
    onSave: (isHome: Boolean, playerOut: String, playerIn: String) -> Unit
) {
    var isHome by remember { mutableStateOf(true) }
    var pairs by remember { mutableStateOf(listOf(SubPair())) }

    val players = if (isHome) uiState.homePlayers else uiState.awayPlayers

    FormBottomSheet(
        title = stringResource(R.string.liveMatch_form_sub_title),
        onDismiss = onDismiss,
        onSave = {
            pairs.forEach { pair ->
                if (pair.playerOut != null || pair.playerIn != null) {
                    onSave(isHome, pair.playerOut?.name ?: "", pair.playerIn?.name ?: "")
                }
            }
        }
    ) {
        FormSection(stringResource(R.string.liveMatch_form_team)) {
            TeamSelector(uiState.homeTeamAbbr, uiState.awayTeamAbbr, isHome) {
                isHome = it
                pairs = listOf(SubPair())
            }
        }
        Spacer(modifier = Modifier.height(14.dp))

        pairs.forEachIndexed { index, pair ->
            if (index > 0) {
                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = SquadGrayLight)
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Column(modifier = Modifier.weight(1f)) {
                    FormSection(stringResource(R.string.liveMatch_form_player_out)) {
                        FormPlayerDropdown("", players, pair.playerOut) { selected ->
                            pairs = pairs.toMutableList().also { it[index] = it[index].copy(playerOut = selected) }
                        }
                    }
                }
                Column(modifier = Modifier.weight(1f)) {
                    FormSection(stringResource(R.string.liveMatch_form_player_in)) {
                        FormPlayerDropdown("", players, pair.playerIn) { selected ->
                            pairs = pairs.toMutableList().also { it[index] = it[index].copy(playerIn = selected) }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
        }

        Spacer(modifier = Modifier.height(6.dp))
        TextButton(
            onClick = { pairs = pairs + SubPair() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Outlined.Add, null, modifier = Modifier.size(16.dp), tint = SquadOrange)
            Spacer(modifier = Modifier.width(4.dp))
            Text(stringResource(R.string.liveMatch_form_sub_add), color = SquadOrange, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

// ─── Advanced Stats Form ──────────────────────────────────────────────────────

@Composable
fun AdvancedStatsFormSheet(
    uiState: LiveMatchUiState,
    onDismiss: () -> Unit,
    onSave: (isHome: Boolean, statName: String, value: Int) -> Unit
) {
    var isHome by remember { mutableStateOf(true) }
    var selectedStat by remember { mutableStateOf("") }
    var statValue by remember { mutableStateOf(1) }

    val statOptions = when (uiState.sportType) {
        SportType.SOCCER, SportType.FUTSAL -> listOf("Shots", "Shots on Goal", "Corners", "Fouls", "Offsides", "Saves")
        SportType.BASKETBALL               -> listOf("Assists", "Rebounds", "Blocks", "Steals", "Turnovers", "3-Pointers")
        SportType.VOLLEYBALL               -> listOf("Aces", "Blocks", "Digs", "Errors", "Sets Won")
        SportType.PADDLE                   -> listOf("Winners", "Errors", "Aces", "Smashes")
    }
    if (selectedStat.isBlank()) selectedStat = statOptions.first()

    FormBottomSheet(
        title = stringResource(R.string.liveMatch_form_stats_title),
        onDismiss = onDismiss,
        onSave = { onSave(isHome, selectedStat, statValue) }
    ) {
        FormSection(stringResource(R.string.liveMatch_form_team)) {
            TeamSelector(uiState.homeTeamAbbr, uiState.awayTeamAbbr, isHome) { isHome = it }
        }
        Spacer(modifier = Modifier.height(14.dp))

        FormSection(stringResource(R.string.liveMatch_form_stats_type)) {
            var expanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
                OutlinedTextField(
                    value = selectedStat,
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { Icon(Icons.Outlined.KeyboardArrowDown, null) },
                    modifier = Modifier.fillMaxWidth().menuAnchor(),
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = SquadOrange, unfocusedBorderColor = SquadGrayLight)
                )
                ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    statOptions.forEach { stat ->
                        DropdownMenuItem(text = { Text(stat) }, onClick = { selectedStat = stat; expanded = false })
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(14.dp))

        FormSection(stringResource(R.string.liveMatch_form_stats_value)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    onClick = { if (statValue > 1) statValue-- },
                    color = SquadGrayLight,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.size(44.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Outlined.Remove, null, tint = MaterialTheme.colorScheme.onSurface, modifier = Modifier.size(20.dp))
                    }
                }
                Text("$statValue", fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.weight(1f), textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                Surface(
                    onClick = { statValue++ },
                    color = SquadOrange,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.size(44.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Outlined.Add, null, tint = Color.White, modifier = Modifier.size(20.dp))
                    }
                }
            }
        }
    }
}
