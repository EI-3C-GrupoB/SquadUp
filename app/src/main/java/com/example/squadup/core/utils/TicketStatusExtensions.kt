package com.example.squadup.core.utils

import android.content.Context
import androidx.compose.ui.graphics.Color
import com.example.squadup.R
import com.example.squadup.core.enums.TicketStatus
import com.example.squadup.core.ui.theme.SquadOrange
import com.example.squadup.core.ui.theme.SquadOrangeLight

fun TicketStatus.toLabel(context: Context): String = when (this) {
    TicketStatus.CONFIRMED -> context.getString(R.string.ticket_status_confirmed)
    TicketStatus.PAST      -> context.getString(R.string.ticket_status_past)
    TicketStatus.EXPIRED   -> context.getString(R.string.ticket_status_expired)
    TicketStatus.CANCELLED -> context.getString(R.string.ticket_status_cancelled)
}

fun TicketStatus.toLabelColor(): Color = when (this) {
    TicketStatus.CONFIRMED -> Color(0xFF00897B)
    TicketStatus.PAST      -> Color(0xFF5F5F5F)
    TicketStatus.EXPIRED   -> Color(0xFF777777)
    TicketStatus.CANCELLED -> Color(0xFFD32F2F)
}

fun TicketStatus.toLabelBackground(): Color = when (this) {
    TicketStatus.CONFIRMED -> Color(0xFFE0F2F1)
    TicketStatus.PAST      -> Color(0xFFF0E7E7)
    TicketStatus.EXPIRED   -> Color(0xFFEDEDED)
    TicketStatus.CANCELLED -> Color(0xFFFFEBEB)
}
