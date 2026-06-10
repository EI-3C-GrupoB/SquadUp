package com.example.squadup.features.admin.manageaccounts.createuser

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.squadup.features.admin.manageaccounts.AccountRole
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CreateUserViewModel(
    private val repository: CreateUserRepository = CreateUserRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateUserUiState())
    val uiState: StateFlow<CreateUserUiState> = _uiState.asStateFlow()

    fun onNameChange(value: String) {
        _uiState.update { it.copy(name = value, errorMessage = null) }
    }

    fun onEmailChange(value: String) {
        _uiState.update { it.copy(email = value, errorMessage = null) }
    }

    fun onUsernameChange(value: String) {
        _uiState.update { it.copy(username = value, errorMessage = null) }
    }

    fun onPasswordChange(value: String) {
        _uiState.update { it.copy(password = value, errorMessage = null) }
    }

    fun onRoleChange(role: AccountRole) {
        _uiState.update { it.copy(selectedRole = role, errorMessage = null) }
    }

    fun createUser(onSuccess: () -> Unit) {
        val state = _uiState.value
        val validationError = validate(state)
        if (validationError != null) {
            _uiState.update { it.copy(errorMessage = validationError) }
            return
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    errorMessage = null,
                    isCreateSuccessful = false
                )
            }

            repository.createUser(
                name = state.name.trim(),
                username = state.username.trim(),
                email = state.email.trim(),
                password = state.password,
                role = state.selectedRole
            ).onSuccess {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isCreateSuccessful = true
                    )
                }
                onSuccess()
            }.onFailure { exception ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = exception.message ?: "Erro ao criar utilizador"
                    )
                }
            }
        }
    }

    private fun validate(state: CreateUserUiState): String? {
        return when {
            state.name.isBlank() -> "Indica o nome do utilizador."
            state.email.isBlank() -> "Indica o email do utilizador."
            "@" !in state.email -> "Indica um email válido."
            state.username.isBlank() -> "Indica o username do utilizador."
            state.password.length < 6 -> "A palavra-passe deve ter pelo menos 6 caracteres."
            else -> null
        }
    }
}
