package com.example.squadup.features.admin.manageaccounts.createuser

import com.example.squadup.features.admin.manageaccounts.AccountRole

data class CreateUserUiState(
    val name: String = "",
    val email: String = "",
    val username: String = "",
    val password: String = "",
    val selectedRole: AccountRole = AccountRole.Organizer,
    val isAdminRole: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isCreateSuccessful: Boolean = false
)
