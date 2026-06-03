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

    fun loadProfile() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            repository
                .getCurrentProfile()
                .onSuccess { profile ->
                    _uiState.value = EditProfileUiState(
                        username = profile.username,
                        selectedPlayStyle = profile.playStyle,
                        selectedSports = profile.sports,
                        isLoading = false
                    )
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = (exception as? EditProfileException)?.messageRes
                    )
                }
        }
    }

    fun onUsernameChange(value: String) {
        _uiState.value = _uiState.value.copy(username = value, errorMessage = null)
    }

    fun onPlayStyleChange(playStyle: PlayStyle) {
        _uiState.value = _uiState.value.copy(selectedPlayStyle = playStyle, errorMessage = null)
    }

    fun onSportToggle(sport: SportType) {
        val current = _uiState.value.selectedSports
        _uiState.value = _uiState.value.copy(
            selectedSports = if (sport in current) current - sport else current + sport,
            errorMessage = null
        )
    }

    fun saveProfile(onSuccess: () -> Unit) {
        val currentState = _uiState.value

        if (currentState.username.isBlank()) {
            _uiState.value = currentState.copy(errorMessage = R.string.editProfile_error_username_required)
            return
        }

        viewModelScope.launch {
            _uiState.value = currentState.copy(isLoading = true, errorMessage = null)

            repository
                .updateProfile(
                    EditProfileUpdate(
                        username = currentState.username.trim(),
                        playStyle = currentState.selectedPlayStyle,
                        sports = currentState.selectedSports
                    )
                )
                .onSuccess {
                    _uiState.value = _uiState.value.copy(isLoading = false)
                    onSuccess()
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = (exception as? EditProfileException)?.messageRes
                    )
                }
        }
    }
}
