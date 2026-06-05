package com.example.squadup.features.events.manageevent

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.squadup.core.enums.EventStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ManageEventViewModel : ViewModel() {

    private val repository = ManageEventRepository()
    private val _uiState = MutableStateFlow(ManageEventUiState())
    val uiState: StateFlow<ManageEventUiState> = _uiState.asStateFlow()

    fun loadEvent(eventId: String) {
        if (eventId.isBlank()) return

        viewModelScope.launch {
            repository.getEvent(eventId).onSuccess { event ->
                _uiState.value = event.copy(
                    selectedTab = _uiState.value.selectedTab,
                    teamSearchQuery = _uiState.value.teamSearchQuery,
                    freeAgentSearchQuery = _uiState.value.freeAgentSearchQuery,
                    gameSearchQuery = _uiState.value.gameSearchQuery
                )
            }
        }
    }

    fun onTabChange(tab: ManageEventTab) {
        _uiState.update { it.copy(selectedTab = tab) }
    }

    fun onTeamSearchQueryChange(query: String) {
        _uiState.update { it.copy(teamSearchQuery = query, teamsDisplayCount = PAGE_SIZE) }
    }

    fun onFreeAgentSearchQueryChange(query: String) {
        _uiState.update { it.copy(freeAgentSearchQuery = query, freeAgentsDisplayCount = PAGE_SIZE) }
    }

    fun onLoadMoreTeams() {
        _uiState.update { it.copy(teamsDisplayCount = it.teamsDisplayCount + PAGE_SIZE) }
    }

    fun onLoadMoreFreeAgents() {
        _uiState.update { it.copy(freeAgentsDisplayCount = it.freeAgentsDisplayCount + PAGE_SIZE) }
    }

    fun onGameSearchQueryChange(query: String) {
        _uiState.update { it.copy(gameSearchQuery = query) }
    }

    fun onTeamExpand(teamId: String) {
        _uiState.update { state ->
            state.copy(expandedTeamId = if (state.expandedTeamId == teamId) null else teamId)
        }
    }

    fun onPlayerRemove(teamId: String, playerId: String) {
        // Remoção ainda depende de regra de backend para inscrição/lineup.
    }

    fun onStatusAction() {
        val next = when (_uiState.value.status) {
            EventStatus.DRAFT -> EventStatus.REGISTRATION_OPEN
            EventStatus.REGISTRATION_OPEN -> EventStatus.REGISTRATION_CLOSED
            EventStatus.REGISTRATION_CLOSED -> EventStatus.ONGOING
            EventStatus.ONGOING -> EventStatus.FINISHED
            else -> return
        }

        persistStatus(next)
    }

    fun onCancelEvent() {
        persistStatus(EventStatus.CANCELLED)
    }

    private fun persistStatus(status: EventStatus) {
        val eventId = _uiState.value.eventId
        if (eventId.isBlank()) return

        viewModelScope.launch {
            repository.updateStatus(eventId, status).onSuccess {
                _uiState.update { it.copy(status = status) }
            }
        }
    }

    companion object {
        private const val PAGE_SIZE = 5
    }
}
