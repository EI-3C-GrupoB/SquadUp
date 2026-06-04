package com.example.squadup.features.teams.createteam

import com.example.squadup.core.enums.SportType

data class CreateTeamUiState(
    val teamName: String = "",
    val selectedSportType: SportType = SportType.BASKETBALL,
    val teamDescription: String = "",
    val isPrivateTeam: Boolean = false
)