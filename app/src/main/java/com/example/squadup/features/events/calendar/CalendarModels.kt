package com.example.squadup.features.events.calendar

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CalendarGameRow(
    val id: Int,
    @SerialName("data_hora_prevista")
    val scheduledAt: String? = null,
    @SerialName("morada")
    val address: String? = null,
    @SerialName("evento_id")
    val eventId: Int? = null
)

@Serializable
data class CalendarEventRow(
    val id: Int,
    @SerialName("titulo")
    val title: String
)

@Serializable
data class CalendarGameTeamRow(
    @SerialName("equipa_id")
    val teamId: Int,
    @SerialName("jogo_id")
    val gameId: Int
)

@Serializable
data class CalendarTeamRow(
    val id: Int,
    @SerialName("nome")
    val name: String
)

@Serializable
data class CalendarUserRow(
    val id: Int,
    @SerialName("auth_user_id")
    val authUserId: String? = null
)

@Serializable
data class CalendarBilheteRow(
    val id: Int,
    @SerialName("user_id")
    val userId: Int,
    @SerialName("evento_id")
    val eventId: Int
)
