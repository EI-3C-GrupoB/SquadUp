package com.example.squadup.features.profile.myevents

import androidx.lifecycle.ViewModel
import com.example.squadup.core.enums.EventStatus
import com.example.squadup.core.enums.SportType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MyEventsViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(MyEventsUiState())
    val uiState: StateFlow<MyEventsUiState> = _uiState.asStateFlow()

    init {
        loadStaticData()
    }

    private fun loadStaticData() {
        _uiState.value = MyEventsUiState(
            events = listOf(
                MyEventItem(
                    id = "1",
                    title = "Summer Slam Finals",
                    location = "Metro Arena",
                    date = "Aug 12",
                    teamsCount = 24,
                    playersCount = 120,
                    status = EventStatus.REGISTRATION_OPEN,
                    sportType = SportType.BASKETBALL,
                    registeredCount = 22
                ),
                MyEventItem(
                    id = "2",
                    title = "City League Soccer",
                    location = "West Field",
                    date = "Jul 28",
                    teamsCount = 12,
                    playersCount = 84,
                    status = EventStatus.ONGOING,
                    sportType = SportType.SOCCER,
                    matchesInProgress = 4
                ),
                MyEventItem(
                    id = "3",
                    title = "Spring Tennis Open",
                    location = "Riverside Courts",
                    date = "June 15",
                    teamsCount = 8,
                    playersCount = 32,
                    status = EventStatus.FINISHED,
                    sportType = SportType.PADDLE
                )
            )
        )
    }

    fun onSearchQueryChange(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
    }

    fun onFilterChange(filter: MyEventsFilter) {
        _uiState.value = _uiState.value.copy(selectedFilter = filter)
    }
}
