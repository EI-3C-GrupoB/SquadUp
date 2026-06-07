package com.example.squadup.features.events

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.squadup.core.enums.SportType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class EventsViewModel : ViewModel() {

    private val repository = EventsRepository()

    private val _uiState = MutableStateFlow(
        EventsUiState(isLoading = true)
    )
    val uiState: StateFlow<EventsUiState> = _uiState.asStateFlow()

    init {
        observeEvents()
    }

    private fun observeEvents() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = null
            )

            repository.getEventsRealtime()
                .collect { eventsData ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        featuredEvent = eventsData.featuredEvent,
                        upcomingEvents = eventsData.upcomingEvents,
                        browseEvents = eventsData.browseEvents,
                        errorMessage = null
                    )
                }
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
    }

    fun onSportFilterChange(sport: SportType?) {
        _uiState.value = _uiState.value.copy(selectedSport = sport)
    }
}
