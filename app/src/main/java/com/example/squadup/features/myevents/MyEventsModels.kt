package com.example.squadup.features.profile.myevents

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MyEventsUserRow(
    val id: Int,
    @SerialName("auth_user_id")
    val authUserId: String? = null
)

@Serializable
data class MyEventsEventRow(
    val id: Int,
    @SerialName("titulo")
    val title: String,
    @SerialName("morada")
    val address: String? = null,
    @SerialName("data_inicio")
    val startDate: String? = null,
    @SerialName("estado_evento")
    val status: String? = null,
    @SerialName("criador_id")
    val creatorId: Int? = null,
    @SerialName("modalidade_id")
    val modalityId: Int? = null
)

@Serializable
data class MyEventsModalityRow(
    val id: Int,
    @SerialName("nome")
    val name: String
)

@Serializable
data class MyEventsRegistrationRow(
    @SerialName("equipa_id")
    val teamId: Int? = null,
    @SerialName("evento_id")
    val eventId: Int? = null,
    @SerialName("user_id")
    val userId: Int? = null
)

@Serializable
data class MyEventsGameRow(
    @SerialName("estado_jogo")
    val status: String? = null,
    @SerialName("evento_id")
    val eventId: Int? = null
)
