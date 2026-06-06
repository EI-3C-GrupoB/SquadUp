package com.example.squadup.features.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.squadup.R
import com.example.squadup.core.enums.PlayStyle
import com.example.squadup.core.enums.UserRole
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {

    private val repository = ProfileRepository()
    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()
    private var loadJob: Job? = null

    fun loadProfile() {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            repository
                .getCurrentProfile()
                .onSuccess { profile ->
                    _uiState.value = ProfileUiState(
                        isLoggedIn = true,
                        isAdmin = profile.isAdmin,
                        role = resolveRole(profile.roleNames),
                        displayName = profile.displayName,
                        matchesPlayed = profile.matchesPlayed,
                        wins = 0,
                        goals = profile.goals,
                        teams = profile.teams,
                        playStyle = resolvePlayStyle(profile.playStyle),
                        isLoading = false
                    )
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isLoggedIn = true, // Mantemos como logado se falhou apenas o carregamento dos dados
                        errorMessage = (exception as? ProfileException)?.messageRes ?: R.string.profile_error_load
                    )
                }
        }
    }

    private fun resolveRole(roleNames: List<String>): UserRole {
        val normalizedRoles = roleNames.map { it.lowercase() }
        val isPlayer = normalizedRoles.any { it == "player" || it == "jogador" }
        val isOrganizer = normalizedRoles.any { it == "organizer" || it == "organizador" }

        return when {
            isPlayer && isOrganizer -> UserRole.PLAYER_ORGANIZER
            isOrganizer -> UserRole.ORGANIZER
            else -> UserRole.PLAYER
        }
    }

    private fun resolvePlayStyle(value: Int?): PlayStyle? =
        value?.let { PlayStyle.fromLevel(it) }
}
