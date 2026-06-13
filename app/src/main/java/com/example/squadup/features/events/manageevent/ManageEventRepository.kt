package com.example.squadup.features.events.manageevent

import android.util.Log
import com.example.squadup.core.SupabaseClientProvider
import com.example.squadup.core.enums.EventFormat
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
import java.util.UUID

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
            val formato = event.formatoId?.let { getFormato(it) }
            val registrations = getRegistrations(event.id)
            val eventTeamRegistrations = getEventTeamRegistrations(event.id)

            // Only load teams relevant to this event (filtro server-side)
            val eventTeamIds = eventTeamRegistrations.map { it.teamId }.toSet()
            val inscricaoTeamIds = registrations.mapNotNull { it.teamId }.toSet()
            val allRelevantTeamIds = eventTeamIds + inscricaoTeamIds
            val teams = if (allRelevantTeamIds.isEmpty()) emptyList() else {
                supabaseClient.from("equipa")
                    .select { filter { isIn("id", allRelevantTeamIds.toList()) } }
                    .decodeList<ManageEventTeamRow>()
            }

            // Only load users who are registered to this event (filtro server-side)
            val registeredUserIds = registrations.mapNotNull { it.userId }.toSet()
            val users = if (registeredUserIds.isEmpty()) emptyList() else {
                supabaseClient.from("utilizador")
                    .select { filter { isIn("id", registeredUserIds.toList()) } }
                    .decodeList<ManageEventUserRow>()
            }

            val games = getGames(event.id)
            val gameIds = games.map { it.id }
            val gameTeams = if (gameIds.isEmpty()) emptyList() else supabaseClient
                .from("jogo_equipa")
                .select { filter { isIn("jogo_id", gameIds) } }
                .decodeList<ManageEventGameTeamRow>()
            val actions = supabaseClient.from("tipo_acao").select().decodeList<ManageEventActionTypeRow>()
            val timeline = if (gameIds.isEmpty()) emptyList() else supabaseClient
                .from("registo_timeline")
                .select { filter { isIn("jogo_id", gameIds) } }
                .decodeList<ManageEventTimelineRow>()

            Result.success(
                event.toUiState(
                    modality = modality,
                    formato = formato,
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
            val registration = supabaseClient
                .from("inscricao")
                .select {
                    filter {
                        eq("id", registrationId)
                        eq("tipo_inscricao", "evento_individual")
                    }
                }
                .decodeSingle<ManageEventRegistrationRow>()

            if (status == "aceite") {
                val eventId = registration.eventId
                    ?: throw Exception("Evento não encontrado na inscrição.")
                checkIndividualCapacity(eventId)
            }

            supabaseClient
                .from("inscricao")
                .update(ManageEventRegistrationStatusUpdateRow(status)) {
                    filter {
                        eq("id", registrationId)
                        eq("tipo_inscricao", "evento_individual")
                    }
                }

            val eventId = registration.eventId
            val userId = registration.userId

            var isPaidEvent = false
            if (status == "aceite" && eventId != null && userId != null) {
                isPaidEvent = createPaymentOrTicketForIndividual(
                    inscricaoId = registration.id,
                    eventId = eventId,
                    userId = userId
                )
            }

            if (eventId != null && userId != null) {
                notifyUserAboutIndividualDecision(
                    eventId = eventId,
                    userId = userId,
                    accepted = status == "aceite",
                    isPaidEvent = isPaidEvent
                )
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

            if (registrationStatus == "aceite") {
                checkTeamCapacity(eventTeamRegistration.eventId)
            }

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

            val captainUserId = eventTeamRegistration.captainUserId

            var isPaidEvent = false
            if (registrationStatus == "aceite" && captainUserId != null) {
                val captainInscricao = supabaseClient
                    .from("inscricao")
                    .select {
                        filter {
                            eq("evento_id", eventTeamRegistration.eventId)
                            eq("equipa_id", eventTeamRegistration.teamId)
                            eq("user_id", captainUserId)
                            eq("tipo_inscricao", "evento_equipa")
                        }
                    }
                    .decodeList<ManageEventRegistrationRow>()
                    .firstOrNull()

                if (captainInscricao != null) {
                    isPaidEvent = createPaymentOrTicketForIndividual(
                        inscricaoId = captainInscricao.id,
                        eventId = eventTeamRegistration.eventId,
                        userId = captainUserId
                    )
                }
            }

            if (captainUserId != null) {
                notifyUserAboutTeamDecision(
                    eventId = eventTeamRegistration.eventId,
                    teamId = eventTeamRegistration.teamId,
                    userId = captainUserId,
                    accepted = registrationStatus == "aceite",
                    isPaidEvent = isPaidEvent
                )
            }

            Result.success(Unit)
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }


    suspend fun createGame(
        eventId: String,
        homeTeamId: String,
        awayTeamId: String,
        date: String,
        time: String,
        venue: String
    ): Result<Unit> {
        return try {
            val id = eventId.toIntOrNull() ?: throw Exception("Evento inválido.")
            val homeId = homeTeamId.toIntOrNull() ?: throw Exception("Equipa casa inválida.")
            val awayId = awayTeamId.toIntOrNull() ?: throw Exception("Equipa visitante inválida.")
            if (homeId == awayId) throw Exception("As equipas têm de ser diferentes.")

            val scheduledAt = "${date}T${time}:00"

            val insertResult = supabaseClient.from("jogo")
                .insert(ManageEventGameInsertRow(
                    eventId = id,
                    scheduledAt = scheduledAt,
                    venue = venue.takeIf { it.isNotBlank() }
                )) { select() }
            val newGame = insertResult.decodeSingle<ManageEventGameCreatedRow>()

            supabaseClient.from("jogo_equipa")
                .insert(ManageEventGameTeamInsertRow(gameId = newGame.id, teamId = homeId))
            supabaseClient.from("jogo_equipa")
                .insert(ManageEventGameTeamInsertRow(gameId = newGame.id, teamId = awayId))

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateGame(
        gameId: String,
        homeTeamId: String,
        awayTeamId: String,
        date: String,
        time: String,
        venue: String
    ): Result<Unit> {
        return try {
            val id = gameId.toIntOrNull() ?: throw Exception("Jogo inválido.")
            val homeId = homeTeamId.toIntOrNull() ?: throw Exception("Equipa casa inválida.")
            val awayId = awayTeamId.toIntOrNull() ?: throw Exception("Equipa visitante inválida.")
            if (homeId == awayId) throw Exception("As equipas têm de ser diferentes.")

            val scheduledAt = "${date}T${time}:00"
            supabaseClient.from("jogo")
                .update(ManageEventGameUpdateRow(
                    scheduledAt = scheduledAt,
                    venue = venue.takeIf { it.isNotBlank() }
                )) { filter { eq("id", id) } }

            // Update teams: delete existing jogo_equipa rows and re-insert
            // Get existing rows first to avoid altering scores/results
            val existing = supabaseClient.from("jogo_equipa")
                .select { filter { eq("jogo_id", id) } }
                .decodeList<ManageEventGameTeamRow>()
            val existingIds = existing.map { it.teamId }.toSet()
            val newIds = setOf(homeId, awayId)
            if (existingIds != newIds) {
                // Only touch teams if the pair actually changed
                supabaseClient.from("jogo_equipa")
                    .delete { filter { eq("jogo_id", id) } }
                supabaseClient.from("jogo_equipa")
                    .insert(ManageEventGameTeamInsertRow(gameId = id, teamId = homeId))
                supabaseClient.from("jogo_equipa")
                    .insert(ManageEventGameTeamInsertRow(gameId = id, teamId = awayId))
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun checkIndividualCapacity(eventId: Int) {
        val event = supabaseClient
            .from("evento")
            .select { filter { eq("id", eventId) } }
            .decodeSingle<ManageEventRow>()

        val limit = event.participationLimit ?: return
        if (limit <= 0) return

        val accepted = supabaseClient
            .from("inscricao")
            .select {
                filter {
                    eq("evento_id", eventId)
                    eq("tipo_inscricao", "evento_individual")
                    eq("estado_inscricao", "aceite")
                }
            }
            .decodeList<ManageEventRegistrationRow>()
            .size

        if (accepted >= limit) {
            throw Exception("Não é possível aceitar. O evento já atingiu o limite.")
        }
    }

    private suspend fun checkTeamCapacity(eventId: Int) {
        val event = supabaseClient
            .from("evento")
            .select { filter { eq("id", eventId) } }
            .decodeSingle<ManageEventRow>()

        val maxTeams = event.maxTeams ?: return
        if (maxTeams <= 0) return

        val confirmed = supabaseClient
            .from("evento_equipa")
            .select {
                filter {
                    eq("evento_id", eventId)
                    eq("estado", "confirmada")
                }
            }
            .decodeList<ManageEventTeamRegistrationRow>()
            .size

        if (confirmed >= maxTeams) {
            throw Exception("Não é possível aceitar. O evento já atingiu o limite.")
        }
    }

    private suspend fun createPaymentOrTicketForIndividual(
        inscricaoId: Int,
        eventId: Int,
        userId: Int
    ): Boolean {
        return runCatching {
            val event = getEventRowForNotification(eventId)
            val total = event.price ?: 0.0

            if (total > 0.0) {
                supabaseClient.from("pagamento").insert(
                    ManageEventPaymentInsertRow(
                        eventId = eventId,
                        userId = userId,
                        amount = total
                    )
                )
                true
            } else {
                supabaseClient.from("bilhete").insert(
                    ManageEventTicketInsertRow(
                        userId = userId,
                        eventId = eventId,
                        codigoQr = UUID.randomUUID().toString()
                    )
                )
                false
            }
        }.getOrDefault(false)
    }

    private suspend fun notifyUserAboutIndividualDecision(
        eventId: Int,
        userId: Int,
        accepted: Boolean,
        isPaidEvent: Boolean = false
    ) {
        runCatching {
            val event = getEventRowForNotification(eventId)

            val description = when {
                !accepted -> "O teu pedido para participar no evento ${event.title} foi recusado."
                isPaidEvent -> "O teu pedido para participar no evento ${event.title} foi aceite. Conclui o pagamento para confirmar a inscrição."
                else -> "O teu pedido para participar no evento ${event.title} foi aceite. O teu bilhete foi emitido."
            }

            supabaseClient
                .from("notificacao")
                .insert(
                    ManageEventNotificationInsertRow(
                        userId = userId,
                        title = if (accepted) "Pedido aceite" else "Pedido recusado",
                        description = description,
                        type = "evento",
                        referenceId = event.id,
                        referenceType = "evento"
                    )
                )
        }.onFailure { exception ->
            Log.e(
                "ManageEventRepository",
                "Erro ao criar notificação de decisão individual: ${exception.message}",
                exception
            )
        }
    }

    private suspend fun notifyUserAboutTeamDecision(
        eventId: Int,
        teamId: Int,
        userId: Int,
        accepted: Boolean,
        isPaidEvent: Boolean = false
    ) {
        runCatching {
            val event = getEventRowForNotification(eventId)

            val team = supabaseClient
                .from("equipa")
                .select {
                    filter {
                        eq("id", teamId)
                    }
                }
                .decodeSingle<ManageEventTeamRow>()

            val description = when {
                !accepted -> "A equipa ${team.name} foi recusada no evento ${event.title}."
                isPaidEvent -> "A equipa ${team.name} foi aceite no evento ${event.title}. Conclui o pagamento para confirmar a inscrição."
                else -> "A equipa ${team.name} foi aceite no evento ${event.title}. O bilhete foi emitido."
            }

            supabaseClient
                .from("notificacao")
                .insert(
                    ManageEventNotificationInsertRow(
                        userId = userId,
                        title = if (accepted) "Equipa aceite" else "Equipa recusada",
                        description = description,
                        type = "evento",
                        referenceId = event.id,
                        referenceType = "evento"
                    )
                )
        }.onFailure { exception ->
            Log.e(
                "ManageEventRepository",
                "Erro ao criar notificação de decisão de equipa: ${exception.message}",
                exception
            )
        }
    }

    private suspend fun getEventRowForNotification(eventId: Int): ManageEventRow {
        return supabaseClient
            .from("evento")
            .select {
                filter {
                    eq("id", eventId)
                }
            }
            .decodeSingle<ManageEventRow>()
    }

    private fun observeEventTables(eventId: Int): Flow<Unit> = flow {
        val channelSuffix = "${eventId}_${System.currentTimeMillis()}"

        val eventChannel = supabaseClient.channel("manage_event_evento_$channelSuffix")
        val registrationsChannel = supabaseClient.channel("manage_event_inscricao_$channelSuffix")
        val eventTeamsChannel = supabaseClient.channel("manage_event_evento_equipa_$channelSuffix")
        val jogoChannel = supabaseClient.channel("manage_event_jogo_$channelSuffix")
        val jogoEquipaChannel = supabaseClient.channel("manage_event_jogo_equipa_$channelSuffix")

        val eventChanges = eventChannel
            .postgresChangeFlow<PostgresAction>(schema = "public") { table = "evento" }.map { Unit }
        val registrationChanges = registrationsChannel
            .postgresChangeFlow<PostgresAction>(schema = "public") { table = "inscricao" }.map { Unit }
        val eventTeamChanges = eventTeamsChannel
            .postgresChangeFlow<PostgresAction>(schema = "public") { table = "evento_equipa" }.map { Unit }
        val jogoChanges = jogoChannel
            .postgresChangeFlow<PostgresAction>(schema = "public") { table = "jogo" }.map { Unit }
        val jogoEquipaChanges = jogoEquipaChannel
            .postgresChangeFlow<PostgresAction>(schema = "public") { table = "jogo_equipa" }.map { Unit }

        eventChannel.subscribe()
        registrationsChannel.subscribe()
        eventTeamsChannel.subscribe()
        jogoChannel.subscribe()
        jogoEquipaChannel.subscribe()

        emitAll(
            merge(eventChanges, registrationChanges, eventTeamChanges, jogoChanges, jogoEquipaChanges)
                .onStart { emit(Unit) }
        )
    }

    private suspend fun getModality(modalityId: Int): ManageEventModalityRow? {
        return runCatching {
            supabaseClient.from("modalidade")
                .select { filter { eq("id", modalityId) } }
                .decodeSingle<ManageEventModalityRow>()
        }.getOrNull()
    }

    private suspend fun getFormato(formatoId: Int): ManageEventFormatoRow? {
        return runCatching {
            supabaseClient.from("formato")
                .select { filter { eq("id", formatoId) } }
                .decodeSingle<ManageEventFormatoRow>()
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
        formato: ManageEventFormatoRow?,
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
        val eventFormat = eventFormatFrom(formato?.name)
        val scheduledGames = games.map { game ->
            game.toScheduledGame(
                // Ordem estável (mesma do LiveMatch) para casa/fora não trocar entre ecrãs
                gameTeams = gameTeams.filter { it.gameId == game.id }.sortedBy { it.teamId },
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
            eventFormat = eventFormat,
            formatName = formato?.name.orEmpty(),
            status = status.toEventStatus(),
            isPublic = isPrivate != true,
            allowTeams = participationType == "equipa" || participationType == "individual_e_equipa",
            allowFreeAgents = participationType == "individual" || participationType == "individual_e_equipa",
            registeredTeams = confirmedTeamIds.size,
            maxTeams = when (participationType) {
                "equipa" -> maxTeams ?: 0
                "individual" -> participationLimit ?: 0
                "individual_e_equipa" -> (maxTeams ?: 0).takeIf { it > 0 } ?: (participationLimit ?: 0)
                else -> maxTeams ?: participationLimit ?: 0
            },
            activePlayers = acceptedIndividualRegistrations.size + confirmedTeamRegistrations.size,
            freeAgentsCount = acceptedIndividualRegistrations.size,
            entryFee = priceLabel(price ?: entryFee),
            waitlistCount = waitlistRegistrations.size + pendingIndividualRegistrations.size + pendingTeamRegistrations.size,
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
            standings = buildStandings(confirmedTeamRegistrations, gameTeams, teamsById, sportType),
            topScorers = topScorers,
            eventSummaryStats = buildEventSummaryStats(timeline, actionById, usersById, sportType, games, gameTeams)
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

        val firstTeamId = gameTeams.getOrNull(0)?.teamId
        val secondTeamId = gameTeams.getOrNull(1)?.teamId
        return ScheduledGameItem(
            id = id.toString(),
            homeTeam = firstTeam?.name ?: "TBD",
            awayTeam = secondTeam?.name ?: "TBD",
            homeTeamAbbr = firstTeam?.name?.abbreviation().orEmpty(),
            awayTeamAbbr = secondTeam?.name?.abbreviation().orEmpty(),
            homeTeamId = firstTeamId?.toString().orEmpty(),
            awayTeamId = secondTeamId?.toString().orEmpty(),
            sport = sportType.name.lowercase().replaceFirstChar { it.uppercase() },
            month = dateTime?.format(DateTimeFormatter.ofPattern("MMM", Locale.US))?.uppercase().orEmpty(),
            day = dateTime?.dayOfMonth?.toString().orEmpty(),
            time = dateTime?.format(DateTimeFormatter.ofPattern("HH:mm")).orEmpty(),
            rawDate = dateTime?.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")).orEmpty(),
            venue = address.orEmpty(),
            status = status.toGameStatus(),
            homeScore = gameTeams.getOrNull(0)?.result.toScoreOrNull(),
            awayScore = gameTeams.getOrNull(1)?.result.toScoreOrNull()
        )
    }

    private fun buildStandings(
        registrations: List<ManageEventRegistrationRow>,
        gameTeams: List<ManageEventGameTeamRow>,
        teamsById: Map<Int, ManageEventTeamRow>,
        sportType: SportType
    ): List<StandingItem> {
        val teamIds = registrations.mapNotNull { it.teamId }.distinct()
        val gameTeamsByGameId = gameTeams.groupBy { it.gameId }

        return teamIds.mapNotNull { teamId ->
            val team = teamsById[teamId] ?: return@mapNotNull null
            val rows = gameTeams.filter { it.teamId == teamId }
            val wins   = rows.count { it.isWinner == true }
            val losses = rows.count { it.isWinner == false }
            val draws  = rows.count { it.isWinner == null && it.result != null }

            // Goals For / Against (Soccer, Futsal)
            val goalsFor = rows.sumOf { it.result.toScoreOrNull() ?: 0 }
            val goalsAgainst = rows.sumOf { row ->
                val others = gameTeamsByGameId[row.gameId]?.filter { it.teamId != teamId } ?: emptyList()
                others.sumOf { it.result.toScoreOrNull() ?: 0 }
            }

            // Sets Won / Lost (Volleyball, Paddle)
            // For volleyball/paddle: result = sets won in that game
            val setsWon  = if (sportType == SportType.VOLLEYBALL || sportType == SportType.PADDLE) goalsFor else 0
            val setsLost = if (sportType == SportType.VOLLEYBALL || sportType == SportType.PADDLE) goalsAgainst else 0

            val points = when (sportType) {
                SportType.SOCCER, SportType.FUTSAL -> wins * 3 + draws
                SportType.BASKETBALL               -> wins * 2
                SportType.VOLLEYBALL               -> wins * 3 + draws
                SportType.PADDLE                   -> wins * 2
            }

            StandingItem(
                position = 0,
                teamName = team.name,
                teamAbbr = team.name.abbreviation(),
                played = rows.size,
                wins = wins,
                draws = draws,
                losses = losses,
                points = points,
                goalsFor = if (sportType == SportType.SOCCER || sportType == SportType.FUTSAL) goalsFor else 0,
                goalsAgainst = if (sportType == SportType.SOCCER || sportType == SportType.FUTSAL) goalsAgainst else 0,
                setsWon = setsWon,
                setsLost = setsLost
            )
        }
            .sortedWith(compareByDescending<StandingItem> { it.points }
                .thenByDescending { it.goalDiff }
                .thenByDescending { it.goalsFor })
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
        val n = name.orEmpty().lowercase()
        return when {
            "futsal" in n -> SportType.FUTSAL
            "futeb" in n || "soccer" in n || "football" in n -> SportType.SOCCER
            "basket" in n || "basquete" in n -> SportType.BASKETBALL
            "volei" in n || "volley" in n -> SportType.VOLLEYBALL
            "padel" in n || "paddle" in n || "tenis" in n || "tennis" in n -> SportType.PADDLE
            else -> SportType.SOCCER
        }
    }

    private fun buildEventSummaryStats(
        timeline: List<ManageEventTimelineRow>,
        actionById: Map<Int, ManageEventActionTypeRow>,
        usersById: Map<Int, ManageEventUserRow>,
        sportType: SportType,
        games: List<ManageEventGameRow>,
        gameTeams: List<ManageEventGameTeamRow>
    ): EventSummaryStats {
        fun actionName(row: ManageEventTimelineRow) =
            row.actionTypeId?.let { actionById[it]?.name }.orEmpty().lowercase()

        val totalScore = timeline.count { actionName(it).isScoreAction() }
        val totalInfractions = timeline.count { actionName(it).isInfractionAction() }

        // Soccer / Futsal discipline
        val yellowCards = timeline.count { "amarelo" in actionName(it) || "yellow" in actionName(it) }
        val redCards = timeline.count {
            val n = actionName(it)
            ("vermelho" in n || "red_card" in n || "red card" in n) && "amarelo" !in n
        }
        val gameTeamsByGameId = gameTeams.groupBy { it.gameId }
        val finishedGameIds = games.filter { it.status == "terminado" }.map { it.id }.toSet()
        val cleanSheets = finishedGameIds.count { gameId ->
            val rows = gameTeamsByGameId[gameId] ?: return@count false
            rows.any { (it.result.toScoreOrNull() ?: 0) == 0 }
        }

        // Basketball fouls
        val personalFouls = timeline.count { "pessoal" in actionName(it) || "personal" in actionName(it) }
        val technicalFouls = timeline.count { "tecnic" in actionName(it) || "technical" in actionName(it) }

        // Volleyball / Paddle sets
        val totalSetsWon = if (sportType == SportType.VOLLEYBALL || sportType == SportType.PADDLE) {
            gameTeams.sumOf { it.result.toScoreOrNull() ?: 0 }
        } else 0

        // Top infractors (all infractions by player)
        val infractorCounts = timeline
            .filter { actionName(it).isInfractionAction() }
            .groupingBy { it.userId }
            .eachCount()
        val topInfractors = infractorCounts
            .mapNotNull { (uid, count) -> uid?.let { usersById[it] }?.let { ScorerItem(it.name, "", count) } }
            .sortedByDescending { it.score }
            .take(5)

        return EventSummaryStats(
            totalScore = totalScore,
            totalInfractions = totalInfractions,
            yellowCards = yellowCards,
            redCards = redCards,
            cleanSheets = cleanSheets,
            personalFouls = personalFouls,
            technicalFouls = technicalFouls,
            totalSetsWon = totalSetsWon,
            topInfractors = topInfractors
        )
    }

    private fun eventFormatFrom(name: String?): EventFormat? {
        val n = name.orEmpty().lowercase()
        return when {
            "unic" in n || "amig" in n || "single" in n || "jogo_unic" in n -> EventFormat.SINGLE_MATCH
            "liga" in n || "league" in n -> EventFormat.LEAGUE
            "grupo" in n || "group" in n -> EventFormat.GROUP_KNOCKOUT
            "eliminat" in n || "knockout" in n || "copa" in n || "cup" in n -> EventFormat.KNOCKOUT
            "livre" in n || "free" in n || "aberto" in n -> EventFormat.FREE
            n.isBlank() -> null
            else -> null
        }
    }
}
