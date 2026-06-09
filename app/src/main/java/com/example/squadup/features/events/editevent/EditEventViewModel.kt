package com.example.squadup.features.events.editevent

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EditEventViewModel : ViewModel() {

    private val repository = EditEventRepository()
    private val _uiState = MutableStateFlow(EditEventUiState())
    val uiState: StateFlow<EditEventUiState> = _uiState.asStateFlow()

    fun loadEvent(eventId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            repository.loadEvent(eventId)
                .onSuccess { state -> _uiState.value = state }
                .onFailure { e -> _uiState.update { it.copy(isLoading = false, errorMessage = e.message) } }
        }
    }

    fun onTitleChange(v: String) { _uiState.update { it.copy(title = v) } }
    fun onDescriptionChange(v: String) { _uiState.update { it.copy(description = v) } }
    fun onAddressChange(v: String) { _uiState.update { it.copy(address = v) } }
    fun onStartDateChange(v: String) { _uiState.update { it.copy(startDate = v) } }
    fun onStartTimeChange(v: String) { _uiState.update { it.copy(startTime = v) } }
    fun onEndDateChange(v: String) { _uiState.update { it.copy(endDate = v) } }
    fun onEndTimeChange(v: String) { _uiState.update { it.copy(endTime = v) } }
    fun onEntryFeeChange(v: String) { _uiState.update { it.copy(entryFee = v) } }
    fun onMaxTeamsChange(v: String) { _uiState.update { it.copy(maxTeams = v) } }
    fun onParticipationLimitChange(v: String) { _uiState.update { it.copy(participationLimit = v) } }
    fun onIsPublicChange(v: Boolean) { _uiState.update { it.copy(isPublic = v) } }

    fun onSave(onSuccess: () -> Unit) {
        val state = _uiState.value
        if (state.title.isBlank()) {
            _uiState.update { it.copy(errorMessage = "O nome do evento é obrigatório.") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null) }
            repository.saveEvent(state)
                .onSuccess {
                    _uiState.update { it.copy(isSaving = false, saveSuccess = true) }
                    onSuccess()
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isSaving = false, errorMessage = e.message ?: "Erro ao guardar.") }
                }
        }
    }
}
