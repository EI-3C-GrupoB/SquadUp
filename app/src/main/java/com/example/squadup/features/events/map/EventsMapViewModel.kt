package com.example.squadup.features.events.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.squadup.core.enums.SportType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EventsMapViewModel : ViewModel() {

    private val repository = EventsMapRepository()
    private val _uiState = MutableStateFlow(EventsMapUiState(isLoading = true))
    val uiState: StateFlow<EventsMapUiState> = _uiState.asStateFlow()

    init {
        loadPins()
    }

    fun loadPins() {
        _uiState.value = _uiState.value.copy(isLoading = true)

        viewModelScope.launch {
            repository.getMapPins()
                .onSuccess { mapData ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        pins = mapData.pins,
                        cameraBounds = mapData.cameraBounds
                    )
                }
                .onFailure {
                    _uiState.value = _uiState.value.copy(isLoading = false)
                }
        }
    }

    fun onSportFilterChange(sportType: SportType?) {
        _uiState.update { it.copy(selectedSport = sportType) }
    }

    fun onPinSelected(eventId: String?) {
        _uiState.update { it.copy(selectedPinId = eventId) }
    }

    fun onUserLocationReady(lat: Double, lon: Double) {
        _uiState.update { it.copy(userLatitude = lat, userLongitude = lon) }
    }

    fun requestCenterOnUser(lat: Double, lon: Double) {
        _uiState.update { it.copy(
            userLatitude = lat,
            userLongitude = lon,
            centerOnUserRequest = it.centerOnUserRequest + 1
        ) }
    }

    fun onRadiusChange(radius: Float) {
        _uiState.update { it.copy(userRadiusKm = radius) }
    }
}
