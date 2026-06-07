package com.example.squadup.features.events

import com.example.squadup.core.SupabaseClientProvider
import com.example.squadup.core.enums.SportType
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.realtime.PostgresAction
import io.github.jan.supabase.realtime.channel
import io.github.jan.supabase.realtime.postgresChangeFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onStart
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class EventsRepository(
    private val supabaseClient: SupabaseClient = SupabaseClientProvider.client
) {
    fun getEventsRealtime(): Flow<EventsData> = flow {
        val currentUser = getCurrentUserRow()

        if (currentUser == null) {
            emit(EventsData.empty())
            return@flow
        }

        val channelSuffix = "${currentUser.id}_${System.currentTimeMillis()}"

        val eventsChannel = supabaseClient.channel("events_evento_$channelSuffix")
        val registrationsChannel = supabaseClient.channel("events_inscricao_$channelSuffix")
        val gamesChannel = supabaseClient.channel("events_jogo_$channelSuffix")
        val usersChannel = supabaseClient.channel("events_utilizador_$channelSuffix")
        val locationsChannel = supabaseClient.channel("events_localizacao_$channelSuffix")

        val eventsChanges = eventsChannel
            .postgresChangeFlow<PostgresAction>(schema = "public") {
                table = "evento"
            }
            .map { Unit }

        val registrationsChanges = registrationsChannel
            .postgresChangeFlow<PostgresAction>(schema = "public") {
                table = "inscricao"
            }
            .map { Unit }

        val gamesChanges = gamesChannel
            .postgresChangeFlow<PostgresAction>(schema = "public") {
                table = "jogo"
            }
            .map { Unit }

        val usersChanges = usersChannel
            .postgresChangeFlow<PostgresAction>(schema = "public") {
                table = "utilizador"
            }
            .map { Unit }

        val locationsChanges = locationsChannel
            .postgresChangeFlow<PostgresAction>(schema = "public") {
                table = "localizacao"
            }
            .map { Unit }

        eventsChannel.subscribe()
        registrationsChannel.subscribe()
        gamesChannel.subscribe()
        usersChannel.subscribe()
        locationsChannel.subscribe()

        emitAll(
            merge(
                eventsChanges,
                registrationsChanges,
                gamesChanges,
                usersChanges,
                locationsChanges
            )
                .onStart {
                    emit(Unit)
                }
                .map {
                    loadNearbyEvents(currentUser.id).getOrElse {
                        EventsData.empty()
                    }
                }
        )
    }

    suspend fun getEvents(): Result<EventsData> {
        val currentUser = getCurrentUserRow()
            ?: return Result.success(EventsData.empty())

        return loadNearbyEvents(currentUser.id)
    }

    private suspend fun loadNearbyEvents(userId: Int): Result<EventsData> {
        return try {
            val events = supabaseClient
                .postgrest
                .rpc(
                    function = "get_nearby_events",
                    parameters = buildJsonObject {
                        put("p_user_id", userId)
                    }
                )
                .decodeList<EventsNearbyEventRow>()
                .sortedWith(
                    compareBy<EventsNearbyEventRow> {
                        it.distanceKm ?: Double.MAX_VALUE
                    }.thenBy {
                        it.startDate.orEmpty()
                    }
                )

            Result.success(
                EventsData(
                    featuredEvent = events.firstOrNull()?.toFeaturedEvent(),
                    upcomingEvents = events.take(3).map { event ->
                        event.toUpcomingEvent()
                    },
                    browseEvents = events.map { event ->
                        event.toBrowseEvent()
                    }
                )
            )
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }

    private suspend fun getCurrentUserRow(): EventsCurrentUserRow? {
        val authUserId = supabaseClient.auth.currentUserOrNull()?.id ?: return null

        return runCatching {
            supabaseClient
                .postgrest
                .from("utilizador")
                .select {
                    filter {
                        eq("auth_user_id", authUserId)
                    }
                }
                .decodeSingle<EventsCurrentUserRow>()
        }.getOrNull()
    }

    private fun EventsNearbyEventRow.toFeaturedEvent(): FeaturedEventItem {
        return FeaturedEventItem(
            id = id.toString(),
            seriesName = formatName ?: eventStatus.toEntryType(),
            title = title,
            dateTime = startDate.toDateTimeLabel(),
            venue = address.orEmpty(),
            sportType = sportTypeFrom(modalityName),
            distance = distanceKm.toDistanceLabel(),
            distanceKm = distanceKm,
            imageUrl = imageUrl
        )
    }

    private fun EventsNearbyEventRow.toUpcomingEvent(): UpcomingEventItem {
        val parsedDate = startDate.toLocalDateTimeOrNull()

        return UpcomingEventItem(
            id = id.toString(),
            month = parsedDate
                ?.format(DateTimeFormatter.ofPattern("MMM", Locale.US))
                ?.uppercase()
                .orEmpty(),
            day = parsedDate?.dayOfMonth?.toString().orEmpty(),
            title = title,
            sportType = sportTypeFrom(modalityName),
            time = parsedDate
                ?.format(DateTimeFormatter.ofPattern("HH:mm"))
                .orEmpty(),
            distance = distanceKm.toDistanceLabel(),
            distanceKm = distanceKm,
            imageUrl = imageUrl
        )
    }

    private fun EventsNearbyEventRow.toBrowseEvent(): BrowseEventItem {
        val registered = registeredTeams?.toInt() ?: 0
        val totalSpots = participationLimit ?: maxTeams ?: 0

        return BrowseEventItem(
            id = id.toString(),
            title = title,
            price = priceLabel(price ?: entryFee, currency),
            dateTime = startDate.toDateTimeLabel(),
            venue = address.orEmpty(),
            sportType = sportTypeFrom(modalityName),
            entryType = formatName ?: eventStatus.toEntryType(),
            requiresTeam = maxTeams != null,
            spotsLeft = (totalSpots - registered).coerceAtLeast(0),
            totalSpots = totalSpots,
            registeredTeams = registered,
            distance = distanceKm.toDistanceLabel(),
            distanceKm = distanceKm,
            latitude = latitude,
            longitude = longitude,
            imageUrl = imageUrl
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
        return if (this % 1.0 == 0.0) {
            toInt().toString()
        } else {
            "%.2f".format(Locale.US, this)
        }
    }

    private fun Double?.toDistanceLabel(): String {
        val distance = this ?: return ""

        return when {
            distance < 1.0 -> "${(distance * 1000).toInt()} m"
            distance < 10.0 -> "%.1f km".format(Locale.US, distance)
            else -> "${distance.toInt()} km"
        }
    }

    private fun String?.toEntryType(): String {
        return when (this) {
            "torneio", "liga" -> "TOURNAMENT ENTRY"
            "jogo_amigavel", "treino" -> "OPEN MATCH"
            "rascunho" -> "DRAFT"
            "inscricoes_abertas" -> "OPEN REGISTRATION"
            "inscricoes_fechadas" -> "REGISTRATION CLOSED"
            "em_curso" -> "LIVE EVENT"
            "terminado" -> "FINISHED"
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
        return runCatching {
            LocalDateTime.parse(normalized)
        }.getOrNull()
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
) {
    companion object {
        fun empty(): EventsData {
            return EventsData(
                featuredEvent = null,
                upcomingEvents = emptyList(),
                browseEvents = emptyList()
            )
        }
    }
}
