package com.example.squadup.features.events.manageevent

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ManageEventRow(
    val id: Int,
    @SerialName("titulo")
    val title: String,
    @SerialName("morada")
    val address: String? = null,
    @SerialName("data_inicio")
    val startDate: String? = null,
    @SerialName("data_fim")
    val endDate: String? = null,
    @SerialName("estado_evento")
    val status: String? = null,
    @SerialName("is_private")
    val isPrivate: Boolean? = null,
    @SerialName("max_equipas")
    val maxTeams: Int? = null,
    @SerialName("limite_participacoes")
    val participationLimit: Int? = null,
    @SerialName("preco")
    val price: Double? = null,
    @SerialName("taxa_inscricao")
    val entryFee: Double? = null,
    @SerialName("tipo_evento")
    val eventType: String? = null,
    @SerialName("modalidade_id")
    val modalityId: Int? = null
)

@Serializable
data class ManageEventModalityRow(
    val id: Int,
    @SerialName("nome")
    val name: String
)

@Serializable
data class ManageEventRegistrationRow(
    val id: Int,
    @SerialName("data_inscricao")
    val createdAt: String? = null,
    @SerialName("estado_inscricao")
    val status: String? = null,
    @SerialName("is_capitao")
    val isCaptain: Boolean? = null,
    @SerialName("equipa_id")
    val teamId: Int? = null,
    @SerialName("evento_id")
    val eventId: Int? = null,
    @SerialName("user_id")
    val userId: Int? = null
)

@Serializable
data class ManageEventTeamRow(
    val id: Int,
    @SerialName("nome")
    val name: String
)

@Serializable
data class ManageEventUserRow(
    val id: Int,
    @SerialName("nome")
    val name: String,
    @SerialName("nivel_experiencia")
    val experienceLevel: Int? = null
)

@Serializable
data class ManageEventGameRow(
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
data class ManageEventGameTeamRow(
    @SerialName("equipa_id")
    val teamId: Int,
    @SerialName("jogo_id")
    val gameId: Int,
    @SerialName("resultado")
    val result: String? = null,
    @SerialName("is_vencedor")
    val isWinner: Boolean? = null
)

@Serializable
data class ManageEventTimelineRow(
    val id: Int,
    @SerialName("minutos_jogo")
    val minute: Int? = null,
    @SerialName("descricao")
    val description: String? = null,
    @SerialName("equipa_id")
    val teamId: Int? = null,
    @SerialName("user_id")
    val userId: Int? = null,
    @SerialName("tipo_acao_id")
    val actionTypeId: Int? = null,
    @SerialName("jogo_id")
    val gameId: Int? = null
)

@Serializable
data class ManageEventActionTypeRow(
    val id: Int,
    @SerialName("nome")
    val name: String
)

@Serializable
data class ManageEventStatusUpdateRow(
    @SerialName("estado_evento")
    val status: String
)
