package com.example.squadup.core.enums

enum class EventStatus(val value: Int) {
    DRAFT(0),
    REGISTRATION_OPEN(1),
    REGISTRATION_CLOSED(2),
    ONGOING(3),
    FINISHED(4),
    CANCELLED(5);

    companion object {
        fun fromInt(value: Int): EventStatus =
            entries.firstOrNull { it.value == value } ?: DRAFT
    }
}