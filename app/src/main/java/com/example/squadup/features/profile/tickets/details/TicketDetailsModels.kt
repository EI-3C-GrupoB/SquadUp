package com.example.squadup.features.profile.tickets.details

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TicketDetailsTicketRow(
    val id: Int,
    @SerialName("evento_id")
    val eventId: Int,
    @SerialName("estado")
    val status: String? = null,
    @SerialName("codigo_qr")
    val codigoQr: String? = null
)

@Serializable
data class TicketDetailsEventRow(
    val id: Int,
    @SerialName("titulo")
    val title: String,
    @SerialName("morada")
    val address: String? = null,
    @SerialName("data_inicio")
    val startDate: String? = null,
    @SerialName("modalidade_id")
    val modalityId: Int? = null,
    @SerialName("latitude")
    val latitude: Double? = null,
    @SerialName("longitude")
    val longitude: Double? = null,
    @SerialName("data_fim")
    val endDate: String? = null
)

@Serializable
data class TicketDetailsModalityRow(
    val id: Int,
    @SerialName("nome")
    val name: String
)
