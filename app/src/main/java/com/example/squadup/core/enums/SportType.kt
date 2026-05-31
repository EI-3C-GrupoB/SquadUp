package com.example.squadup.core.enums

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.Color
import com.example.squadup.R

enum class SportType(
    val value: Int,
    @StringRes val labelRes: Int,
    val color: Color
) {
    SOCCER(
        value = 0,
        labelRes = R.string.sport_soccer,
        color = Color(0xFF2F9D73)
    ),
    BASKETBALL(
        value = 1,
        labelRes = R.string.sport_basketball,
        color = Color(0xFFD4611A)
    ),
    PADDLE(
        value = 2,
        labelRes = R.string.sport_paddle,
        color = Color(0xFF1A7BD4)
    ),
    VOLLEYBALL(
        value = 3,
        labelRes = R.string.sport_volleyball,
        color = Color(0xFF9B27AF)
    ),
    FUTSAL(
        value = 4,
        labelRes = R.string.sport_futsal,
        color = Color(0xFF1A6B3C)
    );

    companion object {
        fun fromInt(value: Int): SportType =
            entries.firstOrNull { it.value == value } ?: SOCCER
    }
}
