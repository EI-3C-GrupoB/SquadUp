package com.example.squadup.features.admin.manageaccounts.createuser

import com.example.squadup.features.admin.manageaccounts.AccountRole
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

data class CreateUserRequest(
    val name: String,
    val username: String,
    val email: String,
    val password: String,
    val role: AccountRole,
    val isAdmin: Boolean
)

@Serializable
data class CreateUserProfileRow(
    val id: Int,

    @SerialName("auth_user_id")
    val authUserId: String? = null
)

@Serializable
data class CreateUserProfileInsert(
    @SerialName("nome")
    val name: String,

    val username: String,

    val email: String,

    @SerialName("auth_user_id")
    val authUserId: String,

    @SerialName("is_admin")
    val isAdmin: Boolean,

    @SerialName("tipo_conta")
    val accountType: Int,

    @SerialName("estado_conta")
    val accountStatus: String = "ativo"
)
