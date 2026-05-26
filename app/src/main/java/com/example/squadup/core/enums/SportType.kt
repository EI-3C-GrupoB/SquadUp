package com.example.squadup.core.enums

enum class SportType(val value: Int) {
    SOCCER(0),
    BASKETBALL(1),
    PADDLE(2),
    VOLLEYBALL(3),
    FUTSAL(4);

    companion object {
        fun fromInt(value: Int): SportType =
            entries.firstOrNull { it.value == value } ?: SOCCER
    }
}