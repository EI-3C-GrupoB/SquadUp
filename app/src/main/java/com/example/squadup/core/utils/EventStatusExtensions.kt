package com.example.squadup.core.utils

import android.content.Context
import androidx.compose.ui.graphics.Color
import com.example.squadup.R
import com.example.squadup.core.enums.EventStatus
import com.example.squadup.core.ui.theme.SquadOrange
import com.example.squadup.core.ui.theme.SquadOrangeLight

fun EventStatus.toLabel(context: Context): String = when (this) {
    EventStatus.DRAFT -> context.getString(R.string.event_status_draft)
    EventStatus.REGISTRATION_OPEN -> context.getString(R.string.event_status_registration_open)
    EventStatus.REGISTRATION_CLOSED -> context.getString(R.string.event_status_registration_closed)
    EventStatus.ONGOING -> context.getString(R.string.event_status_ongoing)
    EventStatus.FINISHED -> context.getString(R.string.event_status_finished)
    EventStatus.CANCELLED -> context.getString(R.string.event_status_cancelled)
}

fun EventStatus.toLabelColor(): Color = when (this) {
    EventStatus.ONGOING -> SquadOrange
    EventStatus.REGISTRATION_OPEN -> Color(0xFF1A7BD4)
    EventStatus.FINISHED -> Color(0xFF5F5F5F)
    EventStatus.CANCELLED -> Color(0xFFD32F2F)
    EventStatus.DRAFT -> Color(0xFF5F5F5F)
    EventStatus.REGISTRATION_CLOSED -> Color(0xFF5F5F5F)
}

fun EventStatus.toLabelBackground(): Color = when (this) {
    EventStatus.ONGOING -> SquadOrangeLight
    EventStatus.REGISTRATION_OPEN -> Color(0xFFDCEEFB)
    EventStatus.FINISHED -> Color(0xFFEDEDED)
    EventStatus.CANCELLED -> Color(0xFFFFEBEB)
    EventStatus.DRAFT -> Color(0xFFEDEDED)
    EventStatus.REGISTRATION_CLOSED -> Color(0xFFEDEDED)
}