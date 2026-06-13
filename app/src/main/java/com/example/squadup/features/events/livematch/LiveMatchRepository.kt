package com.example.squadup.features.events.livematch

import android.util.Log
import com.example.squadup.core.SupabaseClientProvider
import com.example.squadup.core.enums.EventFormat
import com.example.squadup.core.enums.SportType
import com.example.squadup.features.events.livematch.offline.CachedGameEntity
import com.example.squadup.features.events.livematch.offline.LiveMatchOfflineDao
import com.example.squadup.features.events.livematch.offline.PendingOperationEntity
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order
import io.github.jan.supabase.realtime.PostgresAction
import io.github.jan.supabase.realtime.channel
import io.github.jan.supabase.realtime.postgresChangeFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onStart
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class LiveMatchRepository(
    private val supabaseClient: SupabaseClient = SupabaseClientProvider.client,
    private val offlineDao: LiveMatchOfflineDao? = null
) {
    fun observeGame(gameId: String): Flow<LiveMatchUiState> = flow {
        val parsedGameId = gameId.toIntOrNull()
        if (parsedGameId == null) {
            emit(LiveMatchUiState(gameId = gameId))
            return@flow
        }
        emitAll(
            observeGameTables(parsedGameId)
                .map {
                    getGame(gameId).getOrElse { LiveMatchUiState(gameId = gameId, isOffline = true) }
                }
        )
    }

    private fun observeGameTables(gameId: Int): Flow<Unit> = flow {
        val suffix = "${gameId}_${System.currentTimeMillis()}"
        val statsChannel = supabaseClient.channel("livematch_stats_$suffix")
        val timelineChannel = supabaseClient.channel("livematch_timeline_$suffix")

        val statsChanges = statsChannel
            .postgresChangeFlow<PostgresAction>(schema = "public") { table = "estatistica_jogo" }
            .map { Unit }
        val timelineChanges = timelineChannel
            .postgresChangeFlow<PostgresAction>(schema = "public") { table = "registo_timeline" }
            .map { Unit }

        statsChannel.subscribe()
        timelineChannel.subscribe()

        emitAll(merge(statsChanges, timelineChanges).onStart { emit(Unit) })
    }

    suspend fun getGame(gameId: String): Result<LiveMatchUiState> {
        return try {
            val state = getGameNetwork(gameId)
            offlineDao?.upsertCachedGame(
                CachedGameEntity(
                    gameId = gameId,
                    stateJson = Json.encodeToString(state),
                    updatedAt = System.currentTimeMillis()
                )
            )
            Result.success(withPendingEvents(gameId, state))
        } catch (exception: Exception) {
            val cached = runCatching { offlineDao?.getCachedGame(gameId) }.getOrNull()
            val cachedState = cached?.let {
                runCatching { Json.decodeFromString<LiveMatchUiState>(it.stateJson) }.getOrNull()
            }
            if (cachedState != null) {
                Result.success(withPendingEvents(gameId, cachedState.copy(isOffline = true)))
            } else {
                Result.failure(exception)
            }
        }
    }

    /**
     * Reconstrói eventos registados offline que ainda não foram sincronizados,
     * para que não desapareçam do ecrã ao reabrir a app antes da sincronização.
     */
    private suspend fun withPendingEvents(gameId: String, state: LiveMatchUiState): LiveMatchUiState {
        val dao = offlineDao ?: return state
        val pendingInserts = runCatching { dao.getPendingOperations(gameId) }.getOrDefault(emptyList())
            .mapNotNull { entity ->
                runCatching { Json.decodeFromString<PendingOperation>(entity.opJson) }.getOrNull() as? PendingOperation.InsertEvent
            }
        if (pendingInserts.isEmpty()) return state

        val existingIds = state.events.map { it.id }.toSet()
        val missingEvents = pendingInserts
            .filter { it.eventId !in existingIds }
            .map { op ->
                MatchEventItem(
                    id = op.eventId,
                    minute = op.minute,
                    type = op.eventType,
                    playerName = op.playerName,
                    teamAbbr = if (op.isHome) state.homeTeamAbbr else state.awayTeamAbbr,
                    description = op.description,
                    isHome = op.isHome,
                    synced = false
                )
            }
            .reversed()
        if (missingEvents.isEmpty()) return state
        return state.copy(events = missingEvents + state.events)
    }

    private suspend fun getGameNetwork(gameId: String): LiveMatchUiState {
        val game = supabaseClient
            .from("jogo")
            .select {
                filter {
                    eq("id", gameId.toIntOrNull() ?: -1)
                }
            }
            .decodeSingle<LiveMatchGameRow>()
        val event = game.eventId?.let { getEvent(it) }
        val modality = event?.modalityId?.let { getModality(it) }
        val formato = event?.formatoId?.let { getFormato(it) }
        val gameTeams = supabaseClient
            .from("jogo_equipa")
            .select {
                filter {
                    eq("jogo_id", game.id)
                }
                // Ordem estável para casa/fora não trocar entre carregamentos/ecrãs
                order("equipa_id", Order.ASCENDING)
            }
            .decodeList<LiveMatchGameTeamRow>()
        val teamIds = gameTeams.map { it.teamId }.distinct()
        val teams = if (teamIds.isEmpty()) emptyMap() else supabaseClient.from("equipa")
            .select { filter { isIn("id", teamIds) } }
            .decodeList<LiveMatchTeamRow>()
            .associateBy { it.id }
        // Jogadores via inscricao.equipa_id (onde estão realmente atribuídos)
        val eventId = game.eventId
        val registrations: List<LiveMatchLineupRow> = if (eventId != null) {
            supabaseClient.from("inscricao")
                .select {
                    filter {
                        eq("evento_id", eventId)
                        eq("estado_inscricao", "aceite")
                    }
                }
                .decodeList<LiveMatchInscricaoRow>()
                .filter { it.teamId != null }
                .map { LiveMatchLineupRow(id = it.id, teamId = it.teamId, userId = it.userId, gameId = game.id) }
        } else emptyList()
        val lineups = registrations
        val stats = supabaseClient
            .from("estatistica_jogo")
            .select {
                filter {
                    eq("jogo_id", game.id)
                }
            }
            .decodeList<LiveMatchStatsRow>()
            .associateBy { it.teamId }
        val actions = actionTypes().associateBy { it.id }
        val timeline = supabaseClient
            .from("registo_timeline")
            .select {
                filter {
                    eq("jogo_id", game.id)
                }
            }
            .decodeList<LiveMatchTimelineRow>()
        // Buscar só os utilizadores referenciados (lineups + timeline), não a tabela inteira
        val userIds = (lineups.mapNotNull { it.userId } + timeline.mapNotNull { it.userId }).distinct()
        val users = if (userIds.isEmpty()) emptyMap() else supabaseClient.from("utilizador")
            .select { filter { isIn("id", userIds) } }
            .decodeList<LiveMatchUserRow>()
            .associateBy { it.id }

        val isOrganizer = runCatching {
            val authId = supabaseClient.auth.currentUserOrNull()?.id ?: return@runCatching false
            val currentUser = supabaseClient.from("utilizador")
                .select { filter { eq("auth_user_id", authId) } }
                .decodeSingle<LiveMatchCurrentUserRow>()
            event?.creatorId != null && currentUser.id == event.creatorId
        }.getOrDefault(false)

        return game.toUiState(event, modality, formato, gameTeams, teams, lineups, users, stats, actions, timeline, isOrganizer)
    }

    suspend fun startMatch(gameId: String): Result<Unit> {
        return try {
            startMatchNetwork(gameId)
            Result.success(Unit)
        } catch (exception: Exception) {
            queueOrFail(gameId, exception, PendingOperation.UpdateStatus(LiveMatchPhase.LIVE))
        }
    }

    private suspend fun startMatchNetwork(gameId: String) {
        val now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))
        supabaseClient
            .from("jogo")
            .update(LiveMatchStartRow(status = LiveMatchPhase.LIVE.toDatabaseStatus(), startedAt = now)) {
                filter {
                    eq("id", gameId.toIntOrNull() ?: -1)
                }
            }
    }

    suspend fun updateGameStatus(gameId: String, phase: LiveMatchPhase): Result<Unit> {
        return try {
            updateGameStatusNetwork(gameId, phase)
            Result.success(Unit)
        } catch (exception: Exception) {
            queueOrFail(gameId, exception, PendingOperation.UpdateStatus(phase))
        }
    }

    private suspend fun updateGameStatusNetwork(gameId: String, phase: LiveMatchPhase) {
        supabaseClient
            .from("jogo")
            .update(LiveMatchGameStatusUpdateRow(phase.toDatabaseStatus())) {
                filter {
                    eq("id", gameId.toIntOrNull() ?: -1)
                }
            }
    }

    suspend fun insertMatchEvent(
        state: LiveMatchUiState,
        type: MatchEventType,
        isHome: Boolean,
        playerName: String,
        description: String,
        eventId: String
    ): Result<Boolean> {
        val teamId = if (isHome) state.homeTeamId else state.awayTeamId
        val minute = state.timerSeconds / 60
        return try {
            insertMatchEventNetwork(state.gameId, teamId, type, playerName, description, minute)
            Result.success(true)
        } catch (exception: Exception) {
            Log.e("LiveMatchRepo", "insertMatchEventNetwork failed for gameId=${state.gameId}", exception)
            val dao = offlineDao ?: return Result.failure(exception)
            runCatching {
                dao.insertPendingOperation(
                    PendingOperationEntity(
                        gameId = state.gameId,
                        opJson = Json.encodeToString<PendingOperation>(
                            PendingOperation.InsertEvent(
                                eventId = eventId,
                                eventType = type,
                                isHome = isHome,
                                playerName = playerName,
                                description = description,
                                minute = minute
                            )
                        ),
                        createdAt = System.currentTimeMillis()
                    )
                )
            }
            Result.success(false)
        }
    }

    private suspend fun insertMatchEventNetwork(
        gameId: String,
        teamId: Int?,
        type: MatchEventType,
        playerName: String,
        description: String,
        minute: Int
    ) {
        val userId = findUserId(playerName)
        val actionTypeId = findActionTypeId(type)

        supabaseClient
            .from("registo_timeline")
            .insert(
                LiveMatchTimelineInsertRow(
                    minute = minute,
                    description = description,
                    teamId = teamId,
                    userId = userId,
                    actionTypeId = actionTypeId,
                    gameId = gameId.toIntOrNull()
                )
            )
    }

    suspend fun updateScore(gameId: String, homeTeamId: Int?, awayTeamId: Int?, homeScore: Int, awayScore: Int): Result<Unit> {
        return try {
            updateScoreNetwork(gameId, homeTeamId, awayTeamId, homeScore, awayScore)
            Result.success(Unit)
        } catch (e: Exception) {
            queueOrFail(gameId, e, PendingOperation.UpdateScore(homeScore, awayScore))
        }
    }

    private suspend fun updateScoreNetwork(gameId: String, homeTeamId: Int?, awayTeamId: Int?, homeScore: Int, awayScore: Int) {
        val gid = gameId.toIntOrNull() ?: throw IllegalArgumentException("gameId inválido")
        val homeWon = homeScore > awayScore
        val awayWon = awayScore > homeScore
        if (homeTeamId != null) {
            supabaseClient.from("jogo_equipa")
                .update(LiveMatchScoreUpdateRow(resultado = homeScore.toString(), isVencedor = if (homeWon) true else if (awayWon) false else null)) {
                    filter { eq("jogo_id", gid); eq("equipa_id", homeTeamId) }
                }
        }
        if (awayTeamId != null) {
            supabaseClient.from("jogo_equipa")
                .update(LiveMatchScoreUpdateRow(resultado = awayScore.toString(), isVencedor = if (awayWon) true else if (homeWon) false else null)) {
                    filter { eq("jogo_id", gid); eq("equipa_id", awayTeamId) }
                }
        }
    }

    suspend fun updateStats(gameId: String, teamId: Int?, isHome: Boolean, stats: LiveTeamStats): Result<Unit> {
        if (teamId == null) return Result.success(Unit)
        return try {
            updateStatsNetwork(gameId, teamId, stats)
            Result.success(Unit)
        } catch (e: Exception) {
            queueOrFail(gameId, e, PendingOperation.UpdateStats(isHome, stats))
        }
    }

    private suspend fun updateStatsNetwork(gameId: String, teamId: Int, stats: LiveTeamStats) {
        val gid = gameId.toIntOrNull() ?: throw IllegalArgumentException("gameId inválido")
        supabaseClient.from("estatistica_jogo")
            .upsert(
                LiveMatchStatsUpsertRow(
                    gameId = gid,
                    teamId = teamId,
                    shots = stats.shots,
                    shotsOnGoal = stats.shotsOnGoal,
                    corners = stats.corners,
                    fouls = stats.fouls,
                    yellowCards = stats.yellowCards,
                    redCards = stats.redCards,
                    offsides = stats.offsides,
                    saves = stats.saves
                )
            ) {
                onConflict = "jogo_id,equipa_id"
            }
    }

    suspend fun markLoserEliminated(gameId: String, eventId: String, loserTeamId: Int?): Result<Unit> {
        if (loserTeamId == null) return Result.success(Unit)
        return try {
            markLoserEliminatedNetwork(eventId, loserTeamId)
            Result.success(Unit)
        } catch (e: Exception) {
            queueOrFail(gameId, e, PendingOperation.MarkLoserEliminated(loserTeamId))
        }
    }

    private suspend fun markLoserEliminatedNetwork(eventId: String, loserTeamId: Int) {
        val eid = eventId.toIntOrNull() ?: throw IllegalArgumentException("eventId inválido")
        supabaseClient.from("evento_equipa")
            .update(LiveMatchEventoEquipaStatusRow(estado = "eliminada")) {
                filter { eq("evento_id", eid); eq("equipa_id", loserTeamId) }
            }
    }

    suspend fun saveSnapshot(state: LiveMatchUiState) {
        if (state.gameId.isBlank()) return
        runCatching {
            offlineDao?.upsertCachedGame(
                CachedGameEntity(
                    gameId = state.gameId,
                    stateJson = Json.encodeToString(state),
                    updatedAt = System.currentTimeMillis()
                )
            )
        }
    }

    suspend fun syncPendingOperations(gameId: String): Result<List<String>> {
        val dao = offlineDao ?: return Result.success(emptyList())
        return try {
            val pending = dao.getPendingOperations(gameId)
            if (pending.isEmpty()) return Result.success(emptyList())

            val cachedState = dao.getCachedGame(gameId)?.let { Json.decodeFromString<LiveMatchUiState>(it.stateJson) }
            val homeTeamId = cachedState?.homeTeamId
            val awayTeamId = cachedState?.awayTeamId
            val eventId = cachedState?.eventId.orEmpty()

            val syncedEventIds = mutableListOf<String>()
            for (entity in pending) {
                try {
                    val operation = Json.decodeFromString<PendingOperation>(entity.opJson)
                    when (operation) {
                        is PendingOperation.InsertEvent -> {
                            val teamId = if (operation.isHome) homeTeamId else awayTeamId
                            insertMatchEventNetwork(
                                gameId = gameId,
                                teamId = teamId,
                                type = operation.eventType,
                                playerName = operation.playerName,
                                description = operation.description,
                                minute = operation.minute
                            )
                            syncedEventIds.add(operation.eventId)
                        }

                        is PendingOperation.UpdateScore -> {
                            updateScoreNetwork(gameId, homeTeamId, awayTeamId, operation.homeScore, operation.awayScore)
                        }

                        is PendingOperation.UpdateStatus -> {
                            updateGameStatusNetwork(gameId, operation.phase)
                        }

                        is PendingOperation.MarkLoserEliminated -> {
                            val loserTeamId = operation.loserTeamId
                            if (loserTeamId != null) {
                                markLoserEliminatedNetwork(eventId, loserTeamId)
                            }
                        }

                        is PendingOperation.UpdateStats -> {
                            val teamId = if (operation.isHome) homeTeamId else awayTeamId
                            if (teamId != null) {
                                updateStatsNetwork(gameId, teamId, operation.stats)
                            }
                        }
                    }
                    dao.deletePendingOperation(entity.localId)
                } catch (exception: Exception) {
                    // Keep this operation queued for a future retry, but don't block
                    // independent operations (e.g. score/status updates) further in the queue.
                    Log.e("LiveMatchRepo", "syncPendingOperations: operation failed, will retry later", exception)
                }
            }
            Result.success(syncedEventIds)
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }

    private suspend fun queueOrFail(gameId: String, exception: Exception, operation: PendingOperation): Result<Unit> {
        val dao = offlineDao ?: return Result.failure(exception)
        runCatching {
            dao.insertPendingOperation(
                PendingOperationEntity(
                    gameId = gameId,
                    opJson = Json.encodeToString(operation),
                    createdAt = System.currentTimeMillis()
                )
            )
        }
        return Result.success(Unit)
    }

    private suspend fun getFormato(formatoId: Int): LiveMatchFormatoRow? {
        return runCatching {
            supabaseClient.from("formato")
                .select { filter { eq("id", formatoId) } }
                .decodeSingle<LiveMatchFormatoRow>()
        }.getOrNull()
    }

    private suspend fun getEvent(eventId: Int): LiveMatchEventRow? {
        return runCatching {
            supabaseClient
                .from("evento")
                .select {
                    filter {
                        eq("id", eventId)
                    }
                }
                .decodeSingle<LiveMatchEventRow>()
        }.getOrNull()
    }

    private suspend fun getModality(modalityId: Int): LiveMatchModalityRow? {
        return runCatching {
            supabaseClient
                .from("modalidade")
                .select {
                    filter {
                        eq("id", modalityId)
                    }
                }
                .decodeSingle<LiveMatchModalityRow>()
        }.getOrNull()
    }

    private suspend fun findUserId(playerName: String): Int? {
        if (playerName.isBlank()) return null

        // Filtro case-insensitive server-side (ilike sem wildcards = igualdade), em vez de
        // descarregar a tabela utilizador inteira a cada evento registado.
        return runCatching {
            supabaseClient
                .from("utilizador")
                .select { filter { ilike("nome", playerName) } }
                .decodeList<LiveMatchUserRow>()
                .firstOrNull()
                ?.id
        }.getOrNull()
    }

    // Cache da tabela de referência tipo_acao (pequena e imutável durante a sessão)
    private var cachedActionTypes: List<LiveMatchActionTypeRow>? = null

    private suspend fun actionTypes(): List<LiveMatchActionTypeRow> {
        cachedActionTypes?.let { return it }
        return runCatching {
            supabaseClient.from("tipo_acao").select().decodeList<LiveMatchActionTypeRow>()
        }.getOrDefault(emptyList()).also { if (it.isNotEmpty()) cachedActionTypes = it }
    }

    private suspend fun findActionTypeId(type: MatchEventType): Int? {
        val preferredNames = when (type) {
            MatchEventType.SCORE -> listOf("golo", "goal", "score", "ponto")
            MatchEventType.INFRACTION -> listOf("falta", "cartao", "cartão", "card")
            MatchEventType.SUBSTITUTION -> listOf("substituicao", "substituição", "substitution")
            MatchEventType.TIMEOUT -> listOf("timeout")
        }

        return actionTypes().firstOrNull { action ->
            preferredNames.any { it in action.name.lowercase() }
        }?.id
    }

    private fun LiveMatchGameRow.toUiState(
        event: LiveMatchEventRow?,
        modality: LiveMatchModalityRow?,
        formato: LiveMatchFormatoRow?,
        gameTeams: List<LiveMatchGameTeamRow>,
        teams: Map<Int, LiveMatchTeamRow>,
        lineups: List<LiveMatchLineupRow>,
        users: Map<Int, LiveMatchUserRow>,
        stats: Map<Int, LiveMatchStatsRow>,
        actions: Map<Int, LiveMatchActionTypeRow>,
        timeline: List<LiveMatchTimelineRow>,
        isOrganizer: Boolean = false
    ): LiveMatchUiState {
        val homeTeamId = gameTeams.getOrNull(0)?.teamId
        val awayTeamId = gameTeams.getOrNull(1)?.teamId
        val homeTeam = homeTeamId?.let { teams[it] }
        val awayTeam = awayTeamId?.let { teams[it] }
        val scheduledDateTime = scheduledAt.toLocalDateTimeOrNull()
        val phase = status.toPhase()
        // Reconstruir o tempo decorrido a partir da hora de início real (data_hora_real),
        // para o cronómetro não voltar a 00:00 ao reabrir um jogo a decorrer.
        val elapsedSeconds = if (phase == LiveMatchPhase.LIVE) {
            startedAt.toLocalDateTimeOrNull()?.let { start ->
                java.time.Duration.between(start, LocalDateTime.now()).seconds.coerceAtLeast(0L).toInt()
            } ?: 0
        } else 0
        val timelineEvents = timeline.map { row ->
            row.toMatchEvent(
                isHome = row.teamId == homeTeamId,
                users = users,
                teams = teams,
                actions = actions
            )
        }.sortedByDescending { it.minute }

        return LiveMatchUiState(
            gameId = id.toString(),
            eventId = event?.id?.toString().orEmpty(),
            isOrganizer = isOrganizer,
            phase = phase,
            timerSeconds = elapsedSeconds,
            isTimerRunning = phase == LiveMatchPhase.LIVE,
            sportType = sportTypeFrom(modality?.name),
            eventFormat = eventFormatFrom(formato?.name),
            homeTeamId = homeTeamId,
            awayTeamId = awayTeamId,
            homeTeamName = homeTeam?.name.orEmpty(),
            awayTeamName = awayTeam?.name.orEmpty(),
            homeTeamAbbr = homeTeam?.name?.abbreviation().orEmpty(),
            awayTeamAbbr = awayTeam?.name?.abbreviation().orEmpty(),
            venue = address.orEmpty(),
            scheduledDate = scheduledDateTime?.format(DateTimeFormatter.ofPattern("MMM d", Locale.US)).orEmpty(),
            scheduledTime = scheduledDateTime?.format(DateTimeFormatter.ofPattern("HH:mm")).orEmpty(),
            homePlayers = lineups.toPlayers(homeTeamId, users, isHome = true),
            awayPlayers = lineups.toPlayers(awayTeamId, users, isHome = false),
            homeScore = gameTeams.getOrNull(0)?.result?.toIntOrNull()
                ?: timelineEvents.count { it.type == MatchEventType.SCORE && it.isHome },
            awayScore = gameTeams.getOrNull(1)?.result?.toIntOrNull()
                ?: timelineEvents.count { it.type == MatchEventType.SCORE && !it.isHome },
            events = timelineEvents,
            homeStats = homeTeamId?.let { stats[it]?.toTeamStats() } ?: LiveTeamStats(),
            awayStats = awayTeamId?.let { stats[it]?.toTeamStats() } ?: LiveTeamStats()
        )
    }

    private fun List<LiveMatchLineupRow>.toPlayers(
        teamId: Int?,
        users: Map<Int, LiveMatchUserRow>,
        isHome: Boolean
    ): List<LiveMatchPlayer> {
        if (teamId == null) return emptyList()

        return filter { it.teamId == teamId }.mapNotNull { lineup ->
            val user = lineup.userId?.let { users[it] } ?: return@mapNotNull null
            LiveMatchPlayer(
                id = user.id.toString(),
                name = user.name,
                initials = user.name.initials(),
                isHome = isHome
            )
        }
    }

    private fun LiveMatchTimelineRow.toMatchEvent(
        isHome: Boolean,
        users: Map<Int, LiveMatchUserRow>,
        teams: Map<Int, LiveMatchTeamRow>,
        actions: Map<Int, LiveMatchActionTypeRow>
    ): MatchEventItem {
        val actionName = actionTypeId?.let { actions[it]?.name }.orEmpty()

        return MatchEventItem(
            id = id.toString(),
            minute = minute ?: 0,
            type = actionName.toMatchEventType(),
            playerName = userId?.let { users[it]?.name }.orEmpty(),
            teamAbbr = teamId?.let { teams[it]?.name?.abbreviation() }.orEmpty(),
            description = description.orEmpty(),
            isHome = isHome,
            synced = true
        )
    }

    private fun LiveMatchStatsRow.toTeamStats(): LiveTeamStats {
        return LiveTeamStats(
            shots = shots ?: 0,
            shotsOnGoal = shotsOnGoal ?: 0,
            corners = corners ?: 0,
            fouls = fouls ?: 0,
            yellowCards = yellowCards ?: 0,
            redCards = redCards ?: 0,
            offsides = offsides ?: 0,
            saves = saves ?: 0
        )
    }

    private fun LiveMatchPhase.toDatabaseStatus(): String {
        return when (this) {
            LiveMatchPhase.PRE_MATCH -> "agendado"
            LiveMatchPhase.LIVE -> "a_decorrer"
            LiveMatchPhase.FINISHED -> "terminado"
        }
    }

    private fun String?.toPhase(): LiveMatchPhase {
        return when (this) {
            "a_decorrer", "intervalo" -> LiveMatchPhase.LIVE
            "terminado" -> LiveMatchPhase.FINISHED
            else -> LiveMatchPhase.PRE_MATCH
        }
    }

    private fun String.toMatchEventType(): MatchEventType {
        val normalized = lowercase()
        return when {
            "golo" in normalized || "goal" in normalized || "score" in normalized || "ponto" in normalized -> MatchEventType.SCORE
            "falta" in normalized || "cart" in normalized || "card" in normalized -> MatchEventType.INFRACTION
            "substit" in normalized -> MatchEventType.SUBSTITUTION
            "timeout" in normalized -> MatchEventType.TIMEOUT
            else -> MatchEventType.INFRACTION
        }
    }

    private fun String?.toLocalDateTimeOrNull(): LocalDateTime? {
        if (isNullOrBlank()) return null
        return runCatching { LocalDateTime.parse(replace(" ", "T").take(19)) }.getOrNull()
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

    private fun eventFormatFrom(name: String?): EventFormat? {
        val n = name.orEmpty().lowercase()
        return when {
            "single" in n || "unico" in n || "único" in n -> EventFormat.SINGLE_MATCH
            "liga" in n || "league" in n -> EventFormat.LEAGUE
            "eliminat" in n && "grupo" !in n -> EventFormat.KNOCKOUT
            "grupo" in n || "group" in n -> EventFormat.GROUP_KNOCKOUT
            "livre" in n || "free" in n -> EventFormat.FREE
            else -> null
        }
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
