package com.example.squadup.features.events

import androidx.lifecycle.ViewModel
import com.example.squadup.core.enums.SportType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class EventsViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(EventsUiState())
    val uiState: StateFlow<EventsUiState> = _uiState.asStateFlow()

    init {
        loadStaticData()
    }

    private fun loadStaticData() {
        _uiState.value = EventsUiState(
            featuredEvent = FeaturedEventItem(
                id = "1",
                seriesName = "CHAMPIONSHIP SERIES",
                title = "Finals: Tigers vs. Knights",
                dateTime = "Oct 24, 7:30 PM",
                venue = "Velocity Arena",
                sportType = SportType.SOCCER
            ),
            upcomingEvents = listOf(
                UpcomingEventItem("1", "APR", "9",  "The Mavericks vs Red Eagles", SportType.SOCCER,      "18:00 PM"),
                UpcomingEventItem("2", "APR", "12", "Viper Strike Open",           SportType.PADDLE,      "2:00 PM"),
                UpcomingEventItem("3", "APR", "19", "Sunday Night Lights",         SportType.BASKETBALL,  "8:00 PM")
            ),
            browseEvents = listOf(
                BrowseEventItem("1", "Metropolis United vs. Skyhawks FC", "\$45",  "Tomorrow • 6:00 PM",  "City Center Stadium", SportType.SOCCER,     "OPEN MATCH",       requiresTeam = true,  spotsLeft = 4,  totalSpots = 22),
                BrowseEventItem("2", "Youth Elite Invitations",           "Free",  "Oct 30 • 10:00 AM",  "North Park Track",    SportType.BASKETBALL, "TOURNAMENT ENTRY", requiresTeam = false, spotsLeft = 18, totalSpots = 32),
                BrowseEventItem("3", "Sand Pro Tour: Beach Open",         "\$20",  "Nov 02 • 12:00 PM",  "Sunnyside Beach",     SportType.VOLLEYBALL, "TOURNAMENT ENTRY", requiresTeam = false, spotsLeft = 6,  totalSpots = 40),
                BrowseEventItem("4", "Futsal City Cup",                   "\$30",  "Nov 08 • 7:00 PM",   "Indoor Arena B",      SportType.FUTSAL,     "OPEN MATCH",       requiresTeam = true,  spotsLeft = 2,  totalSpots = 16),
                BrowseEventItem("5", "Padel Masters Open",                "\$50",  "Nov 14 • 9:00 AM",   "Westside Club",       SportType.PADDLE,     "TOURNAMENT ENTRY", requiresTeam = true,  spotsLeft = 10, totalSpots = 24)
            )
        )
    }

    fun onSearchQueryChange(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
    }

    fun onSportFilterChange(sport: SportType?) {
        _uiState.value = _uiState.value.copy(selectedSport = sport)
    }
}
