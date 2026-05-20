package com.example.squadup.ui.screens.auth.register

import androidx.annotation.StringRes

import com.example.squadup.data.models.ModalityModel

data class RegisterUiState(
    val fullName: String = "",
    val username: String = "",
    val email: String = "",
    val birthDate: String = "",
    val password: String = "",
    val experienceLevel: Float = 1f,
    val availableModalities: List<ModalityModel> = emptyList(),
    val selectedSports: List<String> = emptyList(),
    val isLoading: Boolean = false,

    @StringRes
    val errorMessageRes: Int? = null,

    val isRegisterSuccessful: Boolean = false
) {
    val canRegister: Boolean
        get() = fullName.isNotBlank() &&
                username.isNotBlank() &&
                email.isNotBlank() &&
                birthDate.isNotBlank() &&
                password.isNotBlank() &&
                selectedSports.isNotEmpty() &&
                !isLoading
}