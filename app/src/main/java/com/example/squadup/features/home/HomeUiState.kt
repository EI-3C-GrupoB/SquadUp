package com.example.squadup.features.home

import com.example.squadup.core.enums.EventStatus
import com.example.squadup.core.enums.SportType
import com.example.squadup.core.enums.UserRole

data class HomeUiState(
    val isLoggedIn: Boolean = false,
    val displayName: String = "",
    val role: UserRole? = null,

    // --- GLOBAL (todos os roles incluindo guest) ---
    val currentMatch: HomeMatch? = null,
    val nearbyEvents: List<HomeEvent> = emptyList(),

    // --- ORGANIZER ---
    val eventsCreated: Int = 0,
    val activeTeams: Int = 0,
    val totalRevenue: Double = 0.0,
    val myEvents: List<HomeOrganizerEvent> = emptyList(),

    // --- PLAYER ---
    val teams: List<HomeTeam> = emptyList(),
) {
    val isOrganizer get() = role == UserRole.ORGANIZER || role == UserRole.PLAYER_ORGANIZER
    val isPlayer get() = role == UserRole.PLAYER || role == UserRole.PLAYER_ORGANIZER
}

data class HomeMatch(
    val id: String,
    val title: String,
    val date: String,
    val location: String,
    val sportType: SportType
)

data class HomeEvent(
    val id: String,
    val title: String,
    val location: String,
    val distance: String,
    val intensity: Float,
    val sportType: SportType,
    val status: EventStatus
)

data class HomeOrganizerEvent(
    val id: String,
    val title: String,
    val price: String,
    val nTeams: Int,
    val dateLeft: String,
    val registeredCount: Int,
    val status: EventStatus,
    val sportType: SportType
)

data class HomeTeam(
    val id: String,
    val name: String,
    val nMembers: Int,
    val sportType: SportType,
    val badge: String?
)