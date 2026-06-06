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

class NotificationsRepository(
    private val supabaseClient: SupabaseClient = SupabaseClientProvider.client
) {
    fun getNotificationsRealtime(): Flow<NotificationsUiState> = flow {
        val user = getCurrentUserRow()
        if (user == null) {
            android.util.Log.d("NotificationsRepo", "No user logged in for realtime notifications")
            emit(NotificationsUiState())
            return@flow
        }

        android.util.Log.d("NotificationsRepo", "Setting up realtime notifications for user: ${user.id}")
        val channel = supabaseClient.channel("notifications_realtime_${user.id}")
        val notificationsFlow = channel.postgresListDataFlow<NotificationsRow, Int>(
            schema = "public",
            table = "notificacao",
            primaryKey = NotificationsRow::id
        )

        emitAll(notificationsFlow.map { rows ->
            // Filtro de segurança ADICIONAL para garantir que apenas as notificações do Simão aparecem
            val myNotifications = rows
                .filter { it.userId == user.id && it.isRead != true }
                .sortedByDescending { it.createdAt.orEmpty() }
                .map { it.toNotificationItem() }
            
            android.util.Log.d("NotificationsRepo", "Received ${myNotifications.size} notifications for user ${user.id}")
            
            val today = LocalDate.now()
            NotificationsUiState(
                todayNotifications = myNotifications.filter {
                    it.createdAt?.toLocalDate() == today
                },
                earlierNotifications = myNotifications.filter {
                    it.createdAt?.toLocalDate() != today
                }
            )
        })

        channel.subscribe()
    }

    suspend fun deleteNotification(notificationId: String): Result<Unit> {
        return try {
            supabaseClient.from("notificacao").update(com.example.squadup.features.teams.NotificationUpdate(isRead = true)) {
                filter { eq("id", notificationId.toInt()) }
            }
            Result.success(Unit)
        } catch (e: Exception) {
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

    suspend fun respondToJoinRequest(notificationId: String, conviteId: Int, accept: Boolean): Result<Unit> {
        android.util.Log.d("NotificationsRepo", "Responding to join request: notificationId=$notificationId, conviteId=$conviteId, accept=$accept")
        return try {
            val currentUser = getCurrentUserRow() ?: throw Exception("User not found")
            
            // 1. Obter detalhes do convite
            val convite = supabaseClient.from("convite")
                .select { filter { eq("id", conviteId) } }
                .decodeSingle<com.example.squadup.features.teams.TeamsInviteRow>()
            
            android.util.Log.d("NotificationsRepo", "Found convite: $convite")

            val newState = if (accept) "aceite" else "recusado"
            
            // 2. Atualizar o estado do convite
            val inviteUpdate = com.example.squadup.features.teams.InviteUpdate(
                estado = newState,
                responseDate = java.time.OffsetDateTime.now().toString()
            )
            supabaseClient.from("convite").update(inviteUpdate) {
                filter { eq("id", conviteId) }
            }

            // 3. Obter nome da equipa para a notificação
            val teamId = convite.teamId ?: 0
            val team = supabaseClient.from("equipa")
                .select { filter { eq("id", teamId) } }
                .decodeSingle<com.example.squadup.features.teams.TeamsTeamRow>()

            if (accept) {
                // 4. Se aceite, adicionar à equipa (inscricao)
                val registration = com.example.squadup.features.teams.RegistrationInsert(
                    teamId = teamId,
                    userId = convite.invitedUserId ?: 0
                )
                supabaseClient.from("inscricao").insert(registration)

                // 5. Notificar o utilizador que foi aceite
                val notification = com.example.squadup.features.teams.NotificationInsert(
                    userId = convite.invitedUserId ?: 0,
                    title = "Pedido Aceite!",
                    description = "Agora fazes parte da equipa ${team.name}.",
                    tipo = "equipa",
                    referenceId = team.id,
                    referenceType = "equipa"
                )
                supabaseClient.from("notificacao").insert(notification)
            } else {
                // 5. Notificar o utilizador que foi recusado
                val notification = com.example.squadup.features.teams.NotificationInsert(
                    userId = convite.invitedUserId ?: 0,
                    title = "Pedido Recusado",
                    description = "O teu pedido para entrar na equipa ${team.name} foi recusado.",
                    tipo = "equipa",
                    referenceId = team.id,
                    referenceType = "equipa"
                )
                supabaseClient.from("notificacao").insert(notification)
            }

            // 6. Marcar notificação original como lida
            supabaseClient.from("notificacao").update(com.example.squadup.features.teams.NotificationUpdate(isRead = true)) {
                filter { eq("id", notificationId.toInt()) }
            }

            Result.success(Unit)
        } catch (e: Exception) {
            android.util.Log.e("NotificationsRepo", "Error responding to join request", e)
            Result.failure(e)
        }
    }
}
