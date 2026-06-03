package com.example.squadup.features.profile

import androidx.annotation.StringRes
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

class ProfileException(@param:StringRes val messageRes: Int) : Exception()

data class ProfileData(
    val id: Int,
    val displayName: String,
    val username: String,
    val isAdmin: Boolean,
    val roleNames: List<String>,
    val playStyle: String?,
    val matchesPlayed: Int,
    val goals: Int,
    val teams: Int
)

@Serializable
data class UserProfileRow(
    val id: Int,
    @SerialName("nome")
    val name: String,
    val username: String,
    @SerialName("is_admin_plataforma")
    val isAdmin: Boolean = false,
    @SerialName("play_style")
    val playStyle: String? = null
)

@Serializable
data class PlayerStatsRow(
    @SerialName("total_jogos")
    val totalMatches: Int = 0,
    @SerialName("total_golos")
    val totalGoals: Int = 0,
    @SerialName("total_equipas")
    val totalTeams: Int = 0
)

@Serializable
data class UserTypeLinkRow(
    @SerialName("tipo_utilizador_id")
    val userTypeId: Int
)

@Serializable
data class UserTypeRow(
    val id: Int,
    @SerialName("tipo")
    val type: String
)
