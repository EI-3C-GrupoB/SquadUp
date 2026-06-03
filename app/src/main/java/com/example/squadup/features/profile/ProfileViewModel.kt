package com.example.squadup.features.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.squadup.core.enums.PlayStyle
import com.example.squadup.core.enums.UserRole
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {

    private val repository = ProfileRepository()
    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    fun loadProfile() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            repository
                .getCurrentProfile()
                .onSuccess { profile ->
                    _uiState.value = ProfileUiState(
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
                        errorMessage = (exception as? ProfileException)?.messageRes
                    )
                }
        }
    }

    fun logout(onSuccess: () -> Unit) {
        viewModelScope.launch {
            repository.logout().onSuccess {
                onSuccess()
            }.onFailure { exception ->
                _uiState.value = _uiState.value.copy(
                    errorMessage = (exception as? ProfileException)?.messageRes
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

    private fun resolvePlayStyle(value: String?): PlayStyle? {
        return when (value?.lowercase()) {
            "low", "baixa", "baixo" -> PlayStyle.LOW
            "medium", "media", "média", "medio", "médio" -> PlayStyle.MEDIUM
            "high", "alta", "alto" -> PlayStyle.HIGH
            else -> null
        }
    }
}
