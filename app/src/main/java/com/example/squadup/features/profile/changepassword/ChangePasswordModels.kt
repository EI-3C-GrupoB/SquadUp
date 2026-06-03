package com.example.squadup.features.profile.changepassword

import androidx.annotation.StringRes

class ChangePasswordException(@param:StringRes val messageRes: Int) : Exception()

data class ChangePasswordRequest(
    val currentPassword: String,
    val newPassword: String
)
