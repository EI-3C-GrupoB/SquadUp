package com.example.squadup.features.teams

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TeamsUserRow(
    val id: Int,
    @SerialName("auth_user_id")
    val authUserId: String? = null,
    @SerialName("nome")
    val name: String = "",
    val username: String = ""
)

@Serializable
data class TeamsTeamRow(
    val id: Int,
    @SerialName("nome")
    val name: String,
    @SerialName("codigo_convite")
    val inviteCode: String? = null,
    @SerialName("user_id")
    val ownerId: Int? = null,
    @SerialName("modalidade_id")
    val modalidadeId: Int? = null,
    @SerialName("descricao")
    val description: String? = null,
    @SerialName("emblema")
    val logoUrl: String? = null,
    @SerialName("is_private")
    val isPrivate: Boolean = false
)

@Serializable
data class TeamsRegistrationRow(
    val id: Int,
    @SerialName("role")
    val role: String? = null,
    @SerialName("is_capitao")
    val isCaptain: Boolean? = null,
    @SerialName("equipa_id")
    val teamId: Int? = null,
    @SerialName("evento_id")
    val eventId: Int? = null,
    @SerialName("user_id")
    val userId: Int? = null
)

@Serializable
data class TeamsInviteRow(
    val id: Int,
    @SerialName("equipa_id")
    val teamId: Int? = null,
    @SerialName("convidado_user_id")
    val invitedUserId: Int? = null,
    val estado: String? = null,
    val tipo: String? = null
)

@Serializable
data class InviteInsert(
    @SerialName("equipa_id")
    val teamId: Int,
    @SerialName("convidado_user_id")
    val invitedUserId: Int,
    val tipo: String,
    val estado: String = "pendente"
)

@Serializable
data class NotificationInsert(
    @SerialName("user_id")
    val userId: Int,
    @SerialName("titulo")
    val title: String,
    @SerialName("descricao")
    val description: String,
    val tipo: String,
    @SerialName("referencia_id")
    val referenceId: Int? = null,
    @SerialName("referencia_tipo")
    val referenceType: String? = null
)

@Serializable
data class InviteUpdate(
    val estado: String,
    @SerialName("data_resposta")
    val responseDate: String
)

@Serializable
data class RegistrationInsert(
    @SerialName("equipa_id")
    val teamId: Int,
    @SerialName("user_id")
    val userId: Int,
    @SerialName("evento_id")
    val eventId: Int? = null,
    val role: String = "membro",
    @SerialName("estado_inscricao")
    val status: String = "aceite"
)

@Serializable
data class NotificationUpdate(
    @SerialName("is_lida")
    val isRead: Boolean
)

@Serializable
data class TeamsGameRow(
    val id: Int,
    @SerialName("data_hora_prevista") val scheduledAt: String? = null,
    @SerialName("morada") val address: String? = null,
    @SerialName("evento_id") val eventId: Int? = null,
    @SerialName("estado_jogo") val status: String? = null
)

@Serializable
data class TeamsGameTeamRow(
    @SerialName("jogo_id") val gameId: Int,
    @SerialName("equipa_id") val teamId: Int
)

@Serializable
data class TeamsEventRow(
    val id: Int,
    @SerialName("titulo") val title: String = ""
)
