package com.example.squadup.features.profile.tickets.details

data class TicketDetailsUiState(
    val title: String = "",
    val ticketType: String = "",
    val dateTime: String = "",
    val ticketNumber: String = "",
    val locationName: String = "",
    val latitude: Double? = null,
    val longitude: Double? = null,
    val codigoQr: String = "",
    val startTimeMillis: Long? = null,
    val endTimeMillis: Long? = null
)
