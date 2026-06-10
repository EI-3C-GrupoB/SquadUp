package com.example.squadup.features.events.formteams

import com.example.squadup.core.enums.SportType

data class FormTeamsUiState(
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val eventId: String = "",
    val eventName: String = "",
    val sportType: SportType = SportType.SOCCER,
    val maxTeams: Int = 2,
    val unassignedPlayers: List<FormTeamPlayer> = emptyList(),
    val awaitingPaymentPlayers: List<FormTeamPlayer> = emptyList(),
    val teams: List<FormTeam> = emptyList(),
    val selectedPlayerForAssign: FormTeamPlayer? = null,
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false
) {
    val totalAthletes: Int get() = unassignedPlayers.size + teams.sumOf { it.players.size }
    val canSave: Boolean get() = teams.isNotEmpty() && teams.any { it.players.isNotEmpty() } && !isSaving
}

data class FormTeamPlayer(
    val userId: Int,
    val registrationId: Int,
    val name: String,
    val initials: String,
    val experienceLevel: Int = 1
)

data class FormTeam(
    val index: Int,
    val name: String,
    val players: List<FormTeamPlayer> = emptyList(),
    val capacity: Int = 8,
    val savedTeamId: Int? = null  // non-null if already persisted in DB
)
