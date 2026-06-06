package com.example.squadup.features.teams.createteam

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateTeamUserRow(
    val id: Int,
    @SerialName("auth_user_id")
    val authUserId: String? = null
)

@Serializable
data class CreateTeamInsertRow(
    @SerialName("nome")
    val name: String,
    @SerialName("codigo_convite")
    val inviteCode: String,
    @SerialName("user_id")
    val ownerId: Int?,
    @SerialName("modalidade_id")
    val modalidadeId: Int,
    @SerialName("descricao")
    val description: String? = null,
    @SerialName("is_private")
    val isPrivate: Boolean = false,
    @SerialName("cor_principal")
    val primaryColor: String? = null,
    @SerialName("emblema")
    val logoUrl: String? = null
)

@Serializable
data class CreateTeamRegistrationInsertRow(
    @SerialName("equipa_id")
    val teamId: Int,
    @SerialName("user_id")
    val userId: Int,
    @SerialName("role")
    val role: String = "capitao",
    @SerialName("estado_inscricao")
    val status: String = "aceite",
    @SerialName("is_capitao")
    val isCaptain: Boolean = true
)
