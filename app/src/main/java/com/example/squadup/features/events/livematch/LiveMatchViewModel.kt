package com.example.squadup.features.events.livematch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.squadup.core.enums.SportType
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

class LiveMatchViewModel : ViewModel() {

    // gameId pode ser passado via SavedStateHandle quando ligarmos ao backend
    // Para já, simula um jogo terminado se gameId = "g5" ou "g6"
    fun loadGame(gameId: String) {
        val isFinished = gameId == "g5" || gameId == "g6"
        if (isFinished) {
            _uiState.update { it.copy(
                gameId = gameId,
                phase = LiveMatchPhase.FINISHED,
                homeScore = if (gameId == "g5") 3 else 1,
                awayScore = if (gameId == "g5") 0 else 1,
                events = if (gameId == "g5") listOf(
                    MatchEventItem("e1", 12, MatchEventType.SCORE,       "Ricardo Silva",  "FCP", "Open Play", false, synced = true),
                    MatchEventItem("e2", 34, MatchEventType.SCORE,       "Ricardo Silva",  "FCP", "Penalty",   false, synced = true),
                    MatchEventItem("e3", 67, MatchEventType.INFRACTION,  "Bruno Costa",    "VTC", "Yellow Card",false, synced = true),
                    MatchEventItem("e4", 78, MatchEventType.SCORE,       "Diogo Jota",     "FCP", "Header",    false, synced = true),
                    MatchEventItem("e5", 55, MatchEventType.SUBSTITUTION,"Carlos Mendes",  "VTC", "↑ Tiago ↓ Carlos", true,  synced = true),
                ) else listOf(
                    MatchEventItem("e6", 22, MatchEventType.SCORE,       "João Silva",  "SLB", "Open Play", true,  synced = true),
                    MatchEventItem("e7", 45, MatchEventType.INFRACTION,  "André Costa","BCW", "Yellow Card",false, synced = true),
                    MatchEventItem("e8", 88, MatchEventType.SCORE,       "Nuno Serra", "BCW", "Free Kick",  false, synced = true),
                )
            ) }
        }
    }

    private val _uiState = MutableStateFlow(
        LiveMatchUiState(
            gameId = "g0",
            sportType = SportType.SOCCER,
            homeTeamName = "Benfica Stars",
            awayTeamName = "FC Porto Elite",
            homeTeamAbbr = "SLB",
            awayTeamAbbr = "FCP",
            venue = "Arena Central",
            scheduledDate = "APR 9",
            scheduledTime = "18:00",
            homePlayers = listOf(
                LiveMatchPlayer("p1", "João Silva",      "JS", true),
                LiveMatchPlayer("p2", "Ricardo Lopes",   "RL", true),
                LiveMatchPlayer("p3", "Miguel Costa",    "MC", true),
                LiveMatchPlayer("p4", "André Martins",   "AM", true),
            ),
            awayPlayers = listOf(
                LiveMatchPlayer("p5", "Ricardo Silva",   "RS", false),
                LiveMatchPlayer("p6", "Bruno Fernandes", "BF", false),
                LiveMatchPlayer("p7", "Diogo Jota",      "DJ", false),
            ),
            homeStats = LiveTeamStats(shots = 5, shotsOnGoal = 3, corners = 2, fouls = 4, yellowCards = 1),
            awayStats = LiveTeamStats(shots = 3, shotsOnGoal = 1, corners = 1, fouls = 6, yellowCards = 1)
        )
    )
    val uiState: StateFlow<LiveMatchUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null

    // ── Phase ─────────────────────────────────────────────────────────────────

    fun onStartMatch() {
        _uiState.update { it.copy(phase = LiveMatchPhase.LIVE, isTimerRunning = true) }
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
        _uiState.update { it.copy(phase = LiveMatchPhase.FINISHED) }
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

    // ── Tabs ──────────────────────────────────────────────────────────────────

    fun onTabChange(tab: LiveMatchTab) {
        _uiState.update { it.copy(selectedTab = tab) }
    }

    // ── Forms ─────────────────────────────────────────────────────────────────

    fun onShowGoalForm(show: Boolean)         { _uiState.update { it.copy(showGoalForm = show) } }
    fun onShowInfractionForm(show: Boolean)   { _uiState.update { it.copy(showInfractionForm = show) } }
    fun onShowSubstitutionForm(show: Boolean) { _uiState.update { it.copy(showSubstitutionForm = show) } }

    // ── Events ────────────────────────────────────────────────────────────────

    fun onRecordGoal(isHome: Boolean, playerName: String, description: String) {
        addEvent(MatchEventType.SCORE, isHome, playerName, description)
        _uiState.update { s ->
            s.copy(
                homeScore = if (isHome) s.homeScore + 1 else s.homeScore,
                awayScore = if (!isHome) s.awayScore + 1 else s.awayScore,
                showGoalForm = false
            )
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

    private fun addEvent(
        type: MatchEventType,
        isHome: Boolean,
        playerName: String,
        description: String
    ) {
        val event = MatchEventItem(
            id = UUID.randomUUID().toString(),
            minute = _uiState.value.timerSeconds / 60,
            type = type,
            playerName = playerName,
            teamAbbr = if (isHome) _uiState.value.homeTeamAbbr else _uiState.value.awayTeamAbbr,
            description = description,
            isHome = isHome,
            synced = false
        )
        _uiState.update { it.copy(events = listOf(event) + it.events) }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}
