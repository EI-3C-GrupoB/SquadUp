package com.example.squadup.features.events.livematch

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LiveMatchGameRow(
    val id: Int,
    @SerialName("data_hora_prevista")
    val scheduledAt: String? = null,
    @SerialName("data_hora_real")
    val startedAt: String? = null,
    @SerialName("estado_jogo")
    val status: String? = null,
    @SerialName("morada")
    val address: String? = null,
    @SerialName("evento_id")
    val eventId: Int? = null
)

@Serializable
data class LiveMatchEventRow(
    val id: Int,
    @SerialName("modalidade_id")
    val modalityId: Int? = null,
    @SerialName("formato_id")
    val formatoId: Int? = null,
    @SerialName("criador_id")
    val creatorId: Int? = null
)

@Serializable
data class LiveMatchCurrentUserRow(
    val id: Int,
    @SerialName("auth_user_id")
    val authUserId: String? = null
)

@Serializable
data class LiveMatchFormatoRow(
    val id: Int,
    @SerialName("nome")
    val name: String
)

@Serializable
data class LiveMatchModalityRow(
    val id: Int,
    @SerialName("nome")
    val name: String
)

@Serializable
data class LiveMatchGameTeamRow(
    @SerialName("equipa_id")
    val teamId: Int,
    @SerialName("jogo_id")
    val gameId: Int,
    @SerialName("resultado")
    val result: String? = null
)

@Serializable
data class LiveMatchTeamRow(
    val id: Int,
    @SerialName("nome")
    val name: String
)

@Serializable
data class LiveMatchLineupRow(
    val id: Int,
    @SerialName("equipa_id")
    val teamId: Int? = null,
    @SerialName("user_id")
    val userId: Int? = null,
    @SerialName("jogo_id")
    val gameId: Int? = null
)

@Serializable
data class LiveMatchUserRow(
    val id: Int,
    @SerialName("nome")
    val name: String
)

@Serializable
data class LiveMatchStatsRow(
    @SerialName("equipa_id")
    val teamId: Int,
    @SerialName("remates_total")
    val shots: Int? = null,
    @SerialName("remates_baliza")
    val shotsOnGoal: Int? = null,
    @SerialName("faltas")
    val fouls: Int? = null,
    @SerialName("cantos")
    val corners: Int? = null,
    @SerialName("cartoes_amarelos")
    val yellowCards: Int? = null,
    @SerialName("cartoes_vermelhos")
    val redCards: Int? = null,
    @SerialName("fora_de_jogo")
    val offsides: Int? = null,
    @SerialName("defesas")
    val saves: Int? = null
)

@Serializable
data class LiveMatchTimelineRow(
    val id: Int,
    @SerialName("minutos_jogo")
    val minute: Int? = null,
    @SerialName("descricao")
    val description: String? = null,
    @SerialName("equipa_id")
    val teamId: Int? = null,
    @SerialName("user_id")
    val userId: Int? = null,
    @SerialName("tipo_acao_id")
    val actionTypeId: Int? = null,
    @SerialName("jogo_id")
    val gameId: Int? = null
)

@Serializable
data class LiveMatchActionTypeRow(
    val id: Int,
    @SerialName("nome")
    val name: String
)

@Serializable
data class LiveMatchTimelineInsertRow(
    @SerialName("minutos_jogo")
    val minute: Int,
    @SerialName("descricao")
    val description: String,
    @SerialName("equipa_id")
    val teamId: Int?,
    @SerialName("user_id")
    val userId: Int?,
    @SerialName("tipo_acao_id")
    val actionTypeId: Int?,
    @SerialName("jogo_id")
    val gameId: Int?
)

@Serializable
data class LiveMatchGameStatusUpdateRow(
    @SerialName("estado_jogo")
    val status: String
)

@Serializable
data class LiveMatchStartRow(
    @SerialName("estado_jogo")
    val status: String,
    @SerialName("data_hora_real")
    val startedAt: String
)

@Serializable
data class LiveMatchStatsUpsertRow(
    @SerialName("jogo_id")
    val gameId: Int,
    @SerialName("equipa_id")
    val teamId: Int,
    @SerialName("remates_total")
    val shots: Int,
    @SerialName("remates_baliza")
    val shotsOnGoal: Int,
    @SerialName("cantos")
    val corners: Int,
    @SerialName("faltas")
    val fouls: Int,
    @SerialName("cartoes_amarelos")
    val yellowCards: Int,
    @SerialName("cartoes_vermelhos")
    val redCards: Int,
    @SerialName("fora_de_jogo")
    val offsides: Int,
    @SerialName("defesas")
    val saves: Int
)

@Serializable
data class LiveMatchInscricaoRow(
    val id: Int,
    @SerialName("user_id") val userId: Int? = null,
    @SerialName("equipa_id") val teamId: Int? = null
)

@Serializable
data class LiveMatchScoreUpdateRow(
    @SerialName("resultado")
    val resultado: String,
    @SerialName("is_vencedor")
    val isVencedor: Boolean? = null
)

@Serializable
data class LiveMatchEventoEquipaStatusRow(
    @SerialName("estado")
    val estado: String
)
