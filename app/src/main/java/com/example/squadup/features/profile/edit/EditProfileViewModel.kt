package com.example.squadup.features.profile.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.squadup.R
import com.example.squadup.core.enums.PlayStyle
import com.example.squadup.core.enums.SportType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class EditProfileViewModel : ViewModel() {

    private val repository = EditProfileRepository()
    private val _uiState = MutableStateFlow(EditProfileUiState())
    val uiState: StateFlow<EditProfileUiState> = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    private fun loadStaticData() {
        _uiState.value = EditProfileUiState(
            name = "Alexandre Caçador",
            username = "alexandre_cacador",
            location = "Viana do Castelo",
            selectedPlayStyle = PlayStyle.HIGH,
            selectedSports = listOf(
                SportType.SOCCER,
                SportType.BASKETBALL
            )
        )
    }

    fun onNameChange(value: String) {
        _uiState.value = _uiState.value.copy(name = value)
    }

    fun onUsernameChange(value: String) {
        _uiState.value = _uiState.value.copy(username = value, errorMessage = null)
    }

    fun onLocationChange(value: String) {
        _uiState.value = _uiState.value.copy(location = value)
    }

    fun onPlayStyleChange(playStyle: PlayStyle) {
        _uiState.value = _uiState.value.copy(selectedPlayStyle = playStyle, errorMessage = null)
    }

    fun onSportToggle(sport: SportType) {
        val current = _uiState.value.selectedSports

        _uiState.value = _uiState.value.copy(
            selectedSports = if (sport in current) {
                current - sport
            } else {
                current + sport
            }
        )
    }
}
