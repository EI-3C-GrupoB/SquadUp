package com.example.squadup.features.events.moredetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MoreDetailsViewModel : ViewModel() {

    private val repository = MoreDetailsRepository()
    private val _uiState = MutableStateFlow(MoreDetailsUiState())
    val uiState: StateFlow<MoreDetailsUiState> = _uiState.asStateFlow()

    fun loadEvent(eventId: String) {
        if (eventId.isBlank()) return

        viewModelScope.launch {
            repository.getEventDetails(eventId).onSuccess { details ->
                _uiState.value = details
            }
        }
    }
}
