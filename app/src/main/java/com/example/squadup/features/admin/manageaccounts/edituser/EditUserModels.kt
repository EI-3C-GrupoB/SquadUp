package com.example.squadup.features.admin.manageaccounts.edituser

import com.example.squadup.features.admin.manageaccounts.AccountRole
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EditUserRow(
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

fun EditUserRow.toEditUserUiState(): EditUserUiState {
    val displayName = name?.takeIf { it.isNotBlank() }
        ?: username?.takeIf { it.isNotBlank() }
        ?: email?.substringBefore("@")
        ?: "Utilizador $id"

    return EditUserUiState(
        userId = id.toString(),
        userName = displayName,
        userEmail = email.orEmpty(),
        userInitials = displayName.toInitials(),
        selectedRole = toAccountRole(),
        isSuspended = accountState == "suspenso",
        isLoading = false
    )
}

private fun EditUserRow.toAccountRole(): AccountRole {
    return when {
        isAdmin == true -> AccountRole.Admin
        accountType == 2 || accountType == 3 -> AccountRole.Organizer
        else -> AccountRole.Player
    }
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
