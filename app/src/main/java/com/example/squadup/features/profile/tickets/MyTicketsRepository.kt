package com.example.squadup.features.profile.tickets

import com.example.squadup.core.SupabaseClientProvider
import com.example.squadup.core.enums.SportType
import com.example.squadup.core.enums.TicketStatus
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class MyTicketsRepository(
    private val supabaseClient: SupabaseClient = SupabaseClientProvider.client
) {
    suspend fun getMyTickets(): Result<MyTicketsUiState> {
        return try {
            val user = getCurrentUserRow() ?: return Result.success(MyTicketsUiState())
            val tickets = supabaseClient
                .from("bilhete")
                .select {
                    filter {
                        eq("user_id", user.id)
                    }
                }
                .decodeList<MyTicketsTicketRow>()
            val events = supabaseClient
                .from("evento")
                .select()
                .decodeList<MyTicketsEventRow>()
                .associateBy { it.id }
            val modalities = supabaseClient
                .from("modalidade")
                .select()
                .decodeList<MyTicketsModalityRow>()
                .associateBy { it.id }

            val now = LocalDateTime.now()
            val upcoming = mutableListOf<MyTicketItem>()
            val past = mutableListOf<MyPastTicketItem>()

            tickets.forEach { ticket ->
                val event = events[ticket.eventId] ?: return@forEach
                val sportType = sportTypeFrom(event.modalityId?.let { modalities[it]?.name })
                val startDate = event.startDate.toLocalDateTimeOrNull()
                val endDate = event.endDate.toLocalDateTimeOrNull()
                val cutoffDate = endDate ?: startDate
                val ticketStatus = ticket.status.toTicketStatus(cutoffDate)

                if (cutoffDate == null || cutoffDate.isAfter(now)) {
                    upcoming += MyTicketItem(
                        id = ticket.id.toString(),
                        title = event.title,
                        dateTime = startDate.toTicketDateTime(),
                        location = event.address.orEmpty(),
                        sportType = sportType,
                        status = ticketStatus
                    )
                } else {
                    past += MyPastTicketItem(
                        id = ticket.id.toString(),
                        title = event.title,
                        status = ticketStatus,
                        date = (cutoffDate).toTicketDate(),
                        location = event.address.orEmpty(),
                        sportType = sportType
                    )
                }
            }

            Result.success(
                MyTicketsUiState(
                    upcomingTickets = upcoming,
                    pastTickets = past
                )
            )
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }

    private suspend fun getCurrentUserRow(): MyTicketsUserRow? {
        val authUserId = supabaseClient.auth.currentUserOrNull()?.id ?: return null
        return runCatching {
            supabaseClient
                .from("utilizador")
                .select {
                    filter {
                        eq("auth_user_id", authUserId)
                    }
                }
                .decodeSingle<MyTicketsUserRow>()
        }.getOrNull()
    }

    private fun String?.toTicketStatus(eventDate: LocalDateTime?): TicketStatus {
        return when (this) {
            "cancelado" -> TicketStatus.CANCELLED
            "usado" -> TicketStatus.PAST
            else -> if (eventDate != null && eventDate.isBefore(LocalDateTime.now())) {
                TicketStatus.EXPIRED
            } else {
                TicketStatus.CONFIRMED
            }
        }
    }

    private fun LocalDateTime?.toTicketDateTime(): String {
        return this?.format(DateTimeFormatter.ofPattern("dd MMM • HH:mm", Locale("pt", "PT"))).orEmpty()
    }

    private fun LocalDateTime.toTicketDate(): String {
        return format(DateTimeFormatter.ofPattern("dd MMM yyyy", Locale("pt", "PT")))
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
