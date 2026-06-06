package com.example.squadup.features.teams.createteam

import android.net.Uri
import com.example.squadup.core.enums.SportType

data class CreateTeamUiState(
    val teamName: String = "",
    val selectedSportType: SportType = SportType.BASKETBALL,
    val teamDescription: String = "",
    val isPrivateTeam: Boolean = false,
    val logoUri: Uri? = null,
    val isSaving: Boolean = false
)
