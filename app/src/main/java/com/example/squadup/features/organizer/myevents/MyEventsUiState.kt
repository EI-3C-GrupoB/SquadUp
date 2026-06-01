package com.example.squadup.features.organizer.myevents

import com.example.squadup.core.enums.EventStatus
import com.example.squadup.core.enums.SportType

enum class MyEventsFilter { All, Active, Completed }

data class MyEventsUiState(
    val searchQuery: String = "",
    val selectedFilter: MyEventsFilter = MyEventsFilter.All,
    val events: List<MyEventItem> = emptyList()
) {
    val filteredEvents: List<MyEventItem>
        get() = events
            .filter { searchQuery.isBlank() || it.title.contains(searchQuery, ignoreCase = true) }
            .filter {
                when (selectedFilter) {
                    MyEventsFilter.All       -> true
                    MyEventsFilter.Active    -> it.status == EventStatus.ONGOING ||
                                               it.status == EventStatus.REGISTRATION_OPEN ||
                                               it.status == EventStatus.REGISTRATION_CLOSED
                    MyEventsFilter.Completed -> it.status == EventStatus.FINISHED ||
                                               it.status == EventStatus.CANCELLED
                }
            }
}

data class MyEventItem(
    val id: String,
    val title: String,
    val location: String,
    val date: String,
    val teamsCount: Int,
    val playersCount: Int,
    val status: EventStatus,
    val sportType: SportType,
    val registeredCount: Int = 0,
    val matchesInProgress: Int = 0
)
