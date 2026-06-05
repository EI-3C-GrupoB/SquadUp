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
    val ownerId: Int? = null
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
