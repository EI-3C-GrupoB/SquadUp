package com.example.squadup.features.auth.login

import androidx.annotation.StringRes

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    @param:StringRes val errorMessage: Int? = null,
    val showAccountSuspendedDialog: Boolean = false
)
