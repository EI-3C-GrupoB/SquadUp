package com.example.squadup.features.events

import com.example.squadup.core.enums.SportType

enum class EventsLocationSource {
    DEVICE,
    PROFILE,
    UNKNOWN
}

data class EventsUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val searchQuery: String = "",
    val selectedSport: SportType? = null,
    val locationSource: EventsLocationSource = EventsLocationSource.UNKNOWN,
    val featuredEvent: FeaturedEventItem? = null,
    val upcomingEvents: List<UpcomingEventItem> = emptyList(),
    val browseEvents: List<BrowseEventItem> = emptyList()
) {
    val filteredFeaturedEvent: FeaturedEventItem?
        get() = featuredEvent
            ?.takeIf { selectedSport == null || it.sportType == selectedSport }
            ?.takeIf {
                searchQuery.isBlank() ||
                        it.title.contains(searchQuery, ignoreCase = true) ||
                        it.venue.contains(searchQuery, ignoreCase = true)
            }

    val filteredBrowseEvents: List<BrowseEventItem>
        get() = browseEvents
            .filter { selectedSport == null || it.sportType == selectedSport }
            .filter {
                searchQuery.isBlank() ||
                        it.title.contains(searchQuery, ignoreCase = true) ||
                        it.venue.contains(searchQuery, ignoreCase = true)
            }

    val filteredUpcomingEvents: List<UpcomingEventItem>
        get() = upcomingEvents
            .filter { selectedSport == null || it.sportType == selectedSport }
            .filter {
                searchQuery.isBlank() ||
                        it.title.contains(searchQuery, ignoreCase = true)
            }
}

data class FeaturedEventItem(
    val id: String,
    val seriesName: String,
    val title: String,
    val dateTime: String,
    val venue: String,
    val sportType: SportType,
    val distance: String = "",
    val distanceKm: Double? = null,
    val imageUrl: String? = null
)

data class UpcomingEventItem(
    val id: String,
    val month: String,
    val day: String,
    val title: String,
    val sportType: SportType,
    val time: String,
    val distance: String = "",
    val distanceKm: Double? = null,
    val imageUrl: String? = null
)

data class BrowseEventItem(
    val id: String,
    val title: String,
    val price: String,
    val dateTime: String,
    val venue: String,
    val sportType: SportType,
    val entryType: String = "OPEN MATCH",
    val requiresTeam: Boolean = true,
    val spotsLeft: Int = 0,
    val totalSpots: Int = 0,
    val registeredTeams: Int = 0,
    val distance: String = "",
    val distanceKm: Double? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val imageUrl: String? = null
)
