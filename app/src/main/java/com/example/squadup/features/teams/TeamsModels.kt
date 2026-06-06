package com.example.squadup.features.teams

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TeamsUserRow(
    val id: Int,
    @SerialName("auth_user_id")
    val authUserId: String? = null,
    @SerialName("nome")
    val name: String = "",
    val username: String = ""
)

@Serializable
data class TeamsTeamRow(
    val id: Int,
    @SerialName("nome")
    val name: String,
    @SerialName("codigo_convite")
    val inviteCode: String? = null,
    @SerialName("user_id")
    val ownerId: Int? = null,
    @SerialName("modalidade_id")
    val modalidadeId: Int? = null,
    @SerialName("descricao")
    val description: String? = null,
    @SerialName("emblema")
    val logoUrl: String? = null,
    @SerialName("is_private")
    val isPrivate: Boolean = false
)

@Serializable
data class TeamsRegistrationRow(
    val id: Int,
    @SerialName("role")
    val role: String? = null,
    @SerialName("is_capitao")
    val isCaptain: Boolean? = null,
    @SerialName("equipa_id")
    val teamId: Int? = null,
    @SerialName("user_id")
    val userId: Int? = null
)

@Serializable
data class TeamsInviteRow(
    val id: Int,
    @SerialName("equipa_id")
    val teamId: Int? = null,
    @SerialName("convidado_user_id")
    val invitedUserId: Int? = null,
    val estado: String? = null,
    val tipo: String? = null
)
