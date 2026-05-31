package com.example.squadup.features.profile

import androidx.lifecycle.ViewModel
import com.example.squadup.core.enums.PlayStyle
import com.example.squadup.core.enums.UserRole
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ProfileViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadStaticData()
    }

    private fun loadStaticData() {
        _uiState.value = ProfileUiState(
            isAdmin = true,
            role = UserRole.PLAYER_ORGANIZER,
            displayName = "Alex Hunter",
            matchesPlayed = 24,
            wins = 8,
            goals = 14,
            teams = 2,
            playStyle = PlayStyle.HIGH
        )
    }
}
