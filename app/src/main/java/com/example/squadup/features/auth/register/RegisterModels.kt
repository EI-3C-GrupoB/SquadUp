package com.example.squadup.features.auth.register

import androidx.annotation.StringRes
import com.example.squadup.core.enums.PlayStyle
import com.example.squadup.core.enums.UserRole
import com.example.squadup.core.ui.components.SelectedLocation
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

class RegisterException(@param:StringRes val messageRes: Int) : Exception()

data class RegisterProfile(
    val fullName: String,
    val username: String,
    val email: String,
    val birthDate: String,
    val password: String,
    val accountType: UserRole,
    val modalityIds: List<Int>,
    val location: SelectedLocation?,
    val playStyle: PlayStyle,
    val notificationRadius: Int
)

@Serializable
data class Modality(
    val id: Int,
    @SerialName("nome")
    val name: String
)
