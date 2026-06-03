package com.example.squadup.features.events

import com.example.squadup.core.enums.SportType

data class EventsUiState(
    val searchQuery: String = "",
    val selectedSport: SportType? = null,
    val featuredEvent: FeaturedEventItem? = null,
    val upcomingEvents: List<UpcomingEventItem> = emptyList(),
    val browseEvents: List<BrowseEventItem> = emptyList()
) {
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
}

data class FeaturedEventItem(
    val id: String,
    val seriesName: String,
    val title: String,
    val dateTime: String,
    val venue: String,
    val sportType: SportType
)

data class UpcomingEventItem(
    val id: String,
    val month: String,
    val day: String,
    val title: String,
    val sportType: SportType,
    val time: String
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
    val totalSpots: Int = 0
)
