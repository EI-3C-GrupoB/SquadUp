package com.example.squadup.features.events.manageevent

import com.example.squadup.core.SupabaseClientProvider
import com.example.squadup.core.enums.EventStatus
import com.example.squadup.core.enums.GameStatus
import com.example.squadup.core.enums.PlayerValidationState
import com.example.squadup.core.enums.SportType
import com.example.squadup.core.enums.TeamEventStatus
import io.github.jan.supabase.SupabaseClient
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
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class ManageEventRepository(
    private val supabaseClient: SupabaseClient = SupabaseClientProvider.client
) {
    fun observeEvent(eventId: String): Flow<ManageEventUiState> = flow {
        val parsedEventId = eventId.toIntOrNull()

        if (parsedEventId == null) {
            emit(
                ManageEventUiState(
                    errorMessage = "Evento inválido."
                )
            )
            return@flow
        }

        emitAll(
            observeEventTables(parsedEventId)
                .map {
                    getEvent(eventId).getOrElse { exception ->
                        ManageEventUiState(
                            eventId = eventId,
                            errorMessage = exception.message ?: "Não foi possível carregar o evento."
                        )
                    }
                }
        )
    }

    suspend fun getEvent(eventId: String): Result<ManageEventUiState> {
        return try {
            val event = supabaseClient
                .from("evento")
                .select {
                    filter {
                        eq("id", eventId.toIntOrNull() ?: -1)
                    }
                }
                .decodeSingle<ManageEventRow>()

            val modality = event.modalityId?.let { getModality(it) }
            val registrations = getRegistrations(event.id)
            val eventTeamRegistrations = getEventTeamRegistrations(event.id)
            val teams = supabaseClient.from("equipa").select().decodeList<ManageEventTeamRow>()
            val users = supabaseClient.from("utilizador").select().decodeList<ManageEventUserRow>()
            val games = getGames(event.id)
            val gameIds = games.map { it.id }.toSet()
            val gameTeams = supabaseClient
                .from("jogo_equipa")
                .select()
                .decodeList<ManageEventGameTeamRow>()
                .filter { it.gameId in gameIds }
            val actions = supabaseClient.from("tipo_acao").select().decodeList<ManageEventActionTypeRow>()
            val timeline = supabaseClient
                .from("registo_timeline")
                .select()
                .decodeList<ManageEventTimelineRow>()
                .filter { it.gameId in gameIds }

            Result.success(
                event.toUiState(
                    modality = modality,
                    registrations = registrations,
                    eventTeamRegistrations = eventTeamRegistrations,
                    teams = teams,
                    users = users,
                    games = games,
                    gameTeams = gameTeams,
                    timeline = timeline,
                    actions = actions
                )
            )
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }

    suspend fun getPendingIndividualRegistrations(
        eventId: String
    ): Result<List<ManageEventRegistrationRow>> {
        return try {
            val parsedEventId = eventId.toIntOrNull()
                ?: throw IllegalArgumentException("Evento inválido.")

            val registrations = supabaseClient
                .from("inscricao")
                .select {
                    filter {
                        eq("evento_id", parsedEventId)
                        eq("tipo_inscricao", "evento_individual")
                        eq("estado_inscricao", "pendente")
                    }
                }
                .decodeList<ManageEventRegistrationRow>()

            Result.success(registrations)
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }

    suspend fun acceptIndividualRegistration(registrationId: Int): Result<Unit> {
        return updateIndividualRegistrationStatus(
            registrationId = registrationId,
            status = "aceite"
        )
    }

    suspend fun rejectIndividualRegistration(registrationId: Int): Result<Unit> {
        return updateIndividualRegistrationStatus(
            registrationId = registrationId,
            status = "recusada"
        )
    }

    suspend fun acceptTeamRegistration(registrationId: Int): Result<Unit> {
        return updateTeamRegistrationRequest(
            registrationId = registrationId,
            registrationStatus = "aceite",
            registrationType = "evento_equipa",
            eventTeamStatus = "confirmada"
        )
    }

    suspend fun rejectTeamRegistration(registrationId: Int): Result<Unit> {
        return updateTeamRegistrationRequest(
            registrationId = registrationId,
            registrationStatus = "recusada",
            registrationType = "pedido_evento_equipa",
            eventTeamStatus = "recusada"
        )
    }

    suspend fun updateStatus(eventId: String, status: EventStatus): Result<Unit> {
        return try {
            supabaseClient
                .from("evento")
                .update(ManageEventStatusUpdateRow(status.toDatabaseValue())) {
                    filter {
                        eq("id", eventId.toIntOrNull() ?: -1)
                    }
                }
            Result.success(Unit)
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }

    private suspend fun updateIndividualRegistrationStatus(
        registrationId: Int,
        status: String
    ): Result<Unit> {
        return try {
            supabaseClient
                .from("inscricao")
                .update(ManageEventRegistrationStatusUpdateRow(status)) {
                    filter {
                        eq("id", registrationId)
                        eq("tipo_inscricao", "evento_individual")
                    }
                }
            Result.success(Unit)
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }

    private suspend fun updateTeamRegistrationRequest(
        registrationId: Int,
        registrationStatus: String,
        registrationType: String,
        eventTeamStatus: String
    ): Result<Unit> {
        return try {
            val eventTeamRegistration = supabaseClient
                .from("evento_equipa")
                .select {
                    filter {
                        eq("id", registrationId)
                    }
                }
                .decodeSingle<ManageEventTeamRegistrationRow>()

            supabaseClient
                .from("evento_equipa")
                .update(ManageEventEventTeamStatusUpdateRow(eventTeamStatus)) {
                    filter {
                        eq("id", eventTeamRegistration.id)
                    }
                }

            supabaseClient
                .from("inscricao")
                .update(
                    ManageEventTeamRegistrationStatusUpdateRow(
                        status = registrationStatus,
                        registrationType = registrationType
                    )
                ) {
                    filter {
                        eq("evento_id", eventTeamRegistration.eventId)
                        eq("equipa_id", eventTeamRegistration.teamId)
                        eq("user_id", eventTeamRegistration.captainUserId ?: -1)
                        eq("tipo_inscricao", "pedido_evento_equipa")
                    }
                }

            Result.success(Unit)
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }

    private fun observeEventTables(eventId: Int): Flow<Unit> = flow {
        val channelSuffix = "${eventId}_${System.currentTimeMillis()}"

        val eventChannel = supabaseClient.channel("manage_event_evento_$channelSuffix")
        val registrationsChannel = supabaseClient.channel("manage_event_inscricao_$channelSuffix")
        val eventTeamsChannel = supabaseClient.channel("manage_event_evento_equipa_$channelSuffix")

        val eventChanges = eventChannel
            .postgresChangeFlow<PostgresAction>(schema = "public") {
                table = "evento"
            }
            .map { Unit }

        val registrationChanges = registrationsChannel
            .postgresChangeFlow<PostgresAction>(schema = "public") {
                table = "inscricao"
            }
            .map { Unit }

        val eventTeamChanges = eventTeamsChannel
            .postgresChangeFlow<PostgresAction>(schema = "public") {
                table = "evento_equipa"
            }
            .map { Unit }

        eventChannel.subscribe()
        registrationsChannel.subscribe()
        eventTeamsChannel.subscribe()

        emitAll(
            merge(
                eventChanges,
                registrationChanges,
                eventTeamChanges
            ).onStart {
                emit(Unit)
            }
        )
    }

    private suspend fun getModality(modalityId: Int): ManageEventModalityRow? {
        return runCatching {
            supabaseClient
                .from("modalidade")
                .select {
                    filter {
                        eq("id", modalityId)
                    }
                }
                .decodeSingle<ManageEventModalityRow>()
        }.getOrNull()
    }

    private suspend fun getRegistrations(eventId: Int): List<ManageEventRegistrationRow> {
        return supabaseClient
            .from("inscricao")
            .select {
                filter {
                    eq("evento_id", eventId)
                }
            }
            .decodeList()
    }

    private suspend fun getEventTeamRegistrations(eventId: Int): List<ManageEventTeamRegistrationRow> {
        return supabaseClient
            .from("evento_equipa")
            .select {
                filter {
                    eq("evento_id", eventId)
                }
            }
            .decodeList()
    }

    private suspend fun getGames(eventId: Int): List<ManageEventGameRow> {
        return supabaseClient
            .from("jogo")
            .select {
                filter {
                    eq("evento_id", eventId)
                }
            }
            .decodeList()
    }

    private fun ManageEventRow.toUiState(
        modality: ManageEventModalityRow?,
        registrations: List<ManageEventRegistrationRow>,
        eventTeamRegistrations: List<ManageEventTeamRegistrationRow>,
        teams: List<ManageEventTeamRow>,
        users: List<ManageEventUserRow>,
        games: List<ManageEventGameRow>,
        gameTeams: List<ManageEventGameTeamRow>,
        timeline: List<ManageEventTimelineRow>,
        actions: List<ManageEventActionTypeRow>
    ): ManageEventUiState {
        val usersById = users.associateBy { it.id }
        val teamsById = teams.associateBy { it.id }
        val confirmedEventTeamRegistrations = eventTeamRegistrations.filter { it.status == "confirmada" }
        val pendingEventTeamRegistrations = eventTeamRegistrations.filter { it.status == "pendente" }
        val confirmedEventTeamIds = confirmedEventTeamRegistrations.map { it.teamId }.toSet()
        val confirmedTeamRegistrations = registrations.filter {
            it.teamId != null &&
                    (
                            it.registrationType == "evento_equipa" &&
                                    it.status == "aceite" ||
                                    it.teamId in confirmedEventTeamIds
                            )
        }
        val pendingTeamRegistrations = registrations.filter {
            it.teamId != null &&
                    it.registrationType == "pedido_evento_equipa" &&
                    it.status == "pendente"
        }
        val individualRegistrations = registrations.filter {
            it.teamId == null &&
                    it.userId != null &&
                    it.registrationType == "evento_individual"
        }
        val acceptedIndividualRegistrations = individualRegistrations.filter { it.status == "aceite" }
        val pendingIndividualRegistrations = individualRegistrations.filter { it.status == "pendente" }
        val waitlistRegistrations = registrations.filter { registration ->
            registration.status == "pendente" &&
                    registration.registrationType != "evento_individual" &&
                    registration.registrationType != "pedido_evento_equipa"
        }
        val confirmedTeamIds = (confirmedEventTeamIds + confirmedTeamRegistrations.mapNotNull { it.teamId }).toSet()
        val sportType = sportTypeFrom(modality?.name)
        val scheduledGames = games.map { game ->
            game.toScheduledGame(
                gameTeams = gameTeams.filter { it.gameId == game.id },
                teamsById = teamsById,
                sportType = sportType
            )
        }
        val actionById = actions.associateBy { it.id }
        val scorerCounts = timeline
            .filter { row -> row.actionTypeId?.let { actionById[it]?.name?.isScoreAction() } == true }
            .groupingBy { it.userId }
            .eachCount()
        val topScorers = scorerCounts
            .mapNotNull { (userId, score) ->
                userId?.let { usersById[it] }?.let { ScorerItem(it.name, "", score) }
            }
            .sortedByDescending { it.score }

        return ManageEventUiState(
            eventId = id.toString(),
            eventName = title,
            venue = address.orEmpty(),
            dateRange = dateRangeLabel(startDate, endDate),
            sportType = sportType,
            status = status.toEventStatus(),
            isPublic = isPrivate != true,
            allowTeams = maxTeams != null,
            allowFreeAgents = participationType == "individual" ||
                    participationType == "individual_e_equipa" ||
                    acceptedIndividualRegistrations.isNotEmpty() ||
                    pendingIndividualRegistrations.isNotEmpty(),
            registeredTeams = confirmedTeamIds.size,
            maxTeams = maxTeams ?: participationLimit ?: 0,
            activePlayers = acceptedIndividualRegistrations.size + confirmedTeamRegistrations.size,
            freeAgentsCount = acceptedIndividualRegistrations.size,
            entryFee = priceLabel(price ?: entryFee),
            waitlistCount = waitlistRegistrations.size + pendingIndividualRegistrations.size + pendingTeamRegistrations.size,
            isSingleMatch = eventType == "jogo_amigavel" || games.size <= 1,
            completedGames = games.count { it.status == "terminado" },
            totalGames = games.size,
            matchProgress = if (games.isEmpty()) 0f else games.count { it.status == "terminado" } / games.size.toFloat(),
            recentRegistrations = registrations
                .sortedByDescending { it.createdAt.orEmpty() }
                .take(5)
                .map { registration ->
                    val user = registration.userId?.let { usersById[it] }
                    RecentRegistrationItem(
                        id = registration.id.toString(),
                        name = user?.name ?: registration.teamId?.let { teamsById[it]?.name }.orEmpty(),
                        position = if (registration.teamId == null) "Free Agent" else "Team",
                        timeAgo = registration.createdAt.toShortDateTime()
                    )
                },
            teams = confirmedTeamIds
                .mapNotNull { teamId ->
                    teamsById[teamId]?.toManageTeam(
                        registrations = confirmedTeamRegistrations.filter { it.teamId == teamId },
                        usersById = usersById
                    )
                },
            freeAgents = acceptedIndividualRegistrations.mapNotNull { registration ->
                val user = registration.userId?.let { usersById[it] } ?: return@mapNotNull null
                FreeAgentItem(
                    id = user.id.toString(),
                    name = user.name,
                    initials = user.name.initials(),
                    position = "Free Agent",
                    experienceLevel = user.experienceLevel ?: 1
                )
            },
            waitlistItems = waitlistRegistrations
                .mapIndexed { index, registration ->
                    val user = registration.userId?.let { usersById[it] }
                    val team = registration.teamId?.let { teamsById[it] }
                    WaitlistItem(
                        id = registration.id.toString(),
                        name = team?.name ?: user?.name.orEmpty(),
                        initials = (team?.name ?: user?.name.orEmpty()).initials(),
                        isTeam = team != null,
                        waitlistPosition = index + 1
                    )
                },
            individualRegistrationRequests = pendingIndividualRegistrations
                .sortedBy { it.createdAt.orEmpty() }
                .mapNotNull { registration ->
                    val user = registration.userId?.let { usersById[it] } ?: return@mapNotNull null
                    IndividualRegistrationRequestItem(
                        registrationId = registration.id,
                        userId = user.id,
                        name = user.name,
                        initials = user.name.initials(),
                        experienceLevel = user.experienceLevel,
                        requestedAt = registration.createdAt.toShortDateTime()
                    )
                },
            teamRegistrationRequests = pendingEventTeamRegistrations
                .sortedBy { it.createdAt.orEmpty() }
                .mapNotNull { registration ->
                    val team = teamsById[registration.teamId] ?: return@mapNotNull null
                    val captain = registration.captainUserId?.let { usersById[it] }
                    TeamRegistrationRequestItem(
                        registrationId = registration.id.toInt(),
                        teamId = team.id,
                        captainUserId = captain?.id,
                        teamName = team.name,
                        captainName = captain?.name.orEmpty(),
                        requestedAt = registration.createdAt.toShortDateTime()
                    )
                },
            currentGame = scheduledGames.firstOrNull { it.status == GameStatus.LIVE }?.let {
                CurrentGameBanner(
                    label = "CURRENT GAME",
                    homeTeamAbbr = it.homeTeamAbbr,
                    awayTeamAbbr = it.awayTeamAbbr,
                    homeTeamName = it.homeTeam.uppercase(),
                    awayTeamName = it.awayTeam.uppercase(),
                    venue = it.venue
                )
            },
            scheduledGames = scheduledGames,
            bestScorer = topScorers.firstOrNull(),
            gamesPlayed = games.size,
            gamesWeeklyDelta = games.count { it.scheduledAt.toLocalDateTimeOrNull()?.isAfter(LocalDateTime.now().minusDays(7)) == true },
            nextBigMatch = games
                .mapNotNull { it.scheduledAt.toLocalDateTimeOrNull() }
                .firstOrNull { it.isAfter(LocalDateTime.now()) }
                ?.format(DateTimeFormatter.ofPattern("dd MMM, HH:mm", Locale.US))
                .orEmpty(),
            standings = buildStandings(confirmedTeamRegistrations, gameTeams, teamsById),
            topScorers = topScorers,
            eventSummaryStats = EventSummaryStats(
                totalScore = topScorers.sumOf { it.score },
                totalInfractions = timeline.count { row ->
                    row.actionTypeId?.let { actionById[it]?.name?.isInfractionAction() } == true
                }
            )
        )
    }

    private fun ManageEventTeamRow.toManageTeam(
        registrations: List<ManageEventRegistrationRow>,
        usersById: Map<Int, ManageEventUserRow>
    ): ManageTeamItem {
        return ManageTeamItem(
            id = id.toString(),
            name = name,
            abbreviation = name.abbreviation(),
            location = "",
            playerCount = registrations.size,
            badge = "",
            eventStatus = registrations.firstOrNull()?.status.toTeamEventStatus(),
            players = registrations.mapNotNull { registration ->
                val user = registration.userId?.let { usersById[it] } ?: return@mapNotNull null
                ManagePlayerItem(
                    id = user.id.toString(),
                    name = user.name,
                    initials = user.name.initials(),
                    playerId = "#${user.id}",
                    state = if (registration.status == "aceite") {
                        PlayerValidationState.VALIDATED
                    } else {
                        PlayerValidationState.PENDING
                    }
                )
            }
        )
    }

    private fun ManageEventGameRow.toScheduledGame(
        gameTeams: List<ManageEventGameTeamRow>,
        teamsById: Map<Int, ManageEventTeamRow>,
        sportType: SportType
    ): ScheduledGameItem {
        val firstTeam = gameTeams.getOrNull(0)?.teamId?.let { teamsById[it] }
        val secondTeam = gameTeams.getOrNull(1)?.teamId?.let { teamsById[it] }
        val dateTime = scheduledAt.toLocalDateTimeOrNull()

        return ScheduledGameItem(
            id = id.toString(),
            homeTeam = firstTeam?.name ?: "TBD",
            awayTeam = secondTeam?.name ?: "TBD",
            homeTeamAbbr = firstTeam?.name?.abbreviation().orEmpty(),
            awayTeamAbbr = secondTeam?.name?.abbreviation().orEmpty(),
            sport = sportType.name.lowercase().replaceFirstChar { it.uppercase() },
            month = dateTime?.format(DateTimeFormatter.ofPattern("MMM", Locale.US))?.uppercase().orEmpty(),
            day = dateTime?.dayOfMonth?.toString().orEmpty(),
            time = dateTime?.format(DateTimeFormatter.ofPattern("HH:mm")).orEmpty(),
            venue = address.orEmpty(),
            status = status.toGameStatus(),
            homeScore = gameTeams.getOrNull(0)?.result.toScoreOrNull(),
            awayScore = gameTeams.getOrNull(1)?.result.toScoreOrNull()
        )
    }

    private fun buildStandings(
        registrations: List<ManageEventRegistrationRow>,
        gameTeams: List<ManageEventGameTeamRow>,
        teamsById: Map<Int, ManageEventTeamRow>
    ): List<StandingItem> {
        val teamIds = registrations.mapNotNull { it.teamId }.distinct()

        return teamIds.mapNotNull { teamId ->
            val team = teamsById[teamId] ?: return@mapNotNull null
            val rows = gameTeams.filter { it.teamId == teamId }
            val wins = rows.count { it.isWinner == true }
            val losses = rows.count { it.isWinner == false }
            val draws = rows.count { it.isWinner == null && it.result != null }

            StandingItem(
                position = 0,
                teamName = team.name,
                teamAbbr = team.name.abbreviation(),
                played = rows.size,
                wins = wins,
                draws = draws,
                losses = losses,
                points = wins * 3 + draws
            )
        }
            .sortedByDescending { it.points }
            .mapIndexed { index, standing -> standing.copy(position = index + 1) }
    }

    private fun EventStatus.toDatabaseValue(): String {
        return when (this) {
            EventStatus.DRAFT -> "rascunho"
            EventStatus.REGISTRATION_OPEN,
            EventStatus.REGISTRATION_CLOSED -> "publicado"
            EventStatus.ONGOING -> "a_decorrer"
            EventStatus.FINISHED -> "terminado"
            EventStatus.CANCELLED -> "cancelado"
        }
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

    private fun String?.toGameStatus(): GameStatus {
        return when (this) {
            "a_decorrer" -> GameStatus.LIVE
            "intervalo" -> GameStatus.WARM_UP
            "terminado" -> GameStatus.FINISHED
            "cancelado" -> GameStatus.CANCELLED
            else -> GameStatus.SCHEDULED
        }
    }

    private fun String?.toTeamEventStatus(): TeamEventStatus {
        return when (this) {
            "aceite" -> TeamEventStatus.CONFIRMED
            "recusada", "recusado", "banido" -> TeamEventStatus.WITHDRAWN
            else -> TeamEventStatus.PENDING
        }
    }

    private fun String?.toLocalDateTimeOrNull(): LocalDateTime? {
        if (isNullOrBlank()) return null
        return runCatching { LocalDateTime.parse(replace(" ", "T").take(19)) }.getOrNull()
    }

    private fun String?.toShortDateTime(): String {
        return toLocalDateTimeOrNull()
            ?.format(DateTimeFormatter.ofPattern("dd MMM, HH:mm", Locale.US))
            .orEmpty()
    }

    private fun dateRangeLabel(start: String?, end: String?): String {
        val startDate = start.toLocalDateTimeOrNull()
        val endDate = end.toLocalDateTimeOrNull()
        if (startDate == null) return ""

        return if (endDate == null || startDate.toLocalDate() == endDate.toLocalDate()) {
            startDate.format(DateTimeFormatter.ofPattern("MMM dd", Locale.US))
        } else {
            "${startDate.format(DateTimeFormatter.ofPattern("MMM dd", Locale.US))} - ${endDate.format(DateTimeFormatter.ofPattern("MMM dd", Locale.US))}"
        }
    }

    private fun priceLabel(value: Double?): String {
        return if (value == null || value == 0.0) "Free" else "€${value.toIntIfWhole()}"
    }

    private fun Double.toIntIfWhole(): String {
        return if (this % 1.0 == 0.0) toInt().toString() else "%.2f".format(Locale.US, this)
    }

    private fun String?.toScoreOrNull(): Int? {
        return this?.trim()?.toIntOrNull()
    }

    private fun String.initials(): String {
        return split(" ")
            .filter { it.isNotBlank() }
            .take(2)
            .joinToString("") { it.first().uppercase() }
    }

    private fun String.abbreviation(): String {
        return split(" ")
            .filter { it.isNotBlank() }
            .take(3)
            .joinToString("") { it.first().uppercase() }
            .ifBlank { take(3).uppercase() }
    }

    private fun String.isScoreAction(): Boolean {
        val normalized = lowercase()
        return "golo" in normalized || "goal" in normalized || "ponto" in normalized || "score" in normalized
    }

    private fun String.isInfractionAction(): Boolean {
        val normalized = lowercase()
        return "falta" in normalized || "cart" in normalized || "card" in normalized || "infraction" in normalized
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
