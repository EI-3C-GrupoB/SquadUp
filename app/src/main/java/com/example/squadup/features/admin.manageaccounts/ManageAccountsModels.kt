package com.example.squadup.features.admin.manageaccounts

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ManageAccountsUserRow(
    val id: Int,
    @SerialName("nome")
    val name: String,
    val username: String,
    val email: String,
    @SerialName("is_admin_plataforma")
    val isAdmin: Boolean? = null,
    @SerialName("estado_conta")
    val accountStatus: String? = null
)

@Serializable
data class ManageAccountsUserInsertRow(
    @SerialName("nome")
    val name: String,
    val username: String,
    val email: String,
    @SerialName("is_admin_plataforma")
    val isAdmin: Boolean,
    @SerialName("estado_conta")
    val accountStatus: String = "ativo"
)

@Serializable
data class ManageAccountsUserUpdateRow(
    @SerialName("is_admin_plataforma")
    val isAdmin: Boolean,
    @SerialName("estado_conta")
    val accountStatus: String
)

@Serializable
data class ManageAccountsCreatedUserRow(
    val id: Int
)

@Serializable
data class ManageAccountsUserTypeLinkRow(
    @SerialName("tipo_utilizador_id")
    val userTypeId: Int,
    @SerialName("user_id")
    val userId: Int
)

@Serializable
data class ManageAccountsUserTypeRow(
    val id: Int,
    @SerialName("tipo")
    val type: String
)

@Serializable
data class ManageAccountsUserTypeInsertRow(
    @SerialName("tipo_utilizador_id")
    val userTypeId: Int,
    @SerialName("user_id")
    val userId: Int
)
