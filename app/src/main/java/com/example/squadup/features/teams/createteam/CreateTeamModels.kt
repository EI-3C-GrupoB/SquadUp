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
    val ownerId: Int?
)
