package com.example.squadup.features.profile.myevents

import com.example.squadup.core.SupabaseClientProvider
import com.example.squadup.core.enums.EventStatus
import com.example.squadup.core.enums.SportType
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class MyEventsRepository(
    private val supabaseClient: SupabaseClient = SupabaseClientProvider.client
) {
    suspend fun getMyEvents(): Result<List<MyEventItem>> {
        return try {
            val currentUser = getCurrentUserRow() ?: return Result.success(emptyList())
            val events = supabaseClient
                .from("evento")
                .select {
                    filter {
                        eq("criador_id", currentUser.id)
                    }
                }
                .decodeList<MyEventsEventRow>()
            val modalities = supabaseClient.from("modalidade").select().decodeList<MyEventsModalityRow>().associateBy { it.id }
            val registrations = supabaseClient.from("inscricao").select().decodeList<MyEventsRegistrationRow>()
            val games = supabaseClient.from("jogo").select().decodeList<MyEventsGameRow>()

            Result.success(
                events.map { event ->
                    val eventRegistrations = registrations.filter { it.eventId == event.id }
                    MyEventItem(
                        id = event.id.toString(),
                        title = event.title,
                        location = event.address.orEmpty(),
                        date = event.startDate.toShortDate(),
                        teamsCount = eventRegistrations.mapNotNull { it.teamId }.distinct().size,
                        playersCount = eventRegistrations.mapNotNull { it.userId }.distinct().size,
                        status = event.status.toEventStatus(),
                        sportType = sportTypeFrom(event.modalityId?.let { modalities[it]?.name }),
                        registeredCount = eventRegistrations.size,
                        matchesInProgress = games.count { it.eventId == event.id && it.status == "a_decorrer" }
                    )
                }
            )
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }

    private suspend fun getCurrentUserRow(): MyEventsUserRow? {
        val authUserId = supabaseClient.auth.currentUserOrNull()?.id ?: return null
        return runCatching {
            supabaseClient
                .from("utilizador")
                .select {
                    filter {
                        eq("auth_user_id", authUserId)
                    }
                }
                .decodeSingle<MyEventsUserRow>()
        }.getOrNull()
    }

    private fun String?.toEventStatus(): EventStatus {
        return when (this) {
            "rascunho" -> EventStatus.DRAFT
            "publicado" -> EventStatus.REGISTRATION_OPEN
            "a_decorrer" -> EventStatus.ONGOING
            "terminado" -> EventStatus.FINISHED
            "cancelado" -> EventStatus.CANCELLED
            else -> EventStatus.DRAFT
        }
    }

    private fun String?.toShortDate(): String {
        val dateTime = toLocalDateTimeOrNull() ?: return ""
        return dateTime.format(DateTimeFormatter.ofPattern("MMM dd", Locale.US))
    }

    private fun String?.toLocalDateTimeOrNull(): LocalDateTime? {
        if (isNullOrBlank()) return null
        return runCatching { LocalDateTime.parse(replace(" ", "T").take(19)) }.getOrNull()
    }

    private fun sportTypeFrom(name: String?): SportType {
        val normalized = name.orEmpty().lowercase()
        return when {
            normalized in listOf("soccer", "football", "futebol") -> SportType.SOCCER
            normalized in listOf("basketball", "basquetebol") -> SportType.BASKETBALL
            normalized in listOf("paddle", "padel") -> SportType.PADDLE
            normalized in listOf("volleyball", "voleibol") -> SportType.VOLLEYBALL
            normalized == "futsal" -> SportType.FUTSAL
            else -> SportType.SOCCER
        }
    }
}
