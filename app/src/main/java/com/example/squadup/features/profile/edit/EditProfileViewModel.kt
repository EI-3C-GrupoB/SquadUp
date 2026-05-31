package com.example.squadup.features.profile.edit

import androidx.lifecycle.ViewModel
import com.example.squadup.core.enums.PlayStyle
import com.example.squadup.core.enums.SportType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class EditProfileViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(EditProfileUiState())
    val uiState: StateFlow<EditProfileUiState> = _uiState.asStateFlow()

    init {
        loadStaticData()
    }

    private fun loadStaticData() {
        _uiState.value = EditProfileUiState(
            username = "Alexandre Caçador",
            selectedPlayStyle = PlayStyle.HIGH,
            selectedSports = listOf(SportType.SOCCER, SportType.BASKETBALL)
        )
    }

    fun onUsernameChange(value: String) {
        _uiState.value = _uiState.value.copy(username = value)
    }

    fun onPlayStyleChange(playStyle: PlayStyle) {
        _uiState.value = _uiState.value.copy(selectedPlayStyle = playStyle)
    }

    fun onSportToggle(sport: SportType) {
        val current = _uiState.value.selectedSports
        _uiState.value = _uiState.value.copy(
            selectedSports = if (sport in current) current - sport else current + sport
        )
    }
}
