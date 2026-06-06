package com.example.squadup.features.auth.login

import androidx.annotation.StringRes

data class LoginCredentials(
    val email: String,
    val password: String
)

class LoginException(@param:StringRes val messageRes: Int) : Exception()
