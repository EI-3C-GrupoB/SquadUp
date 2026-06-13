package com.example.squadup.features.events

import com.example.squadup.core.SupabaseClientProvider
import com.example.squadup.core.enums.SportType
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.realtime.PostgresAction
import io.github.jan.supabase.realtime.channel
import io.github.jan.supabase.realtime.postgresChangeFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onStart
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import com.example.squadup.core.enums.EventParticipationType
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.getOrElse

class EventsRepository(
    private val supabaseClient: SupabaseClient = SupabaseClientProvider.client
) {
    fun getEventsRealtime(
        latitude: Double,
        longitude: Double
    ): Flow<EventsData> = flow {
        val currentUser = getCurrentUserRow()

        if (currentUser == null) {
            emit(EventsData.empty())
            return@flow
        }

        emitAll(
            observeEventsTables(currentUser.id)
                .map {
                    loadNearbyEventsFromDeviceLocation(
                        userId = currentUser.id,
                        latitude = latitude,
                        longitude = longitude
                    ).getOrElse {
                        EventsData.empty()
                    }
                }
        )
    }

    fun getEventsRealtimeFromProfileLocation(): Flow<EventsData> = flow {
        val currentUser = getCurrentUserRow()

        if (currentUser == null) {
            emit(EventsData.empty())
            return@flow
        }

        emitAll(
            observeEventsTables(currentUser.id)
                .map {
                    loadNearbyEventsFromProfileLocation(
                        userId = currentUser.id
                    ).getOrElse {
                        EventsData.empty()
                    }
                }
        )
    }

    suspend fun getEvents(
        latitude: Double,
        longitude: Double
    ): Result<EventsData> {
        val currentUser = getCurrentUserRow()
            ?: return Result.success(EventsData.empty())

        return loadNearbyEventsFromDeviceLocation(
            userId = currentUser.id,
            latitude = latitude,
            longitude = longitude
        )
    }

    private fun observeEventsTables(userId: Int): Flow<Unit> = flow {
        val channelSuffix = "${userId}_${System.currentTimeMillis()}"

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
            ).onStart {
                emit(Unit)
            }
        )
    }

    private suspend fun loadNearbyEventsFromDeviceLocation(
        userId: Int,
        latitude: Double,
        longitude: Double
    ): Result<EventsData> {
        return try {
            val events = supabaseClient
                .postgrest
                .rpc(
                    function = "get_nearby_events_by_location",
                    parameters = buildJsonObject {
                        put("p_user_id", userId)
                        put("p_latitude", latitude)
                        put("p_longitude", longitude)
                    }
                )
                .decodeList<EventsNearbyEventRow>()
                .sortedByDistanceAndDate()

            Result.success(events.toEventsData(currentUserId = userId))
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }

    private suspend fun loadNearbyEventsFromProfileLocation(
        userId: Int
    ): Result<EventsData> {
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
                .sortedByDistanceAndDate()

            Result.success(events.toEventsData(currentUserId = userId))
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }

    suspend fun findEventByAccessCode(code: String): Result<BrowseEventItem?> {
        return try {
            val row = supabaseClient
                .postgrest
                .from("evento")
                .select {
                    filter { eq("codigo_acesso", code.trim().uppercase()) }
                }
                .decodeList<EventsNearbyEventRow>()
                .firstOrNull()
            Result.success(row?.toBrowseEvent())
        } catch (e: Exception) {
            Result.failure(e)
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

    private fun List<EventsNearbyEventRow>.sortedByDistanceAndDate(): List<EventsNearbyEventRow> {
        return sortedWith(
            compareBy<EventsNearbyEventRow> {
                it.distanceKm ?: Double.MAX_VALUE
            }.thenBy {
                it.startDate.orEmpty()
            }
        )
    }

    private fun List<EventsNearbyEventRow>.sortedByFeaturedPriority(): List<EventsNearbyEventRow> {
        return sortedWith(
            compareBy<EventsNearbyEventRow> {
                it.startDate.toLocalDateTimeOrNull() ?: LocalDateTime.MAX
            }.thenBy {
                it.distanceKm ?: Double.MAX_VALUE
            }
        )
    }

    private suspend fun loadAccessiblePrivateEvents(userId: Int): List<EventsNearbyEventRow> {
        val modalities = runCatching {
            supabaseClient.postgrest.from("modalidade")
                .select()
                .decodeList<EventsModalityRow>()
                .associateBy { it.id }
        }.getOrElse { emptyMap() }

        val acceptedEventIds = getAcceptedPrivateEventIds(userId)

        // Events created by the user OR in acceptedEventIds
        val createdByUser = runCatching {
            supabaseClient.postgrest.from("evento")
                .select {
                    filter {
                        eq("criador_id", userId)
                        eq("is_private", true)
                    }
                }
                .decodeList<EventsPrivateEventRow>()
        }.getOrElse { emptyList() }

        val acceptedPrivate = if (acceptedEventIds.isNotEmpty()) {
            runCatching {
                supabaseClient.postgrest.from("evento")
                    .select {
                        filter {
                            isIn("id", acceptedEventIds.toList())
                            eq("is_private", true)
                        }
                    }
                    .decodeList<EventsPrivateEventRow>()
            }.getOrElse { emptyList() }
        } else emptyList()

        return (createdByUser + acceptedPrivate)
            .distinctBy { it.id }
            .map { it.toNearbyEventRow(modalities[it.modalityId]?.name) }
    }

    private suspend fun getAcceptedPrivateEventIds(userId: Int): Set<Int> {
        val result = mutableSetOf<Int>()

        // Individual registrations accepted by organizer
        runCatching {
            supabaseClient.postgrest.from("inscricao")
                .select {
                    filter {
                        eq("user_id", userId)
                        eq("estado_inscricao", "aceite")
                    }
                }
                .decodeList<EventsInscricaoRow>()
                .mapNotNull { it.eventoId }
        }.getOrElse { emptyList() }.forEach { result.add(it) }

        // Team registrations: find user's teams, then check if any team has a confirmed evento_equipa
        val teamIds = runCatching {
            supabaseClient.postgrest.from("inscricao")
                .select {
                    filter {
                        eq("user_id", userId)
                        exact("evento_id", null)
                    }
                }
                .decodeList<EventsInscricaoRow>()
                .mapNotNull { it.equipaId }
        }.getOrElse { emptyList() }

        if (teamIds.isNotEmpty()) {
            runCatching {
                supabaseClient.postgrest.from("evento_equipa")
                    .select {
                        filter {
                            isIn("equipa_id", teamIds)
                            eq("estado", "confirmada")
                        }
                    }
                    .decodeList<EventsEventoEquipaRow>()
                    .mapNotNull { it.eventoId }
            }.getOrElse { emptyList() }.forEach { result.add(it) }
        }

        return result
    }

    private suspend fun List<EventsNearbyEventRow>.toEventsData(currentUserId: Int): EventsData {
        // RPC may exclude private events — fetch them separately and merge
        val privateEvents = loadAccessiblePrivateEvents(currentUserId)
        val rpcIds = map { it.id }.toSet()
        val merged = this + privateEvents.filter { it.id !in rpcIds }
        val visible = merged.filter { event ->
            event.isPrivate != true || event.creatorId == currentUserId || event.id in privateEvents.map { it.id }.toSet()
        }
        val orderedForBrowse = visible.sortedByDistanceAndDate()
        val orderedForFeatured = visible.sortedByFeaturedPriority()

        return EventsData(
            featuredEvent = orderedForFeatured.firstOrNull()?.toFeaturedEvent(),
            upcomingEvents = orderedForFeatured.take(3).map { event ->
                event.toUpcomingEvent()
            },
            browseEvents = orderedForBrowse.map { event ->
                event.toBrowseEvent()
            }
        )
    }

    private fun EventsNearbyEventRow.toFeaturedEvent(): FeaturedEventItem {
        return FeaturedEventItem(
            id = id.toString(),
            seriesName = formatName ?: eventStatus.toEntryType(),
            title = title,
            dateTime = startDate.toDateTimeLabel(),
            venue = address.toShortVenue(maxLength = 44),
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
        val parsedParticipationType = EventParticipationType.fromDbValue(participationType)
        val totalSpots = when (parsedParticipationType) {
            EventParticipationType.TEAM -> maxTeams ?: 0
            EventParticipationType.INDIVIDUAL -> participationLimit ?: 0
            EventParticipationType.INDIVIDUAL_AND_TEAM -> maxTeams ?: participationLimit ?: 0
        }
        val spotsLeft = (totalSpots - registered).coerceAtLeast(0)
        val full = totalSpots > 0 && spotsLeft == 0

        return BrowseEventItem(
            id = id.toString(),
            title = title,
            price = priceLabel(price ?: entryFee, currency),
            dateTime = startDate.toDateTimeLabel(),
            venue = address.toShortVenue(maxLength = 62),
            sportType = sportTypeFrom(modalityName),
            entryType = formatName ?: eventStatus.toEntryType(),
            requiresTeam = parsedParticipationType != EventParticipationType.INDIVIDUAL,
            spotsLeft = spotsLeft,
            totalSpots = totalSpots,
            registeredTeams = registered,
            distance = distanceKm.toDistanceLabel(),
            distanceKm = distanceKm,
            latitude = latitude,
            longitude = longitude,
            imageUrl = imageUrl,
            participationTypeLabel = parsedParticipationType.toLabel(),
            formatLabel = formatName.orEmpty(),
            isPrivate = isPrivate == true,
            eventStatusLabel = eventStatus.toEventStatusLabel(),
            registrationStatusLabel = resolveRegistrationStatusLabel(
                regStart = registrationStartDate,
                regEnd = registrationEndDate,
                isFull = full
            ),
            isFull = full
        )
    }

    private fun EventParticipationType.toLabel(): String = when (this) {
        EventParticipationType.INDIVIDUAL -> "Individual"
        EventParticipationType.TEAM -> "Equipas"
        EventParticipationType.INDIVIDUAL_AND_TEAM -> "Individual + Equipas"
    }

    private fun String?.toEventStatusLabel(): String = when (this) {
        "a_decorrer" -> "A decorrer"
        "terminado" -> "Terminado"
        "cancelado" -> "Cancelado"
        "rascunho" -> "Rascunho"
        else -> ""
    }

    private fun resolveRegistrationStatusLabel(
        regStart: String?,
        regEnd: String?,
        isFull: Boolean
    ): String {
        if (isFull) return "Cheio"
        val now = LocalDateTime.now()
        val start = regStart.toLocalDateTimeOrNull()
        val end = regEnd.toLocalDateTimeOrNull()
        return when {
            start != null && now.isBefore(start) -> "Inscrições não abertas"
            end != null && now.isAfter(end) -> "Inscrições encerradas"
            else -> ""
        }
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
        return when (this?.lowercase()) {
            "publicado" -> "OPEN MATCH"
            "ativo" -> "OPEN MATCH"
            "em_curso" -> "LIVE EVENT"
            "terminado" -> "FINISHED"
            else -> "OPEN MATCH"
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

    private fun String?.toShortVenue(maxLength: Int = 48): String {
        val value = this
            ?.replace("\n", " ")
            ?.replace(Regex("\\s+"), " ")
            ?.trim()
            .orEmpty()

        if (value.length <= maxLength) return value

        return value
            .take(maxLength)
            .trimEnd(',', '.', ' ')
            .plus("...")
    }
}
