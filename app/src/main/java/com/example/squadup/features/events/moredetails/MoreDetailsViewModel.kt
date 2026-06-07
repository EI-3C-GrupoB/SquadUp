package com.example.squadup.features.events.moredetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MoreDetailsViewModel : ViewModel() {

    private val repository = MoreDetailsRepository()
    private var eventDetailsJob: Job? = null

    private val _uiState = MutableStateFlow(
        MoreDetailsUiState(
            isLoading = true
        )
    )

    val uiState: StateFlow<MoreDetailsUiState> = _uiState.asStateFlow()

    fun loadEvent(eventId: String) {
        if (eventId.isBlank()) {
            _uiState.value = MoreDetailsUiState(
                isLoading = false,
                errorMessage = "Evento inválido."
            )
            return
        }

        eventDetailsJob?.cancel()

        eventDetailsJob = viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = null
            )

            repository.observeEventDetails(eventId)
                .collect { details ->
                    _uiState.value = details.copy(
                        isLoading = false
                    )
                }
        }
    }

    override fun onCleared() {
        eventDetailsJob?.cancel()
        super.onCleared()
    }
}
