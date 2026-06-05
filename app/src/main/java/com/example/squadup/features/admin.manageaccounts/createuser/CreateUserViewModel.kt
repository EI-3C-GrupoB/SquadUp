package com.example.squadup.features.admin.manageaccounts.createuser

import androidx.lifecycle.ViewModel
import com.example.squadup.features.admin.manageaccounts.AccountRole
import androidx.lifecycle.viewModelScope
import com.example.squadup.features.admin.manageaccounts.ManageAccountsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CreateUserViewModel : ViewModel() {

    private val repository = ManageAccountsRepository()
    private val _uiState = MutableStateFlow(CreateUserUiState())
    val uiState: StateFlow<CreateUserUiState> = _uiState.asStateFlow()

    fun onEmailChange(value: String) {
        _uiState.value = _uiState.value.copy(email = value)
    }

    fun onUsernameChange(value: String) {
        _uiState.value = _uiState.value.copy(username = value)
    }

    fun onPasswordChange(value: String) {
        _uiState.value = _uiState.value.copy(password = value)
    }

    fun onRoleChange(role: AccountRole) {
        _uiState.value = _uiState.value.copy(selectedRole = role)
    }

    fun createUser(onSuccess: () -> Unit) {
        val currentState = _uiState.value
        if (currentState.email.isBlank() || currentState.username.isBlank()) return

        viewModelScope.launch {
            _uiState.value = currentState.copy(isSaving = true)
            repository
                .createUser(
                    username = currentState.username.trim(),
                    email = currentState.email.trim(),
                    role = currentState.selectedRole
                )
                .onSuccess {
                    _uiState.value = _uiState.value.copy(isSaving = false)
                    onSuccess()
                }
                .onFailure {
                    _uiState.value = _uiState.value.copy(isSaving = false)
                }
        }
    }
}
