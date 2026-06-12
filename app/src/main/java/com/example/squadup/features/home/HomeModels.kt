package com.example.squadup.features.home

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class HomeUserRow(
    val id: Int,
    @SerialName("nome")
    val name: String = "",
    @SerialName("auth_user_id")
    val authUserId: String? = null,
    @SerialName("tipo_conta")
    val accountType: Int? = null
)

@Serializable
data class HomeEventRow(
    val id: Int,
    @SerialName("titulo")
    val title: String,
    @SerialName("morada")
    val address: String? = null,
    @SerialName("data_inicio")
    val startDate: String? = null,
    @SerialName("estado_evento")
    val status: String? = null,
    @SerialName("preco")
    val price: Double? = null,
    @SerialName("taxa_inscricao")
    val entryFee: Double? = null,
    @SerialName("criador_id")
    val creatorId: Int? = null,
    @SerialName("modalidade_id")
    val modalityId: Int? = null
)

@Serializable
data class HomeModalityRow(
    val id: Int,
    @SerialName("nome")
    val name: String
)

@Serializable
data class HomeTeamRow(
    val id: Int,
    @SerialName("nome")
    val name: String,
    @SerialName("user_id")
    val ownerId: Int? = null
)

@Serializable
data class HomeRegistrationRow(
    val id: Int,
    @SerialName("equipa_id")
    val teamId: Int? = null,
    @SerialName("evento_id")
    val eventId: Int? = null,
    @SerialName("user_id")
    val userId: Int? = null
)

@Serializable
data class HomeGameRow(
    val id: Int,
    @SerialName("data_hora_prevista")
    val scheduledAt: String? = null,
    @SerialName("estado_jogo")
    val status: String? = null,
    @SerialName("morada")
    val address: String? = null,
    @SerialName("evento_id")
    val eventId: Int? = null
)

@Serializable
data class HomeGameTeamRow(
    @SerialName("equipa_id")
    val teamId: Int,
    @SerialName("jogo_id")
    val gameId: Int
)

@Serializable
data class HomeUserPhotoRow(
    val id: Int,
    @SerialName("foto_url")
    val photoUrl: String? = null
)

@Serializable
data class HomeNearbyEventRow(
    val id: Int,
    @SerialName("titulo")
    val title: String,
    @SerialName("morada")
    val address: String? = null,
    @SerialName("data_inicio")
    val startDate: String? = null,
    @SerialName("estado_evento")
    val status: String? = null,
    @SerialName("modalidade_nome")
    val modalityName: String? = null,
    @SerialName("distancia_km")
    val distanceKm: Double? = null,
    @SerialName("criador_id")
    val creatorId: Int? = null
)
