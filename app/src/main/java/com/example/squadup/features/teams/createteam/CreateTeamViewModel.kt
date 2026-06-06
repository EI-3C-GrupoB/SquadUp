package com.example.squadup.features.teams.createteam

import android.net.Uri
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

    fun onLogoUriChange(uri: Uri?) {
        _uiState.value = _uiState.value.copy(logoUri = uri)
    }

    fun createTeam(onSuccess: () -> Unit, context: android.content.Context) {
        val currentState = _uiState.value
        if (currentState.teamName.isBlank()) return

        viewModelScope.launch {
            _uiState.value = currentState.copy(isSaving = true)

            val logoBytes = currentState.logoUri?.let { uri ->
                context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
            }

            repository.createTeam(currentState, logoBytes)
                .onSuccess {
                    _uiState.value = _uiState.value.copy(isSaving = false)
                    onSuccess()
                }
                .onFailure { exception ->
                    android.util.Log.e("CreateTeam", "Erro ao criar equipa", exception)
                    _uiState.value = _uiState.value.copy(isSaving = false)
                }
        }
    }
}
