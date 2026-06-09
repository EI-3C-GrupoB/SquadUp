package com.example.squadup.features.events.formteams

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FormTeamsViewModel : ViewModel() {

    private val repository = FormTeamsRepository()
    private val _uiState = MutableStateFlow(FormTeamsUiState())
    val uiState: StateFlow<FormTeamsUiState> = _uiState.asStateFlow()
    private var hasLoaded = false

    fun loadEvent(eventId: String) {
        if (hasLoaded) return
        hasLoaded = true
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            repository.loadEvent(eventId)
                .onSuccess { loaded ->
                    // Only overwrite if we are still in the initial loading state
                    // (user hasn't interacted yet). If the coroutine raced with user
                    // interaction, keep the current interactive state.
                    _uiState.update { current ->
                        if (current.isLoading) loaded else current
                    }
                }
                .onFailure { e ->
                    hasLoaded = false
                    _uiState.update { it.copy(isLoading = false, errorMessage = e.message ?: "Erro ao carregar evento.") }
                }
        }
    }

    fun onRandomize() {
        val state = _uiState.value
        val allPlayers = (state.unassignedPlayers + state.teams.flatMap { it.players })
            .distinctBy { it.userId }
            .shuffled()
            .sortedByDescending { it.experienceLevel }

        val numTeams = state.teams.size.coerceAtLeast(2)
        val emptyTeams = state.teams.map { it.copy(players = emptyList()) }.toMutableList()

        // Round-robin distribution (balanced by experience via sorted order)
        allPlayers.forEachIndexed { i, player ->
            val idx = i % numTeams
            emptyTeams[idx] = emptyTeams[idx].copy(players = emptyTeams[idx].players + player)
        }

        _uiState.update { it.copy(unassignedPlayers = emptyList(), teams = emptyTeams) }
    }

    fun onSelectPlayerForAssign(player: FormTeamPlayer?) {
        _uiState.update { it.copy(selectedPlayerForAssign = player) }
    }

    fun onAssignPlayerToTeam(teamIndex: Int) {
        _uiState.update { state ->
            val player = state.selectedPlayerForAssign ?: return@update state
            // Use userId as the identity key — registrationId can collide in edge cases
            val newUnassigned = state.unassignedPlayers.filterNot { it.userId == player.userId }
            val newTeams = state.teams.mapIndexed { i, team ->
                val filtered = team.players.filterNot { it.userId == player.userId }
                if (i == teamIndex) team.copy(players = filtered + player) else team.copy(players = filtered)
            }
            state.copy(unassignedPlayers = newUnassigned, teams = newTeams, selectedPlayerForAssign = null)
        }
    }

    fun onUnassignPlayer(player: FormTeamPlayer) {
        _uiState.update { state ->
            val newTeams = state.teams.map { team ->
                team.copy(players = team.players.filterNot { it.userId == player.userId })
            }
            state.copy(unassignedPlayers = state.unassignedPlayers + player, teams = newTeams)
        }
    }

    fun onAddTeam() {
        _uiState.update { state ->
            val newIndex = state.teams.size
            // Use a letter that doesn't clash with existing team names
            val usedLetters = state.teams.mapNotNull { t ->
                t.name.lastOrNull()?.takeIf { it in 'A'..'Z' }
            }.toSet()
            val nextLetter = ('A'..'Z').firstOrNull { it !in usedLetters } ?: ('A' + newIndex)
            val newTeam = FormTeam(
                index = newIndex,
                name = "Equipa $nextLetter",
                capacity = sportCapacity(state.sportType.name)
            )
            state.copy(teams = state.teams + newTeam)
        }
    }

    fun onRemoveTeam(teamIndex: Int) {
        _uiState.update { state ->
            val team = state.teams.getOrNull(teamIndex) ?: return@update state
            val newUnassigned = state.unassignedPlayers + team.players
            // Reindex but keep original names — never rename saved teams
            val remaining = state.teams.filterIndexed { i, _ -> i != teamIndex }
                .mapIndexed { i, t -> t.copy(index = i) }
            state.copy(unassignedPlayers = newUnassigned, teams = remaining)
        }
    }

    fun saveRoster(onSuccess: () -> Unit) {
        val state = _uiState.value
        if (!state.canSave) return

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null) }
            repository.saveTeams(state.eventId, state.teams, state.unassignedPlayers, state.sportType)
                .onSuccess {
                    _uiState.update { it.copy(isSaving = false, saveSuccess = true) }
                    onSuccess()
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isSaving = false, errorMessage = e.message ?: "Erro ao guardar equipas.") }
                }
        }
    }

    private fun sportCapacity(sportName: String): Int = when {
        "SOCCER" in sportName -> 11
        "FUTSAL" in sportName -> 5
        "BASKETBALL" in sportName -> 5
        "VOLLEYBALL" in sportName -> 6
        "PADDLE" in sportName -> 2
        else -> 8
    }
}
