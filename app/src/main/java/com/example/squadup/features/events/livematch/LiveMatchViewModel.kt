package com.example.squadup.features.events.livematch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.squadup.core.enums.SportType
import com.example.squadup.core.network.NetworkMonitor
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

class LiveMatchViewModel(
    private val repository: LiveMatchRepository = LiveMatchRepository(),
    private val networkMonitor: NetworkMonitor? = null
) : ViewModel() {

    private val _uiState = MutableStateFlow(LiveMatchUiState())
    val uiState: StateFlow<LiveMatchUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null
    private var gameRealtimeJob: Job? = null

    init {
        networkMonitor?.let { monitor ->
            viewModelScope.launch {
                var previousOnline: Boolean? = null
                monitor.isOnline.collect { online ->
                    _uiState.update { it.copy(isOffline = !online) }
                    if (online && previousOnline == false) {
                        syncPending()
                    }
                    previousOnline = online
                }
            }
        }
    }

    fun loadGame(gameId: String) {
        if (gameId.isBlank()) return
        gameRealtimeJob?.cancel()
        gameRealtimeJob = viewModelScope.launch {
            runCatching { repository.syncPendingOperations(gameId) }

            repository.observeGame(gameId)
                .catch { _uiState.update { it.copy(gameId = gameId, isOffline = true) } }
                .collect { game ->
                    val current = _uiState.value
                    val timerAlreadyRunning = current.isTimerRunning
                    _uiState.update {
                        game.copy(
                            selectedTab = current.selectedTab,
                            isTimerRunning = current.isTimerRunning,
                            timerSeconds = if (current.isTimerRunning) current.timerSeconds else game.timerSeconds,
                            showGoalForm = current.showGoalForm,
                            showInfractionForm = current.showInfractionForm,
                            showSubstitutionForm = current.showSubstitutionForm,
                            showAdvancedStatsForm = current.showAdvancedStatsForm
                        )
                    }
                    if (game.phase == LiveMatchPhase.LIVE && !timerAlreadyRunning) {
                        startTimer()
                    }
                }
        }
    }

    fun onStartMatch() {
        val gameId = _uiState.value.gameId
        _uiState.update { it.copy(phase = LiveMatchPhase.LIVE, isTimerRunning = true) }
        persistSnapshot()
        startTimer()
        if (gameId.isBlank()) return
        // Persiste estado + hora de início real (data_hora_real) para o cronómetro sobreviver a reentradas
        viewModelScope.launch { repository.startMatch(gameId) }
    }

    fun onStopTimer() {
        timerJob?.cancel()
        _uiState.update { it.copy(isTimerRunning = false) }
    }

    fun onResumeTimer() {
        _uiState.update { it.copy(isTimerRunning = true) }
        startTimer()
    }

    fun onEndMatch() {
        onStopTimer()
        updatePhase(LiveMatchPhase.FINISHED)

        val state = _uiState.value
        viewModelScope.launch {
            // Persist final scores to DB
            repository.updateScore(
                gameId = state.gameId,
                homeTeamId = state.homeTeamId,
                awayTeamId = state.awayTeamId,
                homeScore = state.homeScore,
                awayScore = state.awayScore
            )
            // KNOCKOUT: mark loser as eliminated
            if (state.isKnockout && state.homeScore != state.awayScore) {
                val loserTeamId = if (state.homeScore < state.awayScore) state.homeTeamId else state.awayTeamId
                repository.markLoserEliminated(state.gameId, state.eventId, loserTeamId)
            }
        }
    }

    private fun updatePhase(phase: LiveMatchPhase) {
        val gameId = _uiState.value.gameId
        _uiState.update { it.copy(phase = phase) }
        persistSnapshot()
        if (gameId.isBlank()) return
        viewModelScope.launch { repository.updateGameStatus(gameId, phase) }
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_uiState.value.isTimerRunning) {
                delay(1000L)
                _uiState.update { it.copy(timerSeconds = it.timerSeconds + 1) }
            }
        }
    }

    fun onTabChange(tab: LiveMatchTab) = _uiState.update { it.copy(selectedTab = tab) }
    fun onShowGoalForm(show: Boolean) = _uiState.update { it.copy(showGoalForm = show) }
    fun onShowInfractionForm(show: Boolean) = _uiState.update { it.copy(showInfractionForm = show) }
    fun onShowSubstitutionForm(show: Boolean) = _uiState.update { it.copy(showSubstitutionForm = show) }
    fun onShowAdvancedStatsForm(show: Boolean) = _uiState.update { it.copy(showAdvancedStatsForm = show) }

    // Generic score event — delta = points added (soccer=1, basketball=1/2/3, volleyball=1, paddle=1)
    fun onRecordGoal(isHome: Boolean, playerName: String, description: String, delta: Int = 1) {
        addEvent(MatchEventType.SCORE, isHome, playerName, description, scoreDelta = delta)
        _uiState.update { state ->
            val update = { s: LiveTeamStats ->
                when (state.sportType) {
                    SportType.SOCCER, SportType.FUTSAL -> s.copy(
                        shots = s.shots + 1,
                        shotsOnGoal = s.shotsOnGoal + 1
                    )
                    SportType.BASKETBALL -> when (delta) {
                        1 -> s.copy(points1 = s.points1 + 1)
                        3 -> s.copy(points3 = s.points3 + 1)
                        else -> s.copy(points2 = s.points2 + 1)
                    }
                    SportType.VOLLEYBALL, SportType.PADDLE -> s.copy(setsWon = s.setsWon + 1)
                }
            }
            state.copy(
                homeScore = if (isHome) state.homeScore + delta else state.homeScore,
                awayScore = if (!isHome) state.awayScore + delta else state.awayScore,
                homeStats = if (isHome) update(state.homeStats) else state.homeStats,
                awayStats = if (!isHome) update(state.awayStats) else state.awayStats,
                showGoalForm = false
            )
        }
        persistSnapshot()
        val state = _uiState.value
        viewModelScope.launch {
            repository.updateScore(state.gameId, state.homeTeamId, state.awayTeamId, state.homeScore, state.awayScore)
            val teamId = if (isHome) state.homeTeamId else state.awayTeamId
            val stats = if (isHome) state.homeStats else state.awayStats
            repository.updateStats(state.gameId, teamId, isHome, stats)
        }
    }

    fun onRecordInfraction(isHome: Boolean, playerName: String, description: String) {
        addEvent(MatchEventType.INFRACTION, isHome, playerName, description)
        val desc = description.uppercase()
        _uiState.update { state ->
            val update = { s: LiveTeamStats ->
                when (state.sportType) {
                    SportType.SOCCER, SportType.FUTSAL -> when {
                        "SECOND YELLOW" in desc -> s.copy(yellowCards = s.yellowCards + 1, redCards = s.redCards + 1)
                        "YELLOW" in desc -> s.copy(yellowCards = s.yellowCards + 1)
                        "RED" in desc -> s.copy(redCards = s.redCards + 1)
                        else -> s.copy(fouls = s.fouls + 1)
                    }
                    SportType.BASKETBALL -> when {
                        "TECHNICAL" in desc -> s.copy(technicalFouls = s.technicalFouls + 1)
                        else -> s.copy(personalFouls = s.personalFouls + 1)
                    }
                    SportType.VOLLEYBALL, SportType.PADDLE -> when {
                        "EXPULSION" in desc -> s.copy(redCards = s.redCards + 1)
                        "PENALTY" in desc -> s.copy(fouls = s.fouls + 1)
                        else -> s.copy(yellowCards = s.yellowCards + 1)
                    }
                }
            }
            state.copy(
                homeStats = if (isHome) update(state.homeStats) else state.homeStats,
                awayStats = if (!isHome) update(state.awayStats) else state.awayStats,
                showInfractionForm = false
            )
        }
        persistSnapshot()
        val state = _uiState.value
        val teamId = if (isHome) state.homeTeamId else state.awayTeamId
        val stats = if (isHome) state.homeStats else state.awayStats
        viewModelScope.launch {
            repository.updateStats(state.gameId, teamId, isHome, stats)
        }
    }

    fun onRecordSubstitution(isHome: Boolean, playerOut: String, playerIn: String) {
        addEvent(MatchEventType.SUBSTITUTION, isHome, playerOut, "↑ $playerIn  ↓ $playerOut")
        _uiState.update { it.copy(showSubstitutionForm = false) }
    }

    fun onRecordTimeout(isHome: Boolean) {
        addEvent(MatchEventType.TIMEOUT, isHome, "", "Timeout")
    }

    fun onRecordAdvancedStat(isHome: Boolean, statName: String, value: Int) {
        addEvent(MatchEventType.TIMEOUT, isHome, "", "$statName: +$value")
        _uiState.update { state ->
            val newHome = if (isHome) state.homeStats.withStat(statName, value) else state.homeStats
            val newAway = if (!isHome) state.awayStats.withStat(statName, value) else state.awayStats
            state.copy(homeStats = newHome, awayStats = newAway, showAdvancedStatsForm = false)
        }
        persistSnapshot()

        val state = _uiState.value
        val teamId = if (isHome) state.homeTeamId else state.awayTeamId
        val stats = if (isHome) state.homeStats else state.awayStats
        viewModelScope.launch {
            repository.updateStats(state.gameId, teamId, isHome, stats)
        }
    }

    private fun addEvent(
        type: MatchEventType,
        isHome: Boolean,
        playerName: String,
        description: String,
        scoreDelta: Int = 1
    ) {
        val currentState = _uiState.value
        val event = MatchEventItem(
            id = UUID.randomUUID().toString(),
            minute = currentState.timerSeconds / 60,
            type = type,
            playerName = playerName,
            teamAbbr = if (isHome) currentState.homeTeamAbbr else currentState.awayTeamAbbr,
            description = description,
            isHome = isHome,
            scoreDelta = scoreDelta,
            synced = false
        )
        _uiState.update { it.copy(events = listOf(event) + it.events) }

        viewModelScope.launch {
            repository.insertMatchEvent(currentState, type, isHome, playerName, description, event.id)
                .onSuccess { syncedNow ->
                    _uiState.update { state ->
                        state.copy(events = state.events.map { item ->
                            if (item.id == event.id) item.copy(synced = syncedNow) else item
                        })
                    }
                    persistSnapshot()
                }
        }
    }

    private fun syncPending() {
        val gameId = _uiState.value.gameId
        if (gameId.isBlank()) return
        viewModelScope.launch {
            repository.syncPendingOperations(gameId).onSuccess { syncedEventIds ->
                if (syncedEventIds.isEmpty()) return@onSuccess
                _uiState.update { state ->
                    state.copy(events = state.events.map { item ->
                        if (item.id in syncedEventIds) item.copy(synced = true) else item
                    })
                }
                persistSnapshot()
            }
        }
    }

    private fun persistSnapshot() {
        viewModelScope.launch { repository.saveSnapshot(_uiState.value) }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
        gameRealtimeJob?.cancel()
    }
}

