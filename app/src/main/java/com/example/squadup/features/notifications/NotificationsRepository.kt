package com.example.squadup.features.notifications

import com.example.squadup.core.SupabaseClientProvider
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.realtime.channel
import io.github.jan.supabase.realtime.postgresListDataFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import io.github.jan.supabase.realtime.PostgresAction
import io.github.jan.supabase.realtime.postgresChangeFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

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

        android.util.Log.d(
            "NotificationsRepo",
            "Loaded ${myNotifications.size} notifications for user $userId"
        )

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
            supabaseClient
                .from("notificacao")
                .update(
                    NotificationReadUpdate(
                        isRead = true
                    )
                ) {
                    filter {
                        eq("id", notificationId.toInt())
                    }
                }

            Result.success(Unit)
        } catch (e: Exception) {
            android.util.Log.e("NotificationsRepo", "Error deleting notification", e)
            Result.failure(e)
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
        val isJoinRequest = type == "equipa" && referenceType == "convite"
        
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
            primaryAction = if (isJoinRequest) "Accept" else null,
            secondaryAction = if (isJoinRequest) "Decline" else null
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
        return runCatching { LocalDateTime.parse(replace(" ", "T").take(19)) }.getOrNull()
    }

    suspend fun respondToJoinRequest(
        notificationId: String,
        conviteId: Int,
        accept: Boolean
    ): Result<Unit> {
        android.util.Log.d(
            "NotificationsRepo",
            "Responding to join request: notificationId=$notificationId, conviteId=$conviteId, accept=$accept"
        )

        return try {
            val currentUser = getCurrentUserRow()
                ?: throw Exception("Utilizador não encontrado")

            val convite = supabaseClient
                .from("convite")
                .select {
                    filter {
                        eq("id", conviteId)
                    }
                }
                .decodeSingle<com.example.squadup.features.teams.TeamsInviteRow>()

            val teamId = convite.teamId
                ?: throw Exception("Convite sem equipa associada")

            val invitedUserId = convite.invitedUserId
                ?: throw Exception("Convite sem utilizador associado")

            val team = supabaseClient
                .from("equipa")
                .select {
                    filter {
                        eq("id", teamId)
                    }
                }
                .decodeSingle<com.example.squadup.features.teams.TeamsTeamRow>()

            if (team.ownerId != currentUser.id) {
                throw Exception("Apenas o capitão pode responder a este pedido.")
            }

            if (convite.estado != "pendente") {
                supabaseClient
                    .from("notificacao")
                    .update(
                        NotificationReadUpdate(
                            isRead = true
                        )
                    ) {
                        filter {
                            eq("id", notificationId.toInt())
                        }
                    }

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
                    com.example.squadup.features.teams.InviteUpdate(
                        estado = newState,
                        responseDate = java.time.OffsetDateTime.now().toString()
                    )
                ) {
                    filter {
                        eq("id", conviteId)
                    }
                }

            if (accept) {
                val existingRegistrations = supabaseClient
                    .from("inscricao")
                    .select {
                        filter {
                            eq("equipa_id", teamId)
                            eq("user_id", invitedUserId)
                        }
                    }
                    .decodeList<com.example.squadup.features.teams.TeamsRegistrationRow>()

                if (existingRegistrations.isEmpty()) {
                    supabaseClient
                        .from("inscricao")
                        .insert(
                            com.example.squadup.features.teams.RegistrationInsert(
                                teamId = teamId,
                                userId = invitedUserId
                            )
                        )
                }

                supabaseClient
                    .from("notificacao")
                    .insert(
                        com.example.squadup.features.teams.NotificationInsert(
                            userId = invitedUserId,
                            title = "Pedido aceite",
                            description = "Agora fazes parte da equipa ${team.name}.",
                            tipo = "equipa",
                            referenceId = team.id,
                            referenceType = "equipa"
                        )
                    )
            } else {
                supabaseClient
                    .from("notificacao")
                    .insert(
                        com.example.squadup.features.teams.NotificationInsert(
                            userId = invitedUserId,
                            title = "Pedido recusado",
                            description = "O teu pedido para entrar na equipa ${team.name} foi recusado.",
                            tipo = "equipa",
                            referenceId = team.id,
                            referenceType = "equipa"
                        )
                    )
            }

            supabaseClient
                .from("notificacao")
                .update(
                    NotificationReadUpdate(
                        isRead = true
                    )
                ) {
                    filter {
                        eq("id", notificationId.toInt())
                    }
                }

            Result.success(Unit)
        } catch (e: Exception) {
            android.util.Log.e("NotificationsRepo", "Error responding to join request", e)
            Result.failure(e)
        }
    }
}

@Serializable
private data class NotificationReadUpdate(
    @SerialName("is_lida")
    val isRead: Boolean
)
