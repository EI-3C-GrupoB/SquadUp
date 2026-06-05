package com.example.squadup.features.events.map

import com.example.squadup.core.SupabaseClientProvider
import com.example.squadup.core.enums.SportType
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class EventsMapRepository(
    private val supabaseClient: SupabaseClient = SupabaseClientProvider.client
) {
    suspend fun getMapPins(): Result<EventsMapData> {
        return try {
            val events = supabaseClient
                .from("evento")
                .select()
                .decodeList<EventsMapEventRow>()

            val registrationsByEvent = supabaseClient
                .from("inscricao")
                .select()
                .decodeList<EventsMapRegistrationRow>()
                .groupingBy { it.eventId }
                .eachCount()

            val modalities = supabaseClient
                .from("modalidade")
                .select()
                .decodeList<EventsMapModalityRow>()
                .associateBy { it.id }

            val pins = events.mapNotNull { event ->
                event.toPin(
                    registeredCount = registrationsByEvent[event.id] ?: 0,
                    modality = event.modalityId?.let { modalities[it] }
                )
            }

            Result.success(
                EventsMapData(
                    pins = pins,
                    cameraBounds = pins.toCameraBounds()
                )
            )
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }

    private fun EventsMapEventRow.toPin(
        registeredCount: Int,
        modality: EventsMapModalityRow?
    ): EventsMapPin? {
        val lat = latitude ?: return null
        val lng = longitude ?: return null
        val totalSpots = participationLimit ?: maxTeams ?: 0

        return EventsMapPin(
            eventId = id.toString(),
            title = title,
            venue = address.orEmpty(),
            latitude = lat,
            longitude = lng,
            sportType = sportTypeFrom(modality?.name),
            status = resolvePinStatus(
                rawStatus = eventStatus,
                isPrivate = isPrivate == true,
                registeredCount = registeredCount,
                totalSpots = totalSpots
            ),
            registeredCount = registeredCount,
            totalSpots = totalSpots,
            startsAt = startsAt.toDateTimeLabel()
        )
    }

    private fun resolvePinStatus(
        rawStatus: String?,
        isPrivate: Boolean,
        registeredCount: Int,
        totalSpots: Int
    ): EventsMapPinStatus {
        return when {
            rawStatus == "cancelado" -> EventsMapPinStatus.CANCELLED
            rawStatus == "terminado" -> EventsMapPinStatus.FINISHED
            isPrivate -> EventsMapPinStatus.PRIVATE
            totalSpots > 0 && registeredCount >= totalSpots -> EventsMapPinStatus.FULL
            else -> EventsMapPinStatus.AVAILABLE
        }
    }

    private fun List<EventsMapPin>.toCameraBounds(): EventsMapCameraBounds? {
        if (isEmpty()) return null

        return EventsMapCameraBounds(
            minLatitude = minOf { it.latitude },
            maxLatitude = maxOf { it.latitude },
            minLongitude = minOf { it.longitude },
            maxLongitude = maxOf { it.longitude }
        )
    }

    private fun String?.toDateTimeLabel(): String {
        val dateTime = toLocalDateTimeOrNull() ?: return ""
        return dateTime.format(DateTimeFormatter.ofPattern("MMM dd • HH:mm", Locale.US))
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

data class EventsMapData(
    val pins: List<EventsMapPin>,
    val cameraBounds: EventsMapCameraBounds?
)
