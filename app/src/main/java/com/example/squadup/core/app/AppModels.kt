package com.example.squadup.core.app

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

data class LoggedInUser(
    val id: Int,
    val displayName: String,
    val username: String,
    val isAdmin: Boolean
)

@Serializable
data class LoggedInUserRow(
    val id: Int,
    @SerialName("nome")
    val name: String = "",
    val username: String = "",
    @SerialName("is_admin_plataforma")
    val isAdmin: Boolean = false
)
