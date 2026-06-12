package com.example.squadup.features.auth.resetpassword

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.squadup.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ResetPasswordViewModel(
    private val repository: ResetPasswordRepository,
    email: String
) : ViewModel() {

    private val _uiState = MutableStateFlow(ResetPasswordUiState(email = email))
    val uiState: StateFlow<ResetPasswordUiState> = _uiState.asStateFlow()

    fun onCodeChange(value: String) {
        _uiState.value = _uiState.value.copy(code = value, errorMessage = null)
    }

    fun onNewPasswordChange(value: String) {
        _uiState.value = _uiState.value.copy(newPassword = value, errorMessage = null)
    }

    fun onConfirmPasswordChange(value: String) {
        _uiState.value = _uiState.value.copy(confirmPassword = value, errorMessage = null)
    }

    fun resetPassword(onSuccess: () -> Unit) {
        val currentState = _uiState.value

        when {
            currentState.code.isBlank() -> {
                _uiState.value = currentState.copy(errorMessage = R.string.resetPassword_error_empty_code)
                return
            }
            currentState.newPassword.length < 6 -> {
                _uiState.value = currentState.copy(errorMessage = R.string.resetPassword_error_weak_password)
                return
            }
            currentState.newPassword != currentState.confirmPassword -> {
                _uiState.value = currentState.copy(errorMessage = R.string.resetPassword_error_mismatch)
                return
            }
        }

        viewModelScope.launch {
            _uiState.value = currentState.copy(isLoading = true, errorMessage = null)

            repository
                .confirmReset(
                    email = currentState.email,
                    code = currentState.code.trim(),
                    newPassword = currentState.newPassword
                )
                .onSuccess {
                    _uiState.value = _uiState.value.copy(isLoading = false, isSuccess = true)
                    onSuccess()
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = (exception as? ResetPasswordException)?.messageRes
                            ?: R.string.resetPassword_error_generic
                    )
                }
        }
    }
}

class ResetPasswordViewModelFactory(
    private val repository: ResetPasswordRepository,
    private val email: String
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ResetPasswordViewModel::class.java)) {
            return ResetPasswordViewModel(repository, email) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
