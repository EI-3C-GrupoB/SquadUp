package com.example.squadup.features.auth.forgotpassword

import androidx.annotation.StringRes

data class ForgotPasswordUiState(
    val email: String = "",
    val isLoading: Boolean = false,
    val isCodeSent: Boolean = false,
    @param:StringRes val errorMessage: Int? = null
)
