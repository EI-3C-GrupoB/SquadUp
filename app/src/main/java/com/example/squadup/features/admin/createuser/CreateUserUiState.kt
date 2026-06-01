package com.example.squadup.features.admin.createuser

import com.example.squadup.features.admin.manageaccounts.AccountRole

data class CreateUserUiState(
    val email: String = "",
    val username: String = "",
    val password: String = "",
    val selectedRole: AccountRole = AccountRole.Organizer
)
