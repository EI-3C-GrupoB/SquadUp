package com.example.squadup.core.enums

enum class TicketStatus(val value: Int) {
    CONFIRMED(0),
    PAST(1),
    EXPIRED(2),
    CANCELLED(3);

    companion object {
        fun fromInt(value: Int): TicketStatus =
            entries.firstOrNull { it.value == value } ?: CONFIRMED
    }
}
