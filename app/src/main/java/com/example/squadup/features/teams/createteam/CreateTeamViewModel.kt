package com.example.squadup.features.teams.createteam

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.squadup.core.enums.SportType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CreateTeamViewModel : ViewModel() {

    private val repository = CreateTeamRepository()
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

    fun createTeam(onSuccess: () -> Unit) {
        val currentState = _uiState.value
        if (currentState.teamName.isBlank()) return

        viewModelScope.launch {
            _uiState.value = currentState.copy(isSaving = true)
            repository.createTeam(currentState)
                .onSuccess {
                    _uiState.value = _uiState.value.copy(isSaving = false)
                    onSuccess()
                }
                .onFailure {
                    _uiState.value = _uiState.value.copy(isSaving = false)
                }
        }
    }
}
