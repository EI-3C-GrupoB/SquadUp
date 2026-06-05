package com.example.squadup.features.admin.manageaccounts.edituser

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.squadup.features.admin.manageaccounts.AccountRole
import com.example.squadup.features.admin.manageaccounts.ManageAccountsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class EditUserViewModel(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val repository = ManageAccountsRepository()
    private val userId: String = savedStateHandle.get<String>("userId") ?: ""

    private val _uiState = MutableStateFlow(EditUserUiState(userId = userId))
    val uiState: StateFlow<EditUserUiState> = _uiState.asStateFlow()

    init {
        loadUser()
    }

    private fun loadUser() {
        viewModelScope.launch {
            repository.getUser(userId).onSuccess { user ->
                _uiState.value = EditUserUiState(
                    userId = user.id,
                    userName = user.name,
                    userEmail = user.email,
                    userInitials = user.name.initials(),
                    selectedRole = user.role,
                    isSuspended = user.isSuspended
                )
            }
        }
    }

    fun onRoleChange(role: AccountRole) {
        _uiState.value = _uiState.value.copy(selectedRole = role)
    }

    fun onToggleSuspend() {
        _uiState.value = _uiState.value.copy(isSuspended = !_uiState.value.isSuspended)
    }

    fun saveUser(onSuccess: () -> Unit) {
        val currentState = _uiState.value
        viewModelScope.launch {
            repository
                .updateUser(
                    userId = currentState.userId,
                    role = currentState.selectedRole,
                    isSuspended = currentState.isSuspended
                )
                .onSuccess { onSuccess() }
        }
    }

    fun deleteUser(onSuccess: () -> Unit) {
        viewModelScope.launch {
            repository.deleteUser(userId).onSuccess {
                onSuccess()
            }
        }
    }

    private fun String.initials(): String {
        return split(" ")
            .filter { it.isNotBlank() }
            .take(2)
            .joinToString("") { it.first().uppercase() }
    }
}
