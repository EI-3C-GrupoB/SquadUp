package com.example.squadup.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserProfileModel(
    @SerialName("auth_user_id")
    val authUserId: String,

    @SerialName("nome")
    val fullName: String,

    @SerialName("username")
    val username: String,

    @SerialName("email")
    val email: String,

    @SerialName("data_nascimento")
    val birthDate: String,

    @SerialName("nivel_experiencia")
    val experienceLevel: Int
)