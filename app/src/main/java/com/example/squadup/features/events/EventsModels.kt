package com.example.squadup.features.events

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EventsEventRow(
    val id: Int,
    @SerialName("titulo")
    val title: String,
    @SerialName("morada")
    val address: String? = null,
    @SerialName("data_inicio")
    val startDate: String? = null,
    @SerialName("preco")
    val price: Double? = null,
    @SerialName("taxa_inscricao")
    val entryFee: Double? = null,
    @SerialName("moeda")
    val currency: String? = null,
    @SerialName("limite_participacoes")
    val participationLimit: Int? = null,
    @SerialName("max_equipas")
    val maxTeams: Int? = null,
    @SerialName("tipo_evento")
    val eventType: String? = null,
    @SerialName("estado_evento")
    val eventStatus: String? = null,
    @SerialName("modalidade_id")
    val modalityId: Int? = null
)

@Serializable
data class EventsModalityRow(
    val id: Int,
    @SerialName("nome")
    val name: String
)

@Serializable
data class EventsRegistrationRow(
    val id: Int,
    @SerialName("evento_id")
    val eventId: Int? = null
)
