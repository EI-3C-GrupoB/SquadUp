package com.example.squadup.features.notifications

import com.example.squadup.core.SupabaseClientProvider
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class NotificationsRepository(
    private val supabaseClient: SupabaseClient = SupabaseClientProvider.client
) {
    suspend fun getNotifications(): Result<NotificationsUiState> {
        return try {
            val user = getCurrentUserRow() ?: return Result.success(NotificationsUiState())
            val notifications = supabaseClient
                .from("notificacao")
                .select {
                    filter {
                        eq("user_id", user.id)
                    }
                }
                .decodeList<NotificationsRow>()
                .sortedByDescending { it.createdAt.orEmpty() }
                .map { it.toNotificationItem() }

            val today = LocalDate.now()
            Result.success(
                NotificationsUiState(
                    todayNotifications = notifications.filter {
                        it.createdAt?.toLocalDate() == today
                    },
                    earlierNotifications = notifications.filter {
                        it.createdAt?.toLocalDate() != today
                    }
                )
            )
        } catch (exception: Exception) {
            Result.failure(exception)
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
        return NotificationItem(
            id = id.toString(),
            title = title,
            description = description.orEmpty(),
            timeLabel = createdAt.toTimeLabel(),
            type = type.toNotificationType(),
            imageUrl = imageUrl,
            createdAt = createdAt.toLocalDateTimeOrNull()
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
}
