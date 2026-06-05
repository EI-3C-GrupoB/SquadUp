package com.example.squadup.features.events

import com.example.squadup.core.SupabaseClientProvider
import com.example.squadup.core.enums.SportType
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class EventsRepository(
    private val supabaseClient: SupabaseClient = SupabaseClientProvider.client
) {
    suspend fun getEvents(): Result<EventsData> {
        return try {
            val events = supabaseClient
                .from("evento")
                .select()
                .decodeList<EventsEventRow>()
                .filterNot { it.eventStatus == "cancelado" }
                .sortedBy { it.startDate.orEmpty() }

            val modalities = supabaseClient
                .from("modalidade")
                .select()
                .decodeList<EventsModalityRow>()
                .associateBy { it.id }

            val registrations = supabaseClient
                .from("inscricao")
                .select()
                .decodeList<EventsRegistrationRow>()
                .groupingBy { it.eventId }
                .eachCount()

            Result.success(
                EventsData(
                    featuredEvent = events.firstOrNull()?.toFeaturedEvent(modalities),
                    upcomingEvents = events.take(3).map { it.toUpcomingEvent(modalities) },
                    browseEvents = events.map { event ->
                        event.toBrowseEvent(
                            modality = event.modalityId?.let { modalities[it] },
                            registrationsCount = registrations[event.id] ?: 0
                        )
                    }
                )
            )
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }

    private fun EventsEventRow.toFeaturedEvent(
        modalities: Map<Int, EventsModalityRow>
    ): FeaturedEventItem {
        return FeaturedEventItem(
            id = id.toString(),
            seriesName = eventType.toEntryType(),
            title = title,
            dateTime = startDate.toDateTimeLabel(),
            venue = address.orEmpty(),
            sportType = sportTypeFrom(modalityId?.let { modalities[it]?.name })
        )
    }

    private fun EventsEventRow.toUpcomingEvent(
        modalities: Map<Int, EventsModalityRow>
    ): UpcomingEventItem {
        val parsedDate = startDate.toLocalDateTimeOrNull()

        return UpcomingEventItem(
            id = id.toString(),
            month = parsedDate?.format(DateTimeFormatter.ofPattern("MMM", Locale.US))?.uppercase()
                ?: "",
            day = parsedDate?.dayOfMonth?.toString().orEmpty(),
            title = title,
            sportType = sportTypeFrom(modalityId?.let { modalities[it]?.name }),
            time = parsedDate?.format(DateTimeFormatter.ofPattern("HH:mm")).orEmpty()
        )
    }

    private fun EventsEventRow.toBrowseEvent(
        modality: EventsModalityRow?,
        registrationsCount: Int
    ): BrowseEventItem {
        val totalSpots = participationLimit ?: maxTeams ?: 0

        return BrowseEventItem(
            id = id.toString(),
            title = title,
            price = priceLabel(price ?: entryFee, currency),
            dateTime = startDate.toDateTimeLabel(),
            venue = address.orEmpty(),
            sportType = sportTypeFrom(modality?.name),
            entryType = eventType.toEntryType(),
            requiresTeam = maxTeams != null,
            spotsLeft = (totalSpots - registrationsCount).coerceAtLeast(0),
            totalSpots = totalSpots
        )
    }

    private fun priceLabel(value: Double?, currency: String?): String {
        if (value == null || value == 0.0) return "Free"

        val symbol = when (currency?.uppercase()) {
            "EUR", null -> "€"
            "USD" -> "$"
            "GBP" -> "£"
            else -> "${currency.uppercase()} "
        }

        return "$symbol${value.toIntIfWhole()}"
    }

    private fun Double.toIntIfWhole(): String {
        return if (this % 1.0 == 0.0) toInt().toString() else "%.2f".format(Locale.US, this)
    }

    private fun String?.toEntryType(): String {
        return when (this) {
            "torneio", "liga" -> "TOURNAMENT ENTRY"
            "jogo_amigavel", "treino" -> "OPEN MATCH"
            else -> "EVENT ENTRY"
        }
    }

    private fun String?.toDateTimeLabel(): String {
        val dateTime = toLocalDateTimeOrNull() ?: return ""
        return dateTime.format(DateTimeFormatter.ofPattern("MMM dd • HH:mm", Locale.US))
    }

    private fun String?.toLocalDateTimeOrNull(): LocalDateTime? {
        if (isNullOrBlank()) return null

        val normalized = replace(" ", "T").take(19)
        return runCatching { LocalDateTime.parse(normalized) }.getOrNull()
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

data class EventsData(
    val featuredEvent: FeaturedEventItem?,
    val upcomingEvents: List<UpcomingEventItem>,
    val browseEvents: List<BrowseEventItem>
)
