package com.example.squadup.features.events.livematch

import com.example.squadup.core.SupabaseClientProvider
import com.example.squadup.core.enums.EventFormat
import com.example.squadup.core.enums.SportType
import com.example.squadup.features.events.livematch.offline.CachedGameEntity
import com.example.squadup.features.events.livematch.offline.LiveMatchOfflineDao
import com.example.squadup.features.events.livematch.offline.PendingOperationEntity
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
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
            Result.success(state)
        } catch (exception: Exception) {
            val cached = offlineDao?.getCachedGame(gameId)
            if (cached != null) {
                Result.success(Json.decodeFromString<LiveMatchUiState>(cached.stateJson).copy(isOffline = true))
            } else {
                Result.failure(exception)
            }
        }
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
            }
            .decodeList<LiveMatchGameTeamRow>()
        val teams = supabaseClient.from("equipa").select().decodeList<LiveMatchTeamRow>().associateBy { it.id }
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
        val users = supabaseClient.from("utilizador").select().decodeList<LiveMatchUserRow>().associateBy { it.id }
        val stats = supabaseClient
            .from("estatistica_jogo")
            .select {
                filter {
                    eq("jogo_id", game.id)
                }
            }
            .decodeList<LiveMatchStatsRow>()
            .associateBy { it.teamId }
        val actions = supabaseClient.from("tipo_acao").select().decodeList<LiveMatchActionTypeRow>().associateBy { it.id }
        val timeline = supabaseClient
            .from("registo_timeline")
            .select {
                filter {
                    eq("jogo_id", game.id)
                }
            }
            .decodeList<LiveMatchTimelineRow>()

        val isOrganizer = runCatching {
            val authId = supabaseClient.auth.currentUserOrNull()?.id ?: return@runCatching false
            val currentUser = supabaseClient.from("utilizador")
                .select { filter { eq("auth_user_id", authId) } }
                .decodeSingle<LiveMatchCurrentUserRow>()
            event?.creatorId != null && currentUser.id == event.creatorId
        }.getOrDefault(false)

        return game.toUiState(event, modality, formato, gameTeams, teams, lineups, users, stats, actions, timeline, isOrganizer)
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
            val dao = offlineDao ?: return Result.failure(exception)
            dao.insertPendingOperation(
                PendingOperationEntity(
                    gameId = state.gameId,
                    opJson = Json.encodeToString<PendingOperation>(
                        PendingOperation.InsertEvent(
                            eventId = eventId,
                            type = type,
                            isHome = isHome,
                            playerName = playerName,
                            description = description,
                            minute = minute
                        )
                    ),
                    createdAt = System.currentTimeMillis()
                )
            )
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
        offlineDao?.upsertCachedGame(
            CachedGameEntity(
                gameId = state.gameId,
                stateJson = Json.encodeToString(state),
                updatedAt = System.currentTimeMillis()
            )
        )
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
                val operation = Json.decodeFromString<PendingOperation>(entity.opJson)
                try {
                    when (operation) {
                        is PendingOperation.InsertEvent -> {
                            val teamId = if (operation.isHome) homeTeamId else awayTeamId
                            insertMatchEventNetwork(
                                gameId = gameId,
                                teamId = teamId,
                                type = operation.type,
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
                    }
                    dao.deletePendingOperation(entity.localId)
                } catch (exception: Exception) {
                    return Result.success(syncedEventIds)
                }
            }
            Result.success(syncedEventIds)
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }

    private suspend fun queueOrFail(gameId: String, exception: Exception, operation: PendingOperation): Result<Unit> {
        val dao = offlineDao ?: return Result.failure(exception)
        dao.insertPendingOperation(
            PendingOperationEntity(
                gameId = gameId,
                opJson = Json.encodeToString(operation),
                createdAt = System.currentTimeMillis()
            )
        )
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

        return runCatching {
            supabaseClient
                .from("utilizador")
                .select()
                .decodeList<LiveMatchUserRow>()
                .firstOrNull { it.name.equals(playerName, ignoreCase = true) }
                ?.id
        }.getOrNull()
    }

    private suspend fun findActionTypeId(type: MatchEventType): Int? {
        val preferredNames = when (type) {
            MatchEventType.SCORE -> listOf("golo", "goal", "score", "ponto")
            MatchEventType.INFRACTION -> listOf("falta", "cartao", "cartão", "card")
            MatchEventType.SUBSTITUTION -> listOf("substituicao", "substituição", "substitution")
            MatchEventType.TIMEOUT -> listOf("timeout")
        }

        return runCatching {
            supabaseClient
                .from("tipo_acao")
                .select()
                .decodeList<LiveMatchActionTypeRow>()
                .firstOrNull { action ->
                    preferredNames.any { it in action.name.lowercase() }
                }
                ?.id
        }.getOrNull()
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
            phase = status.toPhase(),
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
