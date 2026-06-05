package com.example.squadup.features.events.livematch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
                _uiState.value = game.copy(
                    selectedTab = _uiState.value.selectedTab
                )
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
    }

    private fun updatePhase(phase: LiveMatchPhase) {
        val gameId = _uiState.value.gameId
        _uiState.update { it.copy(phase = phase) }

        if (gameId.isBlank()) return

        viewModelScope.launch {
            repository.updateGameStatus(gameId, phase)
        }
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

    fun onTabChange(tab: LiveMatchTab) {
        _uiState.update { it.copy(selectedTab = tab) }
    }

    fun onShowGoalForm(show: Boolean) {
        _uiState.update { it.copy(showGoalForm = show) }
    }

    fun onShowInfractionForm(show: Boolean) {
        _uiState.update { it.copy(showInfractionForm = show) }
    }

    fun onShowSubstitutionForm(show: Boolean) {
        _uiState.update { it.copy(showSubstitutionForm = show) }
    }

    fun onRecordGoal(isHome: Boolean, playerName: String, description: String) {
        addEvent(MatchEventType.SCORE, isHome, playerName, description)
        _uiState.update { state ->
            state.copy(
                homeScore = if (isHome) state.homeScore + 1 else state.homeScore,
                awayScore = if (!isHome) state.awayScore + 1 else state.awayScore,
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
        val currentState = _uiState.value
        val event = MatchEventItem(
            id = UUID.randomUUID().toString(),
            minute = currentState.timerSeconds / 60,
            type = type,
            playerName = playerName,
            teamAbbr = if (isHome) currentState.homeTeamAbbr else currentState.awayTeamAbbr,
            description = description,
            isHome = isHome,
            synced = false
        )

        _uiState.update { it.copy(events = listOf(event) + it.events) }

        viewModelScope.launch {
            repository.insertMatchEvent(currentState, type, isHome, playerName, description)
                .onSuccess {
                    _uiState.update { state ->
                        state.copy(
                            events = state.events.map { item ->
                                if (item.id == event.id) item.copy(synced = true) else item
                            }
                        )
                    }
                }
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}
