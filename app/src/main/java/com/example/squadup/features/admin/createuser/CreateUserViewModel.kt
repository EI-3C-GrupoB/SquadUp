package com.example.squadup.features.admin.createuser

import androidx.lifecycle.ViewModel
import com.example.squadup.features.admin.manageaccounts.AccountRole
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class CreateUserViewModel : ViewModel() {

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
}
