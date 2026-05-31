package com.example.squadup.core.enums

enum class UserRole(val value: Int) {
    PLAYER_ORGANIZER(1),
    ORGANIZER(2),
    PLAYER(3);

    companion object {
        fun fromInt(value: Int): UserRole =
            entries.firstOrNull { it.value == value } ?: PLAYER
    }
}