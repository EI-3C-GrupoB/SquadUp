package com.example.squadup.features.events.livematch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.squadup.core.enums.EventFormat
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

class LiveMatchViewModel : ViewModel() {

    private val repository = LiveMatchRepository()
    private val _uiState = MutableStateFlow(LiveMatchUiState())
    val uiState: StateFlow<LiveMatchUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null

    fun loadGame(gameId: String) {
        if (gameId.isBlank()) return
        viewModelScope.launch {
            repository.getGame(gameId).onSuccess { game ->
                _uiState.value = game.copy(selectedTab = _uiState.value.selectedTab)
            }
        }
    }

    fun onStartMatch() {
        updatePhase(LiveMatchPhase.LIVE)
        _uiState.update { it.copy(isTimerRunning = true) }
        startTimer()
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
                repository.markLoserEliminated(state.eventId, loserTeamId)
            }
        }
    }

    private fun updatePhase(phase: LiveMatchPhase) {
        val gameId = _uiState.value.gameId
        _uiState.update { it.copy(phase = phase) }
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
            state.copy(
                homeScore = if (isHome) state.homeScore + delta else state.homeScore,
                awayScore = if (!isHome) state.awayScore + delta else state.awayScore,
                showGoalForm = false
            )
        }
        // Persist intermediate score
        val state = _uiState.value
        viewModelScope.launch {
            repository.updateScore(state.gameId, state.homeTeamId, state.awayTeamId, state.homeScore, state.awayScore)
        }
    }

    fun onRecordInfraction(isHome: Boolean, playerName: String, description: String) {
        addEvent(MatchEventType.INFRACTION, isHome, playerName, description)
        _uiState.update { it.copy(showInfractionForm = false) }
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
            repository.insertMatchEvent(currentState, type, isHome, playerName, description)
                .onSuccess {
                    _uiState.update { state ->
                        state.copy(events = state.events.map { item ->
                            if (item.id == event.id) item.copy(synced = true) else item
                        })
                    }
                }
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
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
