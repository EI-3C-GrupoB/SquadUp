package com.example.squadup.features.auth.register

import androidx.annotation.StringRes
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

class RegisterException(@param:StringRes val messageRes: Int) : Exception()

enum class AccountType {
    Player,
    Organizer
}

data class RegisterProfile(
    val fullName: String,
    val username: String,
    val email: String,
    val birthDate: String,
    val password: String,
    val accountType: AccountType,
    val modalityNames: List<String>
)

@Serializable
data class Modality(
    val id: Int,
    @SerialName("nome")
    val name: String
)

