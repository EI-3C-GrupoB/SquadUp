package com.example.squadup.features.events.moredetails

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MoreDetailsEventRow(
    val id: Int,
    @SerialName("titulo")
    val title: String,
    @SerialName("morada")
    val address: String? = null,
    @SerialName("data_inicio")
    val startDate: String? = null,
    @SerialName("data_fim")
    val endDate: String? = null,
    @SerialName("tipo_evento")
    val eventType: String? = null,
    @SerialName("max_equipas")
    val maxTeams: Int? = null,
    @SerialName("regras")
    val rules: String? = null
)
