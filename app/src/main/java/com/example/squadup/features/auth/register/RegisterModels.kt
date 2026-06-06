package com.example.squadup.features.auth.register

import androidx.annotation.StringRes
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

class RegisterException(@param:StringRes val messageRes: Int) : Exception()

enum class AccountType(val databaseAliases: List<String>) {
    Player(listOf("player", "jogador")),
    Organizer(listOf("organizer", "organizador"))
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

@Serializable
data class UserProfileInsert(
    @SerialName("auth_user_id")
    val authUserId: String,
    @SerialName("nome")
    val fullName: String,
    val username: String,
    val email: String,
    @SerialName("data_nascimento")
    val birthDate: String
)

@Serializable
data class CreatedUser(val id: Int)

@Serializable
data class UserModalityInsert(
    @SerialName("user_id")
    val userId: Int,
    @SerialName("modalidade_id")
    val modalityId: Int
)

@Serializable
data class UserType(
    val id: Int,
    @SerialName("tipo")
    val type: String
)

@Serializable
data class UserTypeInsert(
    @SerialName("user_id")
    val userId: Int,
    @SerialName("tipo_utilizador_id")
    val userTypeId: Int
)
