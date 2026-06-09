package com.example.squadup.features.events.map

import com.example.squadup.core.enums.SportType

data class EventsMapUiState(
    val isLoading: Boolean = false,
    val selectedSport: SportType? = null,
    val selectedPinId: String? = null,
    val pins: List<EventsMapPin> = emptyList(),
    val cameraBounds: EventsMapCameraBounds? = null,
    val userLatitude: Double? = null,
    val userLongitude: Double? = null,
    val userRadiusKm: Float = 5f,
    val centerOnUserRequest: Int = 0
) {
    val visiblePins: List<EventsMapPin>
        get() = pins.filter { selectedSport == null || it.sportType == selectedSport }

    val selectedPin: EventsMapPin?
        get() = pins.firstOrNull { it.eventId == selectedPinId }

    val availableCount: Int
        get() = pins.count { it.status == EventsMapPinStatus.AVAILABLE }

    val fullCount: Int
        get() = pins.count { it.status == EventsMapPinStatus.FULL }

    val privateCount: Int
        get() = pins.count { it.status == EventsMapPinStatus.PRIVATE }

    val cancelledCount: Int
        get() = pins.count { it.status == EventsMapPinStatus.CANCELLED || it.status == EventsMapPinStatus.FINISHED }
}
