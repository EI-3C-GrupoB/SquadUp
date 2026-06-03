package com.example.squadup.features.profile.changepassword

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.squadup.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ChangePasswordViewModel : ViewModel() {

    private val repository = ChangePasswordRepository()
    private val _uiState = MutableStateFlow(ChangePasswordUiState())
    val uiState: StateFlow<ChangePasswordUiState> = _uiState.asStateFlow()

    fun onCurrentPasswordChange(value: String) {
        _uiState.value = _uiState.value.copy(currentPassword = value, errorMessage = null)
    }

    fun onNewPasswordChange(value: String) {
        _uiState.value = _uiState.value.copy(newPassword = value, errorMessage = null)
    }

    fun onConfirmNewPasswordChange(value: String) {
        _uiState.value = _uiState.value.copy(confirmNewPassword = value, errorMessage = null)
    }

    fun changePassword(onSuccess: () -> Unit) {
        val currentState = _uiState.value

        when {
            currentState.currentPassword.isBlank() -> {
                _uiState.value = currentState.copy(errorMessage = R.string.changePassword_error_current_required)
                return
            }
            currentState.newPassword.length < 6 -> {
                _uiState.value = currentState.copy(errorMessage = R.string.changePassword_error_new_min_length)
                return
            }
            currentState.newPassword != currentState.confirmNewPassword -> {
                _uiState.value = currentState.copy(errorMessage = R.string.changePassword_error_mismatch)
                return
            }
        }

        viewModelScope.launch {
            _uiState.value = currentState.copy(isLoading = true, errorMessage = null)

            repository
                .changePassword(
                    ChangePasswordRequest(
                        currentPassword = currentState.currentPassword,
                        newPassword = currentState.newPassword
                    )
                )
                .onSuccess {
                    _uiState.value = ChangePasswordUiState()
                    onSuccess()
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = (exception as? ChangePasswordException)?.messageRes
                    )
                }
        }
    }
}
