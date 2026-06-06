package com.example.squadup.features.auth.register

import androidx.annotation.StringRes

data class RegisterUiState(
    val fullName: String = "",
    val username: String = "",
    val email: String = "",
    val birthDate: String = "",
    val password: String = "",
    val accountType: AccountType = AccountType.Player,
    val modalities: List<Modality> = emptyList(),
    val selectedModalities: List<String> = emptyList(),
    val isLoading: Boolean = false,
    val isRegisterSuccessful: Boolean = false,
    @param:StringRes val errorMessage: Int? = null
)
