package com.example.squadup.core.enums

enum class UserRole(val value: Int) {
    PLAYER(1),
    ORGANIZER(2),
    PLAYER_ORGANIZER(3);

    companion object {
        fun fromInt(value: Int?): UserRole =
            entries.firstOrNull { it.value == value } ?: PLAYER
    }
}