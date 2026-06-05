package com.example.squadup.features.events.moredetails

import com.example.squadup.core.SupabaseClientProvider
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class MoreDetailsRepository(
    private val supabaseClient: SupabaseClient = SupabaseClientProvider.client
) {
    suspend fun getEventDetails(eventId: String): Result<MoreDetailsUiState> {
        return try {
            val event = supabaseClient
                .from("evento")
                .select {
                    filter {
                        eq("id", eventId.toIntOrNull() ?: -1)
                    }
                }
                .decodeSingle<MoreDetailsEventRow>()

            Result.success(event.toUiState())
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }

    private fun MoreDetailsEventRow.toUiState(): MoreDetailsUiState {
        val start = startDate.toLocalDateTimeOrNull()
        val end = endDate.toLocalDateTimeOrNull()
        val hasTeamRequirement = maxTeams != null && maxTeams > 0

        return MoreDetailsUiState(
            title = title,
            date = start?.format(DateTimeFormatter.ofPattern("MMM dd, yyyy", Locale.US)).orEmpty(),
            time = listOfNotNull(
                start?.format(DateTimeFormatter.ofPattern("HH:mm")),
                end?.format(DateTimeFormatter.ofPattern("HH:mm"))
            ).joinToString(" - "),
            entryType = eventType.toEntryType(),
            teamRequirementTitle = if (hasTeamRequirement) "Team required" else "No team needed",
            teamRequirementDescription = if (hasTeamRequirement) {
                "This event accepts team registrations."
            } else {
                "This event accepts individual registrations."
            },
            rules = rules
                ?.lines()
                ?.map { it.trim() }
                ?.filter { it.isNotBlank() }
                .orEmpty(),
            venueName = address.orEmpty()
        )
    }

    private fun String?.toEntryType(): String {
        return when (this) {
            "torneio", "liga" -> "TOURNAMENT ENTRY"
            "jogo_amigavel", "treino" -> "OPEN MATCH"
            else -> "EVENT ENTRY"
        }
    }

    private fun String?.toLocalDateTimeOrNull(): LocalDateTime? {
        if (isNullOrBlank()) return null
        return runCatching { LocalDateTime.parse(replace(" ", "T").take(19)) }.getOrNull()
    }
}
