package com.example.squadup.features.profile.tickets

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MyTicketsUserRow(
    val id: Int,
    @SerialName("auth_user_id")
    val authUserId: String? = null
)

@Serializable
data class MyTicketsTicketRow(
    val id: Int,
    @SerialName("user_id")
    val userId: Int,
    @SerialName("evento_id")
    val eventId: Int,
    @SerialName("data_emissao")
    val issuedAt: String? = null,
    @SerialName("estado")
    val status: String? = null
)

@Serializable
data class MyTicketsEventRow(
    val id: Int,
    @SerialName("titulo")
    val title: String,
    @SerialName("morada")
    val address: String? = null,
    @SerialName("data_inicio")
    val startDate: String? = null,
    @SerialName("data_fim")
    val endDate: String? = null,
    @SerialName("modalidade_id")
    val modalityId: Int? = null
)

@Serializable
data class MyTicketsModalityRow(
    val id: Int,
    @SerialName("nome")
    val name: String
)
