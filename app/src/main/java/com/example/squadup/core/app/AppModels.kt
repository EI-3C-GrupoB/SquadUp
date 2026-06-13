package com.example.squadup.core.app

import com.example.squadup.core.enums.UserRole
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

data class LoggedInUser(
    val id: Int,
    val displayName: String,
    val username: String,
    val isAdmin: Boolean,
    val photoUrl: String? = null,
    val userRole: UserRole = UserRole.PLAYER
)

@Serializable
data class LoggedInUserRow(
    val id: Int,

    @SerialName("nome")
    val name: String? = null,

    val username: String? = null,

    @SerialName("is_admin")
    val isAdmin: Boolean? = false,

    @SerialName("foto_url")
    val photoUrl: String? = null,

    @SerialName("tipo_conta")
    val accountType: Int? = null,

    @SerialName("estado_conta")
    val accountState: String? = null
)
