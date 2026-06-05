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
    private val _uiState = MutableStateFlow(EventsUiState())
    val uiState: StateFlow<EventsUiState> = _uiState.asStateFlow()

    init {
        loadEvents()
    }

    private fun loadEvents() {
        viewModelScope.launch {
            repository.getEvents().onSuccess { eventsData ->
                _uiState.value = _uiState.value.copy(
                    featuredEvent = eventsData.featuredEvent,
                    upcomingEvents = eventsData.upcomingEvents,
                    browseEvents = eventsData.browseEvents
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
