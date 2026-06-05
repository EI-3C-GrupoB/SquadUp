package com.example.squadup.features.events.createevent

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateEventUserRow(
    val id: Int,
    @SerialName("auth_user_id")
    val authUserId: String? = null
)

@Serializable
data class CreateEventUserTypeLinkRow(
    @SerialName("tipo_utilizador_id")
    val userTypeId: Int
)

@Serializable
data class CreateEventUserTypeRow(
    val id: Int,
    @SerialName("tipo")
    val type: String
)

@Serializable
data class CreateEventTeamRow(
    val id: Int,
    @SerialName("nome")
    val name: String,
    @SerialName("user_id")
    val ownerId: Int? = null
)

@Serializable
data class CreateEventTeamMemberRow(
    @SerialName("equipa_id")
    val teamId: Int? = null
)

@Serializable
data class CreateEventModalityRow(
    val id: Int,
    @SerialName("nome")
    val name: String
)

@Serializable
data class CreateEventFormatRow(
    val id: Int,
    @SerialName("nome")
    val name: String
)

@Serializable
data class CreateEventInsertRow(
    @SerialName("titulo")
    val title: String,
    @SerialName("morada")
    val address: String?,
    @SerialName("is_private")
    val isPrivate: Boolean,
    @SerialName("data_inicio")
    val startDate: String?,
    @SerialName("data_fim")
    val endDate: String?,
    @SerialName("max_equipas")
    val maxTeams: Int?,
    @SerialName("preco")
    val price: Double,
    @SerialName("taxa_inscricao")
    val entryFee: Double,
    @SerialName("moeda")
    val currency: String = "EUR",
    @SerialName("tipo_evento")
    val eventType: String,
    @SerialName("estado_evento")
    val eventStatus: String = "rascunho",
    @SerialName("regras")
    val rules: String?,
    @SerialName("criador_id")
    val creatorId: Int?,
    @SerialName("modalidade_id")
    val modalityId: Int?,
    @SerialName("formato_id")
    val formatId: Int?
)

data class CreateEventUserContext(
    val userRole: com.example.squadup.core.enums.UserRole?,
    val userTeams: List<NotifyTeamItem>,
    val formatOptions: List<String>
)
