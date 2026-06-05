package com.example.squadup.features.teams.createteam

import androidx.lifecycle.ViewModel
import com.example.squadup.core.enums.SportType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class CreateTeamViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(CreateTeamUiState())
    val uiState: StateFlow<CreateTeamUiState> = _uiState.asStateFlow()

    fun onTeamNameChange(value: String) {
        _uiState.value = _uiState.value.copy(teamName = value)
    }

    fun onSportTypeSelected(sportType: SportType) {
        _uiState.value = _uiState.value.copy(selectedSportType = sportType)
    }

    fun onTeamDescriptionChange(value: String) {
        _uiState.value = _uiState.value.copy(teamDescription = value)
    }

    fun onPrivateTeamChange(isPrivate: Boolean) {
        _uiState.value = _uiState.value.copy(isPrivateTeam = isPrivate)
    }
}