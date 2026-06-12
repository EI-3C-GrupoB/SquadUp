package com.example.squadup.features.auth.resetpassword

import androidx.annotation.StringRes

data class ResetPasswordUiState(
    val email: String = "",
    val code: String = "",
    val newPassword: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    @param:StringRes val errorMessage: Int? = null
)
