package com.example.squadup.features.profile.tickets

import com.example.squadup.core.enums.SportType
import com.example.squadup.core.enums.TicketStatus
import com.example.squadup.core.ui.components.TicketTab

data class MyTicketsUiState(
    val selectedTab: TicketTab = TicketTab.Upcoming,
    val upcomingTickets: List<MyTicketItem> = emptyList(),
    val pastTickets: List<MyPastTicketItem> = emptyList()
)

data class MyTicketItem(
    val id: String,
    val title: String,
    val dateTime: String,
    val location: String,
    val sportType: SportType,
    val status: TicketStatus = TicketStatus.CONFIRMED
)

data class MyPastTicketItem(
    val id: String,
    val title: String,
    val status: TicketStatus,
    val date: String,
    val location: String,
    val sportType: SportType
)
