package com.example.squadup.features.profile.changepassword

data class ChangePasswordUiState(
    val currentPassword: String = "",
    val newPassword: String = "",
    val confirmNewPassword: String = ""
)
