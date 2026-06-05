package com.example.squadup.features.profile.tickets.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TicketDetailsViewModel : ViewModel() {

    private val repository = TicketDetailsRepository()
    private val _uiState = MutableStateFlow(TicketDetailsUiState())
    val uiState: StateFlow<TicketDetailsUiState> = _uiState.asStateFlow()

    fun loadTicket(ticketId: String) {
        if (ticketId.isBlank()) return

        viewModelScope.launch {
            repository.getTicketDetails(ticketId).onSuccess { ticket ->
                _uiState.value = ticket
            }
        }
    }
}
