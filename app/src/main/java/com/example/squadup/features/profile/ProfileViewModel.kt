package com.example.squadup.features.profile

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.squadup.R
import com.example.squadup.core.enums.PlayStyle
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
            _uiState.value = _uiState.value.copy(errorMessage = null)

            repository
                .getCurrentProfile()
                .onSuccess { profile ->
                    _uiState.value = ProfileUiState(
                        isLoggedIn = true,
                        isAdmin = profile.isAdmin,
                        role = profile.role,
                        displayName = profile.displayName,
                        photoUrl = profile.photoUrl,
                        matchesPlayed = profile.matchesPlayed,
                        wins = 0,
                        goals = profile.goals,
                        teams = profile.teams,
                        playStyle = resolvePlayStyle(profile.playStyle),
                    )
                }
                .onFailure { exception ->
                    if (exception is kotlinx.coroutines.CancellationException) return@onFailure
                    _uiState.value = _uiState.value.copy(
                        isLoggedIn = true,
                        errorMessage = (exception as? ProfileException)?.messageRes ?: R.string.profile_error_load
                    )
                }
        }
    }

    fun uploadAvatar(
        uri: Uri, 
        context: Context,
        onUploadSuccess: (String) -> Unit = {}
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(errorMessage = null)
            
            val photoBytes = context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
            if (photoBytes == null) {
                _uiState.value = _uiState.value.copy(errorMessage = R.string.profile_error_load)
                return@launch
            }

            repository.uploadAvatar(photoBytes)
                .onSuccess { url ->
                    _uiState.value = _uiState.value.copy(photoUrl = url)
                    onUploadSuccess(url)
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        errorMessage = (exception as? ProfileException)?.messageRes ?: R.string.profile_error_load
                    )
                }
        }
    }

    private fun resolvePlayStyle(value: Int?): PlayStyle? =
        value?.let { PlayStyle.fromLevel(it) }
}
