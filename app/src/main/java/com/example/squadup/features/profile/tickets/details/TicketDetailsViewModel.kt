package com.example.squadup.features.profile.tickets.details

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class TicketDetailsViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(TicketDetailsUiState())
    val uiState: StateFlow<TicketDetailsUiState> = _uiState.asStateFlow()

    init {
        loadStaticData()
    }

    private fun loadStaticData() {
        _uiState.value = TicketDetailsUiState(
            title = "Evening Tennis Singles",
            ticketType = "Standard Entry • Participant",
            dateTime = "Oct 24, 2023 • 6:30 PM",
            locationName = "Riverside Sports Complex,",
            locationDetail = "Court #4"
        )
    }
}
