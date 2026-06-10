package com.example.squadup.features.admin.manageaccounts.edituser

import com.example.squadup.features.admin.manageaccounts.AccountRole

data class EditUserUiState(
    val userId: String = "",
    val userName: String = "",
    val userEmail: String = "",
    val userInitials: String = "",
    val selectedRole: AccountRole = AccountRole.Player,
    val isSuspended: Boolean = false,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val isDeleting: Boolean = false,
    val errorMessage: String? = null,
    val isSaveSuccessful: Boolean = false,
    val isDeleteSuccessful: Boolean = false
)
