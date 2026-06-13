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
    val wins: Int,
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
data class ProfileInscricaoRow(
    @SerialName("equipa_id")
    val teamId: Int? = null
)

@Serializable
data class ProfileTeamRow(
    val id: Int,
    @SerialName("user_id")
    val ownerId: Int? = null
)

@Serializable
data class ProfileGameTeamRow(
    @SerialName("equipa_id")
    val teamId: Int? = null,
    @SerialName("jogo_id")
    val gameId: Int? = null,
    @SerialName("is_vencedor")
    val isWinner: Boolean? = null
)

@Serializable
data class ProfileActionTypeRow(
    val id: Int,
    @SerialName("nome")
    val name: String
)

@Serializable
data class ProfileTimelineRow(
    val id: Int
)

@Serializable
data class ProfileConviteRow(
    @SerialName("equipa_id")
    val teamId: Int,
    @SerialName("estado")
    val estado: String? = null
)

