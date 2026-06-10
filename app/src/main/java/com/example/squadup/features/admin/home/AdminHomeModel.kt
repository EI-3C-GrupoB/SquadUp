package com.example.squadup.features.admin.home


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

data class AdminHomeDashboard(
    val totalUsers: Int,
    val usersGrowthPercent: Float,
    val activeMatches: Int,
    val activeSportsCount: Int,
    val activeEvents: Int,
    val sportPopularity: List<SportPopularityItem>
)

data class SportPopularityItem(
    val sportName: String,
    val percentage: Int
)

@Serializable
data class AdminUserCountRow(
    val id: Int,

    @SerialName("data_registo")
    val createdAt: String? = null
)

@Serializable
data class AdminEventRow(
    val id: Int,

    @SerialName("estado_evento")
    val status: String? = null,

    @SerialName("modalidade_id")
    val sportId: Int? = null
)

@Serializable
data class AdminMatchRow(
    val id: Int,

    @SerialName("estado_jogo")
    val status: String? = null,

    @SerialName("evento_id")
    val eventId: Int? = null
)

@Serializable
data class AdminSportRow(
    val id: Int,

    @SerialName("nome")
    val name: String
)