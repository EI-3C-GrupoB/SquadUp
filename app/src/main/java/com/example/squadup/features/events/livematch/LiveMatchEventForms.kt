@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
package com.example.squadup.features.events.livematch

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
                    Text(abbr, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = if (selected) Color.White else SquadTextSecondary)
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
        Text(label, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = SquadTextSecondary, letterSpacing = 0.5.sp)
        Spacer(modifier = Modifier.height(4.dp))
        ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
            OutlinedTextField(
                value = selected?.name ?: "",
                onValueChange = {},
                readOnly = true,
                placeholder = { Text(stringResource(R.string.liveMatch_form_optional), color = SquadTextSecondary, fontSize = 13.sp) },
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
    Text(label, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = SquadTextSecondary, letterSpacing = 0.5.sp)
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
        containerColor = Color.White
    ) {
        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            Text(title, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = SquadTextPrimary)
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

// ─── Goal Form ────────────────────────────────────────────────────────────────

@Composable
fun GoalFormSheet(
    uiState: LiveMatchUiState,
    onDismiss: () -> Unit,
    onSave: (isHome: Boolean, playerName: String, description: String) -> Unit
) {
    var isHome by remember { mutableStateOf(true) }
    var scorer by remember { mutableStateOf<LiveMatchPlayer?>(null) }
    var assist by remember { mutableStateOf<LiveMatchPlayer?>(null) }
    var goalType by remember { mutableStateOf("Open Play") }

    val goalTypes = listOf("Open Play", "Header", "Free Kick", "Penalty", "Own Goal")
    val players = if (isHome) uiState.homePlayers else uiState.awayPlayers

    FormBottomSheet(
        title = stringResource(R.string.liveMatch_form_goal_title),
        onDismiss = onDismiss,
        onSave = {
            val desc = "$goalType${if (assist != null) " • Assist: ${assist!!.name}" else ""}"
            onSave(isHome, scorer?.name ?: "", desc)
        }
    ) {
        // Team
        FormSection(stringResource(R.string.liveMatch_form_team)) {
            TeamSelector(uiState.homeTeamAbbr, uiState.awayTeamAbbr, isHome) {
                isHome = it; scorer = null; assist = null
            }
        }
        Spacer(modifier = Modifier.height(14.dp))

        // Scorer
        FormSection(stringResource(R.string.liveMatch_form_goal_scorer)) {
            FormPlayerDropdown("", players, scorer) { scorer = it }
        }
        Spacer(modifier = Modifier.height(14.dp))

        // Goal type
        FormSection(stringResource(R.string.liveMatch_form_goal_type)) {
            var expanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
                OutlinedTextField(
                    value = goalType,
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { Icon(Icons.Outlined.KeyboardArrowDown, null) },
                    modifier = Modifier.fillMaxWidth().menuAnchor(),
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = SquadOrange, unfocusedBorderColor = SquadGrayLight)
                )
                ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    goalTypes.forEach { type ->
                        DropdownMenuItem(text = { Text(type) }, onClick = { goalType = type; expanded = false })
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(14.dp))

        // Assist (optional)
        FormSection(stringResource(R.string.liveMatch_form_assist)) {
            FormPlayerDropdown("", players, assist) { assist = it }
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

@Composable
fun SubstitutionFormSheet(
    uiState: LiveMatchUiState,
    onDismiss: () -> Unit,
    onSave: (isHome: Boolean, playerOut: String, playerIn: String) -> Unit
) {
    var isHome by remember { mutableStateOf(true) }
    var playerOut by remember { mutableStateOf<LiveMatchPlayer?>(null) }
    var playerInName by remember { mutableStateOf("") }

    val players = if (isHome) uiState.homePlayers else uiState.awayPlayers

    FormBottomSheet(
        title = stringResource(R.string.liveMatch_form_sub_title),
        onDismiss = onDismiss,
        onSave = { onSave(isHome, playerOut?.name ?: "", playerInName) }
    ) {
        FormSection(stringResource(R.string.liveMatch_form_team)) {
            TeamSelector(uiState.homeTeamAbbr, uiState.awayTeamAbbr, isHome) { isHome = it; playerOut = null }
        }
        Spacer(modifier = Modifier.height(14.dp))

        FormSection(stringResource(R.string.liveMatch_form_player_out)) {
            FormPlayerDropdown("", players, playerOut) { playerOut = it }
        }
        Spacer(modifier = Modifier.height(14.dp))

        FormSection(stringResource(R.string.liveMatch_form_player_in)) {
            OutlinedTextField(
                value = playerInName,
                onValueChange = { playerInName = it },
                placeholder = { Text(stringResource(R.string.liveMatch_form_player_in_hint), color = SquadTextSecondary, fontSize = 13.sp) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = SquadOrange, unfocusedBorderColor = SquadGrayLight)
            )
        }
    }
}
