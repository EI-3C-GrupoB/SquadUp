package com.example.squadup.features.home

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.example.squadup.core.enums.EventStatus
import com.example.squadup.core.enums.SportType
import com.example.squadup.core.enums.UserRole

class HomeViewModel : ViewModel() {

    private val staticCurrentMatch = HomeMatch(
        id = "1",
        title = "City Finals: Red Dragons vs. Blue Hawks",
        date = "Today, 18:45",
        location = "Central Arena",
        sportType = SportType.BASKETBALL
    )

    private val staticNearbyEvents = listOf(
        HomeEvent(
            id = "1",
            title = "Open Tennis Doubles",
            location = "Westside Club",
            distance = "2.4km",
            intensity = 0.65f,
            sportType = SportType.PADDLE,
            status = EventStatus.REGISTRATION_OPEN
        ),
        HomeEvent(
            id = "2",
            title = "3x3 Street Basketball",
            location = "Central Park",
            distance = "1.1km",
            intensity = 0.80f,
            sportType = SportType.BASKETBALL,
            status = EventStatus.REGISTRATION_OPEN
        )
    )

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadStaticData()
    }

    private fun loadStaticData() {
        _uiState.value = buildPlayerOrganizerState()
    }

    private fun buildGuestState() = HomeUiState(
        isLoggedIn = false,
        currentMatch = staticCurrentMatch,
        nearbyEvents = staticNearbyEvents
    )

    private fun buildOrganizerState() = HomeUiState(
        isLoggedIn = true,
        displayName = "Diogo",
        role = UserRole.ORGANIZER,
        eventsCreated = 24,
        activeTeams = 112,
        totalRevenue = 12450.0,
        currentMatch = staticCurrentMatch,
        nearbyEvents = staticNearbyEvents,
        myEvents = listOf(
            HomeOrganizerEvent(
                id = "1",
                title = "City League Season 5",
                price = "$450.00",
                nTeams = 16,
                dateLeft = "4 Weeks Left",
                registeredCount = 109,
                status = EventStatus.ONGOING,
                sportType = SportType.BASKETBALL
            ),
            HomeOrganizerEvent(
                id = "2",
                title = "Midnight Soccer Cup",
                price = "$1,200.00",
                nTeams = 32,
                dateLeft = "Starts Oct 20",
                registeredCount = 8,
                status = EventStatus.REGISTRATION_OPEN,
                sportType = SportType.SOCCER
            )
        )
    )

    private fun buildPlayerState() = HomeUiState(
        isLoggedIn = true,
        displayName = "Diogo",
        role = UserRole.PLAYER,
        currentMatch = staticCurrentMatch,
        nearbyEvents = staticNearbyEvents,
        teams = listOf(
            HomeTeam(
                id = "1",
                name = "The Mavericks",
                nMembers = 12,
                sportType = SportType.SOCCER,
                badge = "LEADER"
            ),
            HomeTeam(
                id = "2",
                name = "Midnight Hoop",
                nMembers = 8,
                sportType = SportType.BASKETBALL,
                badge = null
            )
        )
    )

    private fun buildPlayerOrganizerState() = buildOrganizerState().copy(
        role = UserRole.PLAYER_ORGANIZER,
        teams = buildPlayerState().teams
    )
}