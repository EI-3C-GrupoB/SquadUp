package com.example.squadup.features.notifications

import com.example.squadup.core.SupabaseClientProvider
import com.example.squadup.features.teams.NotificationInsert
import com.example.squadup.features.teams.RegistrationInsert
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.realtime.PostgresAction
import io.github.jan.supabase.realtime.channel
import io.github.jan.supabase.realtime.postgresChangeFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class NotificationsRepository(
    private val supabaseClient: SupabaseClient = SupabaseClientProvider.client
) {
    fun getNotificationsRealtime(): Flow<NotificationsUiState> = flow {
        val user = getCurrentUserRow()

        if (user == null) {
            android.util.Log.d(
                "NotificationsRepo",
                "No user logged in for realtime notifications"
            )
            emit(NotificationsUiState())
            return@flow
        }

        android.util.Log.d(
            "NotificationsRepo",
            "Setting up realtime notifications for user: ${user.id}"
        )

        val channel = supabaseClient.channel(
            "notifications_realtime_${user.id}_${System.currentTimeMillis()}"
        )

        val changes = channel
            .postgresChangeFlow<PostgresAction>(schema = "public") {
                table = "notificacao"
            }
            .map { Unit }

        channel.subscribe()

        emitAll(
            changes
                .onStart {
                    emit(Unit)
                }
                .map {
                    loadNotificationsUiState(user.id)
                }
        )
    }

    private suspend fun loadNotificationsUiState(
        userId: Int
    ): NotificationsUiState {
        val rows = supabaseClient
            .from("notificacao")
            .select {
                filter {
                    eq("user_id", userId)
                    eq("is_lida", false)
                }
            }
            .decodeList<NotificationsRow>()

        val myNotifications = rows
            .sortedByDescending { it.createdAt.orEmpty() }
            .map { it.toNotificationItem() }

        val today = LocalDate.now()

        return NotificationsUiState(
            todayNotifications = myNotifications.filter {
                it.createdAt?.toLocalDate() == today
            },
            earlierNotifications = myNotifications.filter {
                it.createdAt?.toLocalDate() != today
            }
        )
    }

    suspend fun deleteNotification(notificationId: String): Result<Unit> {
        return try {
            markNotificationAsRead(notificationId.toInt())
            Result.success(Unit)
        } catch (e: Exception) {
            android.util.Log.e("NotificationsRepo", "Error deleting notification", e)
            Result.failure(e)
        }
    }

    suspend fun respondToJoinRequest(
        notificationId: String,
        conviteId: Int,
        accept: Boolean
    ): Result<Unit> {
        android.util.Log.d(
            "NotificationsRepo",
            "Responding to invite/request: notificationId=$notificationId, conviteId=$conviteId, accept=$accept"
        )

        return try {
            val currentUser = getCurrentUserRow()
                ?: throw Exception("Utilizador não encontrado")

            val invite = supabaseClient
                .from("convite")
                .select {
                    filter {
                        eq("id", conviteId)
                    }
                }
                .decodeSingle<NotificationInviteRow>()

            val teamId = invite.teamId
                ?: throw Exception("Convite sem equipa associada")

            val invitedUserId = invite.invitedUserId
                ?: throw Exception("Convite sem utilizador associado")

            val inviteType = invite.type
                ?: throw Exception("Convite sem tipo associado")

            val team = supabaseClient
                .from("equipa")
                .select {
                    filter {
                        eq("id", teamId)
                    }
                }
                .decodeSingle<NotificationTeamRow>()

            validateInviteResponsePermission(
                inviteType = inviteType,
                currentUserId = currentUser.id,
                invitedUserId = invitedUserId,
                teamOwnerId = team.ownerId
            )

            if (invite.status != "pendente") {
                markNotificationAsRead(notificationId.toInt())
                return Result.success(Unit)
            }

            val newState = if (accept) {
                "aceite"
            } else {
                "recusado"
            }

            supabaseClient
                .from("convite")
                .update(
                    NotificationInviteUpdate(
                        status = newState,
                        responseDate = OffsetDateTime.now().toString()
                    )
                ) {
                    filter {
                        eq("id", conviteId)
                    }
                }

            if (accept) {
                addUserToTeamIfNeeded(
                    teamId = teamId,
                    userId = invitedUserId
                )
            }

            notifyResponseTarget(
                inviteType = inviteType,
                accept = accept,
                currentUserName = currentUser.name,
                invitedUserId = invitedUserId,
                team = team
            )

            markNotificationAsRead(notificationId.toInt())

            Result.success(Unit)
        } catch (e: Exception) {
            android.util.Log.e("NotificationsRepo", "Error responding to invite/request", e)
            Result.failure(e)
        }
    }

    private fun validateInviteResponsePermission(
        inviteType: String,
        currentUserId: Int,
        invitedUserId: Int,
        teamOwnerId: Int?
    ) {
        when (inviteType) {
            "pedido_adesao" -> {
                if (teamOwnerId != currentUserId) {
                    throw Exception("Apenas o capitão pode responder a este pedido.")
                }
            }

            "convite" -> {
                if (invitedUserId != currentUserId) {
                    throw Exception("Apenas o utilizador convidado pode responder a este convite.")
                }
            }

            else -> {
                throw Exception("Tipo de convite inválido.")
            }
        }
    }

    private suspend fun addUserToTeamIfNeeded(
        teamId: Int,
        userId: Int
    ) {
        val existingRegistrations = supabaseClient
            .from("inscricao")
            .select {
                filter {
                    eq("equipa_id", teamId)
                    eq("user_id", userId)
                }
            }
            .decodeList<NotificationRegistrationRow>()

        if (existingRegistrations.isEmpty()) {
            supabaseClient
                .from("inscricao")
                .insert(
                    RegistrationInsert(
                        teamId = teamId,
                        userId = userId
                    )
                )
        }
    }

    private suspend fun notifyResponseTarget(
        inviteType: String,
        accept: Boolean,
        currentUserName: String,
        invitedUserId: Int,
        team: NotificationTeamRow
    ) {
        val responseNotificationUserId = if (inviteType == "convite") {
            team.ownerId
        } else {
            invitedUserId
        } ?: return

        val title = when {
            inviteType == "convite" && accept -> "Convite aceite"
            inviteType == "convite" && !accept -> "Convite recusado"
            inviteType == "pedido_adesao" && accept -> "Pedido aceite"
            else -> "Pedido recusado"
        }

        val description = when {
            inviteType == "convite" && accept ->
                "$currentUserName aceitou o convite para entrar na equipa ${team.name}."

            inviteType == "convite" && !accept ->
                "$currentUserName recusou o convite para entrar na equipa ${team.name}."

            inviteType == "pedido_adesao" && accept ->
                "Agora fazes parte da equipa ${team.name}."

            else ->
                "O teu pedido para entrar na equipa ${team.name} foi recusado."
        }

        supabaseClient
            .from("notificacao")
            .insert(
                NotificationInsert(
                    userId = responseNotificationUserId,
                    title = title,
                    description = description,
                    tipo = "equipa",
                    referenceId = team.id,
                    referenceType = "equipa"
                )
            )
    }

    private suspend fun markNotificationAsRead(notificationId: Int) {
        supabaseClient
            .from("notificacao")
            .update(
                NotificationReadUpdate(
                    isRead = true
                )
            ) {
                filter {
                    eq("id", notificationId)
                }
            }
    }

    private suspend fun getCurrentUserRow(): NotificationsUserRow? {
        val authUserId = supabaseClient.auth.currentUserOrNull()?.id ?: return null

        return runCatching {
            supabaseClient
                .from("utilizador")
                .select {
                    filter {
                        eq("auth_user_id", authUserId)
                    }
                }
                .decodeSingle<NotificationsUserRow>()
        }.getOrNull()
    }

    private fun NotificationsRow.toNotificationItem(): NotificationItem {
        val hasTeamInviteActions = type == "equipa" && referenceType == "convite"

        return NotificationItem(
            id = id.toString(),
            title = title,
            description = description.orEmpty(),
            timeLabel = createdAt.toTimeLabel(),
            type = type.toNotificationType(),
            imageUrl = imageUrl,
            createdAt = createdAt.toLocalDateTimeOrNull(),
            referenceId = referenceId,
            referenceType = referenceType,
            primaryAction = if (hasTeamInviteActions) "Accept" else null,
            secondaryAction = if (hasTeamInviteActions) "Decline" else null
        )
    }

    private fun String?.toNotificationType(): NotificationType {
        return when (this) {
            "jogo" -> NotificationType.MATCH
            "evento" -> NotificationType.EVENT
            "equipa" -> NotificationType.TEAM
            else -> NotificationType.UPDATE
        }
    }

    private fun String?.toTimeLabel(): String {
        val dateTime = toLocalDateTimeOrNull() ?: return ""
        val today = LocalDate.now()

        return when (dateTime.toLocalDate()) {
            today -> dateTime.format(DateTimeFormatter.ofPattern("HH:mm", Locale.US))
            today.minusDays(1) -> "Yesterday"
            else -> dateTime.format(DateTimeFormatter.ofPattern("MMM dd", Locale.US))
        }
    }

    private fun String?.toLocalDateTimeOrNull(): LocalDateTime? {
        if (isNullOrBlank()) return null
        return runCatching {
            LocalDateTime.parse(replace(" ", "T").take(19))
        }.getOrNull()
    }
}

@Serializable
private data class NotificationReadUpdate(
    @SerialName("is_lida")
    val isRead: Boolean
)

@Serializable
private data class NotificationInviteUpdate(
    @SerialName("estado")
    val status: String,
    @SerialName("data_resposta")
    val responseDate: String
)

@Serializable
private data class NotificationInviteRow(
    val id: Int,
    @SerialName("equipa_id")
    val teamId: Int? = null,
    @SerialName("convidado_user_id")
    val invitedUserId: Int? = null,
    @SerialName("convidador_user_id")
    val inviterUserId: Int? = null,
    @SerialName("estado")
    val status: String? = null,
    @SerialName("tipo")
    val type: String? = null
)

@Serializable
private data class NotificationTeamRow(
    val id: Int,
    @SerialName("nome")
    val name: String,
    @SerialName("user_id")
    val ownerId: Int? = null
)

@Serializable
private data class NotificationRegistrationRow(
    val id: Int,
    @SerialName("equipa_id")
    val teamId: Int? = null,
    @SerialName("user_id")
    val userId: Int? = null
)