// Helper to update a named stat on LiveTeamStats
private fun LiveTeamStats.withStat(statName: String, value: Int): LiveTeamStats {
    val n = statName.lowercase()
    return when {
        "shot" in n && "goal" in n -> copy(shotsOnGoal = shotsOnGoal + value)
        "shot" in n || "remate" in n -> copy(shots = shots + value)
        "corner" in n || "canto" in n -> copy(corners = corners + value)
        "yellow" in n || "amarelo" in n -> copy(yellowCards = yellowCards + value)
        "red" in n || "vermelho" in n -> copy(redCards = redCards + value)
        "offside" in n || "fora de jogo" in n -> copy(offsides = offsides + value)
        "save" in n || "defesa" in n -> copy(saves = saves + value)
        "personal" in n || "pessoal" in n -> copy(personalFouls = personalFouls + value)
        "technical" in n || "tecnico" in n -> copy(technicalFouls = technicalFouls + value)
        "assist" in n -> copy(assists = assists + value)
        "rebound" in n || "ressalto" in n -> copy(rebounds = rebounds + value)
        "ace" in n -> copy(aces = aces + value)
        "block" in n || "bloco" in n -> copy(blocks = blocks + value)
        "error" in n || "erro" in n -> copy(errors = errors + value)
        "set" in n -> copy(setsWon = setsWon + value)
        "foul" in n || "falta" in n -> copy(fouls = fouls + value)
        else -> this
    }
}
