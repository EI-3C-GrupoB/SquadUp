package com.example.squadup.features.profile.tickets

import androidx.lifecycle.ViewModel
import com.example.squadup.core.enums.SportType
import com.example.squadup.core.enums.TicketStatus
import com.example.squadup.core.ui.components.TicketTab
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MyTicketsViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(MyTicketsUiState())
    val uiState: StateFlow<MyTicketsUiState> = _uiState.asStateFlow()

    init {
        loadStaticData()
    }

    private fun loadStaticData() {
        _uiState.value = MyTicketsUiState(
            upcomingTickets = listOf(
                MyTicketItem(
                    id = "1",
                    title = "Evening Tennis\nSingles",
                    dateTime = "Oct 24 • 6:30 PM",
                    location = "Riverside Sports Complex",
                    seatInfo = "Court 4 • Gate B",
                    sportType = SportType.PADDLE,
                    status = TicketStatus.CONFIRMED
                ),
                MyTicketItem(
                    id = "2",
                    title = "Elite Basketball\nLeague",
                    dateTime = "Oct 28 • 8:00 PM",
                    location = "North Arena Stadium",
                    seatInfo = "Section A12 • Row 4",
                    sportType = SportType.BASKETBALL,
                    status = TicketStatus.CONFIRMED
                )
            ),
            pastTickets = listOf(
                MyPastTicketItem(
                    id = "1",
                    title = "Summer\nBasketball\nOpen",
                    status = TicketStatus.PAST,
                    date = "Sept 15, 2023",
                    location = "Downtown Arena",
                    sportType = SportType.BASKETBALL
                ),
                MyPastTicketItem(
                    id = "2",
                    title = "Pickleball\nSocial",
                    status = TicketStatus.PAST,
                    date = "Aug 22, 2023",
                    location = "Sunset Courts",
                    sportType = SportType.PADDLE
                ),
                MyPastTicketItem(
                    id = "3",
                    title = "Charity\nSoccer\nCup",
                    status = TicketStatus.PAST,
                    date = "July 04, 2023",
                    location = "Grand Stadium",
                    sportType = SportType.SOCCER
                ),
                MyPastTicketItem(
                    id = "4",
                    title = "Morning\nVolleyball\nSession",
                    status = TicketStatus.EXPIRED,
                    date = "June 12, 2023",
                    location = "Pulse Fitness Hub",
                    sportType = SportType.VOLLEYBALL
                )
            )
        )
    }

    fun onTabSelected(tab: TicketTab) {
        _uiState.value = _uiState.value.copy(selectedTab = tab)
    }
}
