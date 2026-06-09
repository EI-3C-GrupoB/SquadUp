package com.example.squadup.features.events.manageevent

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.squadup.core.enums.EventStatus
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ManageEventViewModel : ViewModel() {

    private val repository = ManageEventRepository()
    private val _uiState = MutableStateFlow(ManageEventUiState())
    val uiState: StateFlow<ManageEventUiState> = _uiState.asStateFlow()
    private var eventRealtimeJob: Job? = null

    fun loadEvent(eventId: String) {
        if (eventId.isBlank()) return

        eventRealtimeJob?.cancel()
        eventRealtimeJob = viewModelScope.launch {
            repository.observeEvent(eventId)
                .catch { exception ->
                    _uiState.update {
                        it.copy(errorMessage = exception.message ?: "Não foi possível carregar o evento.")
                    }
                }
                .collect { event ->
                    _uiState.value = event.copy(
                        selectedTab = _uiState.value.selectedTab,
                        teamSearchQuery = _uiState.value.teamSearchQuery,
                        freeAgentSearchQuery = _uiState.value.freeAgentSearchQuery,
                        gameSearchQuery = _uiState.value.gameSearchQuery,
                        activeRegistrationActionId = _uiState.value.activeRegistrationActionId,
                        errorMessage = _uiState.value.errorMessage ?: event.errorMessage
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

    fun acceptIndividualRegistration(registrationId: Int) {
        updateIndividualRegistrationStatus(
            registrationId = registrationId,
            update = repository::acceptIndividualRegistration
        )
    }

    fun rejectIndividualRegistration(registrationId: Int) {
        updateIndividualRegistrationStatus(
            registrationId = registrationId,
            update = repository::rejectIndividualRegistration
        )
    }

    fun acceptTeamRegistration(registrationId: Int) {
        updateIndividualRegistrationStatus(
            registrationId = registrationId,
            update = repository::acceptTeamRegistration
        )
    }

    fun rejectTeamRegistration(registrationId: Int) {
        updateIndividualRegistrationStatus(
            registrationId = registrationId,
            update = repository::rejectTeamRegistration
        )
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
                _uiState.update { it.copy(status = status, errorMessage = null) }
            }.onFailure { exception ->
                _uiState.update {
                    it.copy(errorMessage = exception.message ?: "Não foi possível atualizar o evento.")
                }
            }
        }
    }

    private fun updateIndividualRegistrationStatus(
        registrationId: Int,
        update: suspend (Int) -> Result<Unit>
    ) {
        if (_uiState.value.activeRegistrationActionId != null) return

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    activeRegistrationActionId = registrationId,
                    errorMessage = null
                )
            }

            update(registrationId).onSuccess {
                _uiState.update { state ->
                    state.copy(
                        activeRegistrationActionId = null,
                        errorMessage = null,
                        individualRegistrationRequests = state.individualRegistrationRequests
                            .filterNot { it.registrationId == registrationId },
                        teamRegistrationRequests = state.teamRegistrationRequests
                            .filterNot { it.registrationId == registrationId }
                    )
                }
            }.onFailure { exception ->
                _uiState.update {
                    it.copy(
                        activeRegistrationActionId = null,
                        errorMessage = exception.message ?: "Não foi possível atualizar a inscrição."
                    )
                }
            }
        }
    }

    companion object {
        private const val PAGE_SIZE = 5
    }
}
