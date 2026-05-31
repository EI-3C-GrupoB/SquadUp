package com.example.squadup.core.enums

import androidx.annotation.StringRes
import com.example.squadup.R

enum class PlayStyle(
    val level: Int,
    @StringRes val labelRes: Int,
    @StringRes val descriptionRes: Int
) {
    LOW(
        level = 1,
        labelRes = R.string.play_style_low_label,
        descriptionRes = R.string.play_style_low_desc
    ),
    MEDIUM(
        level = 2,
        labelRes = R.string.play_style_medium_label,
        descriptionRes = R.string.play_style_medium_desc
    ),
    HIGH(
        level = 3,
        labelRes = R.string.play_style_high_label,
        descriptionRes = R.string.play_style_high_desc
    );

    val progress: Float get() = level / 3f

    companion object {
        fun fromLevel(level: Int): PlayStyle =
            entries.firstOrNull { it.level == level } ?: LOW
    }
}
