package com.example.squadup.features.profile.edit

import androidx.annotation.StringRes
import com.example.squadup.core.enums.PlayStyle
import com.example.squadup.core.enums.SportType
import com.example.squadup.core.ui.components.SelectedLocation
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

class EditProfileException(@param:StringRes val messageRes: Int) : Exception()

data class EditableProfile(
    val id: Int,
    val name: String,
    val username: String,
    val playStyle: PlayStyle,
    val notificationRadius: Int,
    val sports: List<SportType>,
    val photoUrl: String? = null,
    val location: SelectedLocation? = null
)

data class EditProfileUpdate(
    val name: String,
    val username: String,
    val playStyle: PlayStyle,
    val notificationRadius: Int,
    val sports: List<SportType>,
    val location: SelectedLocation?
)

@Serializable
data class EditableProfileRow(
    val id: Int,
    @SerialName("nome")
    val name: String? = null,
    val username: String? = null,
    @SerialName("play_style")
    val playStyle: Int? = null,
    @SerialName("raio_notificacao")
    val notificationRadius: Int? = null,
    @SerialName("foto_url")
    val photoUrl: String? = null,
    @SerialName("loc_id")
    val locId: Long? = null
)

@Serializable
data class EditUserProfileUpdateRow(
    @SerialName("nome")
    val name: String,
    val username: String,
    @SerialName("play_style")
    val playStyle: Int,
    @SerialName("raio_notificacao")
    val notificationRadius: Int,
    @SerialName("loc_id")
    val locId: Long? = null
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
