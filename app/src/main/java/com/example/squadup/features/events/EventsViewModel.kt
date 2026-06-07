package com.example.squadup.features.events

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.squadup.core.enums.SportType
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class EventsViewModel : ViewModel() {

    private val repository = EventsRepository()
    private var eventsObservationJob: Job? = null

    private val _uiState = MutableStateFlow(
        EventsUiState(
            isLoading = true,
            errorMessage = null
        )
    )
    val uiState: StateFlow<EventsUiState> = _uiState.asStateFlow()

    fun observeEventsFromDeviceLocation(
        latitude: Double,
        longitude: Double
    ) {
        eventsObservationJob?.cancel()

        eventsObservationJob = viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = null
            )

            repository.getEventsRealtime(
                latitude = latitude,
                longitude = longitude
            ).collect { eventsData ->
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

    fun observeEventsFromProfileLocation() {
        eventsObservationJob?.cancel()

        eventsObservationJob = viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = null
            )

            repository.getEventsRealtimeFromProfileLocation()
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

    fun onLocationUnavailable() {
        observeEventsFromProfileLocation()
    }

    fun onSearchQueryChange(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
    }

    fun onSportFilterChange(sport: SportType?) {
        _uiState.value = _uiState.value.copy(selectedSport = sport)
    }
}
