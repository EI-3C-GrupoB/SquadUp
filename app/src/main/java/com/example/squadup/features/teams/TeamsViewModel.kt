package com.example.squadup.features.teams

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TeamsViewModel : ViewModel() {

    private val repository = TeamsRepository()

    private val _uiState = MutableStateFlow(TeamsUiState())
    val uiState: StateFlow<TeamsUiState> = _uiState.asStateFlow()

    init {
        loadTeams()
    }

    private fun loadTeams() {
        viewModelScope.launch {
            repository.getTeamsRealtime().collect { teams ->
                val currentState = _uiState.value

                val shouldKeepSettingsActive = currentState.settingsTeamId?.let { settingsTeamId ->
                    teams.myTeams.any { team ->
                        team.id == settingsTeamId && team.isCaptain
                    }
                } ?: false

                _uiState.value = teams.copy(
                    selectedTab = currentState.selectedTab,
                    searchQuery = currentState.searchQuery,
                    expandedTeamId = currentState.expandedTeamId,
                    settingsTeamId = if (shouldKeepSettingsActive) {
                        currentState.settingsTeamId
                    } else {
                        null
                    },
                    pendingJoinRequests = teams.pendingJoinRequests + currentState.pendingJoinRequests,
                    showJoinByCodeDialog = currentState.showJoinByCodeDialog,
                    joinByCodeValue = currentState.joinByCodeValue,
                    joinByCodeError = currentState.joinByCodeError,
                    isJoiningByCode = currentState.isJoiningByCode
                )
            }
        }
    }

    fun onTabSelected(tab: TeamsTab) {
        _uiState.value = _uiState.value.copy(
            selectedTab = tab,
            searchQuery = "",
            expandedTeamId = null,
            settingsTeamId = null
        )
    }

    fun onSearchQueryChange(value: String) {
        _uiState.value = _uiState.value.copy(searchQuery = value)
    }

    fun onTeamToggle(teamId: String) {
        val currentExpandedId = _uiState.value.expandedTeamId
        val isClosing = currentExpandedId == teamId

        _uiState.value = _uiState.value.copy(
            expandedTeamId = if (isClosing) null else teamId,
            settingsTeamId = if (isClosing) null else _uiState.value.settingsTeamId
        )
    }

    fun onTeamSettingsToggle(teamId: String) {
        val currentSettingsTeamId = _uiState.value.settingsTeamId

        _uiState.value = _uiState.value.copy(
            settingsTeamId = if (currentSettingsTeamId == teamId) null else teamId
        )
    }

    fun onAskToJoinClick(teamId: String) {
        val currentRequests = _uiState.value.pendingJoinRequests

        android.util.Log.d(
            "TeamsViewModel",
            "Click onAskToJoin for team: $teamId. Current requests: $currentRequests"
        )

        if (currentRequests.contains(teamId)) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                pendingJoinRequests = currentRequests + teamId
            )

            android.util.Log.d("TeamsViewModel", "Optimistic update done for: $teamId")

            repository.requestToJoinTeam(teamId.toInt())
                .onSuccess {
                    android.util.Log.d("TeamsViewModel", "Success joining team: $teamId")
                }
                .onFailure { error ->
                    android.util.Log.e(
                        "TeamsViewModel",
                        "Error joining team: ${error.message}",
                        error
                    )

                    _uiState.value = _uiState.value.copy(
                        pendingJoinRequests = _uiState.value.pendingJoinRequests - teamId
                    )
                }
        }
    }

    fun onPromoteMemberClick(
        teamId: String,
        memberId: String
    ) {
        viewModelScope.launch {
            repository.promoteMemberToCaptain(
                teamId = teamId.toInt(),
                memberUserId = memberId.toInt()
            ).onSuccess {
                android.util.Log.d(
                    "TeamsViewModel",
                    "Member $memberId promoted to captain in team $teamId"
                )
            }.onFailure { error ->
                android.util.Log.e(
                    "TeamsViewModel",
                    "Error promoting member $memberId in team $teamId: ${error.message}",
                    error
                )
            }
        }
    }

    fun onRemoveMemberClick(
        teamId: String,
        memberId: String
    ) {
        viewModelScope.launch {
            repository.removeMemberFromTeam(
                teamId = teamId.toInt(),
                memberUserId = memberId.toInt()
            ).onSuccess {
                android.util.Log.d(
                    "TeamsViewModel",
                    "Member $memberId removed from team $teamId"
                )
            }.onFailure { error ->
                android.util.Log.e(
                    "TeamsViewModel",
                    "Error removing member $memberId from team $teamId: ${error.message}",
                    error
                )
            }
        }
    }

    fun onJoinByCodeDialogOpen() {
        _uiState.value = _uiState.value.copy(
            showJoinByCodeDialog = true,
            joinByCodeValue = "",
            joinByCodeError = null
        )
    }

    fun onJoinByCodeDialogDismiss() {
        if (_uiState.value.isJoiningByCode) return

        _uiState.value = _uiState.value.copy(
            showJoinByCodeDialog = false,
            joinByCodeValue = "",
            joinByCodeError = null
        )
    }

    fun onJoinByCodeChange(value: String) {
        _uiState.value = _uiState.value.copy(
            joinByCodeValue = value.uppercase(),
            joinByCodeError = null
        )
    }

    fun onJoinByCodeSubmit() {
        val code = _uiState.value.joinByCodeValue.trim()

        if (code.isBlank()) {
            _uiState.value = _uiState.value.copy(
                joinByCodeError = "Introduz um código de convite."
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isJoiningByCode = true,
                joinByCodeError = null
            )

            repository.requestToJoinTeamByInviteCode(code)
                .onSuccess {
                    _uiState.value = _uiState.value.copy(
                        showJoinByCodeDialog = false,
                        joinByCodeValue = "",
                        joinByCodeError = null,
                        isJoiningByCode = false
                    )
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        joinByCodeError = error.message ?: "Não foi possível enviar o pedido.",
                        isJoiningByCode = false
                    )
                }
        }
    }
}
