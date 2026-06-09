package com.example.squadup.features.events

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EventsNearbyEventRow(
    val id: Int,
    @SerialName("titulo")
    val title: String,
    @SerialName("descricao")
    val description: String? = null,
    @SerialName("imagem_url")
    val imageUrl: String? = null,
    @SerialName("limite_participacoes")
    val participationLimit: Int? = null,
    @SerialName("max_equipas")
    val maxTeams: Int? = null,
    @SerialName("taxa_inscricao")
    val entryFee: Double? = null,
    @SerialName("preco")
    val price: Double? = null,
    @SerialName("moeda")
    val currency: String? = null,
    @SerialName("longitude")
    val longitude: Double? = null,
    @SerialName("latitude")
    val latitude: Double? = null,
    @SerialName("morada")
    val address: String? = null,
    @SerialName("is_private")
    val isPrivate: Boolean? = false,
    @SerialName("codigo_acesso")
    val accessCode: String? = null,
    @SerialName("data_inicio")
    val startDate: String? = null,
    @SerialName("data_fim")
    val endDate: String? = null,
    @SerialName("data_ini_inscricao")
    val registrationStartDate: String? = null,
    @SerialName("data_fim_inscricao")
    val registrationEndDate: String? = null,
    @SerialName("estado_evento")
    val eventStatus: String? = null,
    @SerialName("regras")
    val rules: String? = null,
    @SerialName("criador_id")
    val creatorId: Int? = null,
    @SerialName("modalidade_id")
    val modalityId: Int? = null,
    @SerialName("modalidade_nome")
    val modalityName: String? = null,
    @SerialName("formato_id")
    val formatId: Int? = null,
    @SerialName("formato_nome")
    val formatName: String? = null,
    @SerialName("equipas_inscritas")
    val registeredTeams: Long? = 0,
    @SerialName("distancia_km")
    val distanceKm: Double? = null,
    @SerialName("tipo_participacao")
    val participationType: String? = null,
    @SerialName("tipo_evento")
    val eventType: String? = null
)

@Serializable
data class EventsCurrentUserRow(
    val id: Int,
    @SerialName("auth_user_id")
    val authUserId: String? = null
)
