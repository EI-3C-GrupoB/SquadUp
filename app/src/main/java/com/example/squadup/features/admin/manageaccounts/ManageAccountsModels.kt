package com.example.squadup.features.admin.manageaccounts

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ManageAccountRow(
    val id: Int,

    @SerialName("nome")
    val name: String? = null,

    val username: String? = null,

    val email: String? = null,

    @SerialName("is_admin")
    val isAdmin: Boolean? = false,

    @SerialName("tipo_conta")
    val accountType: Int? = null,

    @SerialName("estado_conta")
    val accountState: String? = null
)

fun ManageAccountRow.toManageAccountItem(): ManageAccountItem {
    val displayName = name?.takeIf { it.isNotBlank() }
        ?: username?.takeIf { it.isNotBlank() }
        ?: email?.substringBefore("@")
        ?: "Utilizador $id"

    return ManageAccountItem(
        id = id.toString(),
        initials = displayName.toInitials(),
        name = displayName,
        email = email.orEmpty(),
        role = toAccountRole(),
        isSuspended = accountState == "suspenso"
    )
}

fun ManageAccountRow.toAccountRole(): AccountRole {
    return when {
        isAdmin == true -> AccountRole.Admin
        accountType == 2 || accountType == 3 -> AccountRole.Organizer
        else -> AccountRole.Player
    }
}

fun AccountRole.toAccountUpdatePayload(): Map<String, Any?> {
    return when (this) {
        AccountRole.Admin -> mapOf(
            "is_admin" to true,
            "tipo_conta" to 2
        )
        AccountRole.Organizer -> mapOf(
            "is_admin" to false,
            "tipo_conta" to 2
        )
        AccountRole.Player -> mapOf(
            "is_admin" to false,
            "tipo_conta" to 1
        )
    }
}

fun AccountRole.toAccountType(): Int {
    return when (this) {
        AccountRole.Admin -> 2
        AccountRole.Organizer -> 2
        AccountRole.Player -> 1
    }
}

fun AccountRole.toIsAdmin(): Boolean {
    return this == AccountRole.Admin
}

private fun String.toInitials(): String {
    return trim()
        .split(" ")
        .filter { it.isNotBlank() }
        .take(2)
        .map { it.first().uppercaseChar() }
        .joinToString("")
        .ifBlank { "U" }
}
