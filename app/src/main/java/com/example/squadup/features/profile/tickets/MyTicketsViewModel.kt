package com.example.squadup.features.profile.tickets

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.squadup.core.ui.components.TicketTab
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MyTicketsViewModel : ViewModel() {

    private val repository = MyTicketsRepository()
    private val _uiState = MutableStateFlow(MyTicketsUiState())
    val uiState: StateFlow<MyTicketsUiState> = _uiState.asStateFlow()

    init {
        loadTickets()
    }

    private fun loadTickets() {
        viewModelScope.launch {
            repository.getMyTickets().onSuccess { tickets ->
                _uiState.value = tickets.copy(selectedTab = _uiState.value.selectedTab)
            }
        }
    }

    fun onTabSelected(tab: TicketTab) {
        _uiState.value = _uiState.value.copy(selectedTab = tab)
    }
}
