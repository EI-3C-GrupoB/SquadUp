package com.example.squadup.features.profile.edit

import androidx.annotation.StringRes
import com.example.squadup.core.enums.PlayStyle
import com.example.squadup.core.enums.SportType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

class EditProfileException(@param:StringRes val messageRes: Int) : Exception()

data class EditableProfile(
    val id: Int,
    val username: String,
    val playStyle: PlayStyle,
    val sports: List<SportType>
)

data class EditProfileUpdate(
    val username: String,
    val playStyle: PlayStyle,
    val sports: List<SportType>
)

@Serializable
data class EditableProfileRow(
    val id: Int,
    val username: String,
    @SerialName("play_style")
    val playStyle: String? = null
)

@Serializable
data class EditUserProfileUpdateRow(
    val username: String,
    @SerialName("play_style")
    val playStyle: String
)

@Serializable
data class EditModalityRow(
    val id: Int,
    @SerialName("nome")
    val name: String
)

@Serializable
data class EditUserModalityRow(
    @SerialName("modalidade_id")
    val modalityId: Int
)

@Serializable
data class EditUserModalityInsertRow(
    @SerialName("user_id")
    val userId: Int,
    @SerialName("modalidade_id")
    val modalityId: Int
)
