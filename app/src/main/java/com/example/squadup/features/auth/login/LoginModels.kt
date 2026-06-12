package com.example.squadup.features.auth.login

import androidx.annotation.StringRes
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

data class LoginCredentials(
    val email: String,
    val password: String
)

class LoginException(
    @param:StringRes val messageRes: Int,
    val isAccountSuspended: Boolean = false
) : Exception()

@Serializable
data class AccountStatusRow(
    @SerialName("estado_conta")
    val accountState: String? = null
)
