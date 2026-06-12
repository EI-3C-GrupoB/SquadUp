package com.example.squadup.features.auth.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.squadup.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel(
    private val loginRepository: LoginRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onEmailChange(value: String) {
        _uiState.value = _uiState.value.copy(email = value, errorMessage = null)
    }

    fun onPasswordChange(value: String) {
        _uiState.value = _uiState.value.copy(password = value, errorMessage = null)
    }

    fun login(onSuccess: () -> Unit) {
        val currentState = _uiState.value

        when {
            currentState.email.isBlank() -> {
                _uiState.value = currentState.copy(errorMessage = R.string.login_error_empty_email)
                return
            }
            currentState.password.isBlank() -> {
                _uiState.value = currentState.copy(errorMessage = R.string.login_error_empty_password)
                return
            }
        }

        viewModelScope.launch {
            _uiState.value = currentState.copy(isLoading = true, errorMessage = null)

            loginRepository
                .login(
                    LoginCredentials(
                        email = currentState.email.trim(),
                        password = currentState.password
                    )
                )
                .onSuccess {
                    _uiState.value = _uiState.value.copy(isLoading = false)
                    onSuccess()
                }
                .onFailure { exception ->
                    val loginException = exception as? LoginException
                    if (loginException?.isAccountSuspended == true) {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            showAccountSuspendedDialog = true
                        )
                    } else {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = loginException?.messageRes
                                ?: R.string.login_error_generic
                        )
                    }
                }
        }
    }

    fun dismissAccountSuspendedDialog() {
        _uiState.value = _uiState.value.copy(showAccountSuspendedDialog = false)
    }
}

class LoginViewModelFactory(
    private val loginRepository: LoginRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(loginRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
