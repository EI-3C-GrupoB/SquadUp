package com.example.squadup.core.enums

import androidx.annotation.StringRes
import com.example.squadup.R

enum class EventFormat(
    @StringRes val labelRes: Int,
    @StringRes val descRes: Int
) {
    SINGLE_MATCH(R.string.createEvent_format_single_match, R.string.createEvent_format_single_match_desc),
    LEAGUE(R.string.createEvent_format_league, R.string.createEvent_format_league_desc),
    KNOCKOUT(R.string.createEvent_format_knockout, R.string.createEvent_format_knockout_desc),
    GROUP_KNOCKOUT(R.string.createEvent_format_group_knockout, R.string.createEvent_format_group_knockout_desc),
    FREE(R.string.createEvent_format_free, R.string.createEvent_format_free_desc)
}
