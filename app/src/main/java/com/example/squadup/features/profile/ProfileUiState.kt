package com.example.squadup.features.profile

import com.example.squadup.core.enums.PlayStyle
import com.example.squadup.core.enums.UserRole

data class ProfileUiState(
    val isAdmin: Boolean = false,
    val role: UserRole? = null,
    val displayName: String = "",
    val matchesPlayed: Int = 0,
    val wins: Int = 0,
    val goals: Int = 0,
    val teams: Int = 0,
    val playStyle: PlayStyle? = null
) {
    val isPlayer get() = role == UserRole.PLAYER || role == UserRole.PLAYER_ORGANIZER
    val isOrganizer get() = role == UserRole.ORGANIZER || role == UserRole.PLAYER_ORGANIZER
}
