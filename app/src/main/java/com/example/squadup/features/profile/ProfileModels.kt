package com.example.squadup.features.profile

import androidx.annotation.StringRes
import com.example.squadup.core.enums.UserRole
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

class ProfileException(@param:StringRes val messageRes: Int) : Exception()

data class ProfileData(
    val id: Int,
    val displayName: String,
    val username: String,
    val isAdmin: Boolean,
    val role: UserRole,
    val playStyle: Int?,
    val photoUrl: String? = null,
    val matchesPlayed: Int,
    val goals: Int,
    val teams: Int
)

@Serializable
data class UserProfileRow(
    val id: Int,
    @SerialName("nome")
    val name: String? = null,
    val username: String? = null,
    @SerialName("is_admin")
    val isAdmin: Boolean? = false,
    @SerialName("play_style")
    val playStyle: Int? = null,
    @SerialName("foto_url")
    val photoUrl: String? = null,
    @SerialName("tipo_conta")
    val accountType: Int? = null
)

@Serializable
data class PlayerStatsRow(
    @SerialName("total_jogos")
    val totalMatches: Int? = 0,
    @SerialName("total_golos")
    val totalGoals: Int? = 0,
    @SerialName("total_equipas")
    val totalTeams: Int? = 0
)

