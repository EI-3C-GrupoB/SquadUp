package com.example.squadup.features.events.createevent

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateEventUserRow(
    val id: Int,
    @SerialName("auth_user_id")
    val authUserId: String? = null,
    @SerialName("tipo_conta")
    val accountType: Int? = null
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
    val teamId: Int? = null,
    @SerialName("evento_id")
    val eventId: Int? = null
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

    @SerialName("descricao")
    val description: String? = null,

    @SerialName("imagem_url")
    val imageUrl: String? = null,

    @SerialName("morada")
    val address: String?,

    @SerialName("latitude")
    val latitude: Double?,

    @SerialName("longitude")
    val longitude: Double?,

    @SerialName("is_private")
    val isPrivate: Boolean,

    @SerialName("codigo_acesso")
    val codigoAcesso: String? = null,

    @SerialName("recorrente")
    val recorrente: Boolean? = null,

    @SerialName("tipo_recorrencia")
    val tipoRecorrencia: String? = null,

    @SerialName("data_inicio")
    val startDate: String?,

    @SerialName("data_fim")
    val endDate: String?,

    @SerialName("data_ini_inscricao")
    val registrationStartDate: String?,

    @SerialName("data_fim_inscricao")
    val registrationEndDate: String?,

    @SerialName("max_equipas")
    val maxTeams: Int?,

    @SerialName("limite_participacoes")
    val participationLimit: Int?,

    @SerialName("preco")
    val price: Double,

    @SerialName("taxa_inscricao")
    val entryFee: Double,

    @SerialName("moeda")
    val currency: String = "EUR",

    @SerialName("estado_evento")
    val eventStatus: String,

    @SerialName("tipo_participacao")
    val participationType: String = "individual",

    @SerialName("regras")
    val rules: String?,

    @SerialName("criador_id")
    val creatorId: Int?,

    @SerialName("modalidade_id")
    val modalityId: Int?,

    @SerialName("formato_id")
    val formatId: Int?,

    @SerialName("tipo_evento")
    val eventType: String? = null
)

@Serializable
data class CreateEventConflictRow(
    val id: Int,
    @SerialName("titulo")
    val title: String
)

data class CreateEventUserContext(
    val userRole: com.example.squadup.core.enums.UserRole?,
    val userTeams: List<NotifyTeamItem>,
    val formatOptions: List<String>
)
