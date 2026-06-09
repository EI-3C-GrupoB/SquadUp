package com.example.squadup.features.events.moredetails

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MoreDetailsEventRow(
    val id: Int,

    @SerialName("titulo")
    val title: String,

    @SerialName("descricao")
    val description: String? = null,

    @SerialName("imagem_url")
    val imageUrl: String? = null,

    @SerialName("morada")
    val address: String? = null,

    @SerialName("latitude")
    val latitude: Double? = null,

    @SerialName("longitude")
    val longitude: Double? = null,

    @SerialName("data_inicio")
    val startDate: String? = null,

    @SerialName("data_fim")
    val endDate: String? = null,

    @SerialName("data_ini_inscricao")
    val registrationStartDate: String? = null,

    @SerialName("data_fim_inscricao")
    val registrationEndDate: String? = null,

    @SerialName("tipo_evento")
    val eventType: String? = null,

    @SerialName("tipo_participacao")
    val participationType: String? = null,

    @SerialName("estado_evento")
    val eventStatus: String? = null,

    @SerialName("max_equipas")
    val maxTeams: Int? = null,

    @SerialName("limite_participacoes")
    val participationLimit: Int? = null,

    @SerialName("preco")
    val price: Double? = null,

    @SerialName("taxa_inscricao")
    val entryFee: Double? = null,

    @SerialName("moeda")
    val currency: String? = null,

    @SerialName("regras")
    val rules: String? = null,

    @SerialName("criador_id")
    val creatorId: Int? = null,

    @SerialName("modalidade_id")
    val modalityId: Int? = null,

    @SerialName("formato_id")
    val formatId: Int? = null
)

@Serializable
data class MoreDetailsModalityRow(
    val id: Int,

    @SerialName("nome")
    val name: String
)

@Serializable
data class MoreDetailsFormatRow(
    val id: Int,

    @SerialName("nome")
    val name: String,

    @SerialName("descricao")
    val description: String? = null
)

@Serializable
data class MoreDetailsRegistrationRow(
    val id: Int,

    @SerialName("evento_id")
    val eventId: Int? = null,

    @SerialName("equipa_id")
    val teamId: Int? = null,

    @SerialName("user_id")
    val userId: Int? = null,

    @SerialName("estado_inscricao")
    val registrationStatus: String? = null,

    @SerialName("tipo_inscricao")
    val registrationType: String? = null,

    @SerialName("role")
    val role: String? = null,

    @SerialName("is_capitao")
    val isCaptain: Boolean? = null
)

@Serializable
data class MoreDetailsEventTeamRow(
    val id: Long,

    @SerialName("evento_id")
    val eventId: Int,

    @SerialName("equipa_id")
    val teamId: Int,

    @SerialName("estado")
    val status: String? = null,

    @SerialName("capitao_user_id")
    val captainUserId: Int? = null,

    @SerialName("data_inscricao")
    val createdAt: String? = null
)

@Serializable
data class MoreDetailsTeamRow(
    val id: Int,

    @SerialName("nome")
    val name: String,

    @SerialName("user_id")
    val ownerId: Int? = null
)

@Serializable
data class MoreDetailsEventTeamInsertRow(
    @SerialName("evento_id")
    val eventId: Int,

    @SerialName("equipa_id")
    val teamId: Int,

    @SerialName("estado")
    val status: String,

    @SerialName("capitao_user_id")
    val captainUserId: Int
)


@Serializable
data class MoreDetailsIndividualRegistrationInsertRow(
    @SerialName("evento_id")
    val eventId: Int,

    @SerialName("user_id")
    val userId: Int,

    @SerialName("equipa_id")
    val teamId: Int?,

    @SerialName("estado_inscricao")
    val registrationStatus: String,

    @SerialName("tipo_inscricao")
    val registrationType: String,

    @SerialName("is_capitao")
    val isCaptain: Boolean,

    @SerialName("role")
    val role: String
)
