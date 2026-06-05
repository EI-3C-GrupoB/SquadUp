package com.example.squadup.features.profile.tickets.details

import com.example.squadup.core.SupabaseClientProvider
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class TicketDetailsRepository(
    private val supabaseClient: SupabaseClient = SupabaseClientProvider.client
) {
    suspend fun getTicketDetails(ticketId: String): Result<TicketDetailsUiState> {
        return try {
            val ticket = supabaseClient
                .from("bilhete")
                .select {
                    filter {
                        eq("id", ticketId.toIntOrNull() ?: -1)
                    }
                }
                .decodeSingle<TicketDetailsTicketRow>()
            val event = supabaseClient
                .from("evento")
                .select {
                    filter {
                        eq("id", ticket.eventId)
                    }
                }
                .decodeSingle<TicketDetailsEventRow>()
            val modality = event.modalityId?.let { getModality(it) }
            val eventDate = event.startDate.toLocalDateTimeOrNull()

            Result.success(
                TicketDetailsUiState(
                    title = event.title,
                    ticketType = "${ticket.status.toTicketType()} • ${modality?.name.orEmpty()}",
                    dateTime = eventDate?.format(DateTimeFormatter.ofPattern("MMM dd, yyyy • HH:mm", Locale.US)).orEmpty(),
                    locationName = event.address.orEmpty(),
                    locationDetail = "Ticket #${ticket.id}"
                )
            )
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }

    private suspend fun getModality(modalityId: Int): TicketDetailsModalityRow? {
        return runCatching {
            supabaseClient
                .from("modalidade")
                .select {
                    filter {
                        eq("id", modalityId)
                    }
                }
                .decodeSingle<TicketDetailsModalityRow>()
        }.getOrNull()
    }

    private fun String?.toTicketType(): String {
        return when (this) {
            "usado" -> "Used ticket"
            "cancelado" -> "Cancelled ticket"
            else -> "Standard Entry"
        }
    }

    private fun String?.toLocalDateTimeOrNull(): LocalDateTime? {
        if (isNullOrBlank()) return null
        return runCatching { LocalDateTime.parse(replace(" ", "T").take(19)) }.getOrNull()
    }
}
