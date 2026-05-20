package com.example.squadup.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserModalityModel(
    @SerialName("user_id")
    val userId: Int,

    @SerialName("modalidade_id")
    val modalityId: Int
)