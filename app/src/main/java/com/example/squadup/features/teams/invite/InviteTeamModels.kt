package com.example.squadup.features.teams.invite

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class InviteTeamUserRow(
    val id: Int,
    @SerialName("nome")
    val name: String,
    val username: String,
    val email: String? = null,
    @SerialName("auth_user_id")
    val authUserId: String? = null
)

@Serializable
data class InviteTeamTeamRow(
    val id: Int,
    @SerialName("nome")
    val name: String,
    @SerialName("codigo_convite")
    val inviteCode: String? = null,
    @SerialName("user_id")
    val ownerId: Int? = null
)

@Serializable
data class InviteTeamInsertRow(
    @SerialName("equipa_id")
    val teamId: Int,
    @SerialName("convidado_user_id")
    val invitedUserId: Int,
    @SerialName("convidador_user_id")
    val inviterUserId: Int?,
    @SerialName("estado")
    val status: String = "pendente",
    @SerialName("tipo")
    val type: String = "convite"
)
