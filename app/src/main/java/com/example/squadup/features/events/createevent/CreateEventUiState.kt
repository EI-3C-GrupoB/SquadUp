package com.example.squadup.features.events.createevent

import com.example.squadup.core.enums.EventFormat
import com.example.squadup.core.enums.RecurrenceType
import com.example.squadup.core.enums.SportType
import com.example.squadup.core.enums.UserRole

enum class CreateEventStep { BASIC_INFO, FORMAT_PLAYERS, LOCATION_TIME, REVIEW }

data class NotifyTeamItem(
    val id: String,
    val name: String,
    val nMembers: Int,
    val sportType: SportType
)

data class CreateEventUiState(
    val currentStep: CreateEventStep = CreateEventStep.BASIC_INFO,

    // Step 1 — Basic Info
    val eventName: String = "",
    val isPublic: Boolean = true,
    val selectedSport: SportType? = null,

    // Step 2 — Format & Players
    val eventFormat: EventFormat = EventFormat.SINGLE_MATCH,
    val format: String = "5v5 Full Court",
    val maxTeams: Int = 8,
    val generalRules: String = "",
    val isPublicEvent: Boolean = true,
    val entryFee: String = "",
    val allowTeams: Boolean = true,
    val allowFreeAgents: Boolean = false,

    // Step 3 — Location & Time
    val venue: String = "",
    val eventDate: String = "",
    val startTime: String = "",
    val endTime: String = "",
    val isRecurring: Boolean = false,
    val recurrenceType: RecurrenceType = RecurrenceType.WEEKLY,
    val recurringDays: Set<Int> = setOf(2, 4), // 0=Sun … 6=Sat
    val showRecurrenceDialog: Boolean = false,

    // User context (loaded from session)
    val userRole: UserRole? = null,
    val userTeams: List<NotifyTeamItem> = emptyList(),

    // Review
    val teamsToNotify: Set<String> = emptySet()
) {
    val isPlayerOrganizer get() = userRole == UserRole.PLAYER_ORGANIZER

    val progressStep: Int get() = when (currentStep) {
        CreateEventStep.BASIC_INFO      -> 1
        CreateEventStep.FORMAT_PLAYERS  -> 2
        CreateEventStep.LOCATION_TIME   -> 3
        CreateEventStep.REVIEW          -> 3
    }
}
