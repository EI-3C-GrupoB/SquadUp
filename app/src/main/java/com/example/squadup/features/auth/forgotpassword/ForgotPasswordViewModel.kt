package com.example.squadup.features.auth.forgotpassword

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.squadup.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ForgotPasswordViewModel(
    private val repository: ForgotPasswordRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ForgotPasswordUiState())
    val uiState: StateFlow<ForgotPasswordUiState> = _uiState.asStateFlow()

    fun onEmailChange(value: String) {
        _uiState.value = _uiState.value.copy(email = value, errorMessage = null)
    }

    fun sendCode(onCodeSent: (email: String) -> Unit) {
        val currentState = _uiState.value

        if (currentState.email.isBlank()) {
            _uiState.value = currentState.copy(errorMessage = R.string.forgotPassword_error_empty_email)
            return
        }

        val email = currentState.email.trim()

        viewModelScope.launch {
            _uiState.value = currentState.copy(isLoading = true, errorMessage = null)

            repository
                .requestReset(email)
                .onSuccess {
                    _uiState.value = _uiState.value.copy(isLoading = false, isCodeSent = true)
                    onCodeSent(email)
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = (exception as? ForgotPasswordException)?.messageRes
                            ?: R.string.forgotPassword_error_generic
                    )
                }
        }
    }
}

class ForgotPasswordViewModelFactory(
    private val repository: ForgotPasswordRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ForgotPasswordViewModel::class.java)) {
            return ForgotPasswordViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
