package com.example.squadup.features.notifications

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NotificationsUserRow(
    val id: Int,
    @SerialName("auth_user_id")
    val authUserId: String? = null
)

@Serializable
data class NotificationsRow(
    val id: Int,
    @SerialName("titulo")
    val title: String,
    @SerialName("descricao")
    val description: String? = null,
    @SerialName("imagem_url")
    val imageUrl: String? = null,
    @SerialName("data_criacao")
    val createdAt: String? = null,
    @SerialName("tipo")
    val type: String? = null,
    @SerialName("is_lida")
    val isRead: Boolean? = null
)
