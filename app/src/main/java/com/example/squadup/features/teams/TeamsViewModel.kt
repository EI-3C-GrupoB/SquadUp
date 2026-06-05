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
            repository.getTeams().onSuccess { teams ->
                _uiState.value = teams.copy(
                    selectedTab = _uiState.value.selectedTab,
                    searchQuery = _uiState.value.searchQuery
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
}
