package com.example.squadup.features.admin.manageaccounts.edituser

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.squadup.features.admin.manageaccounts.AccountRole
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EditUserViewModel(
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val repository = EditUserRepository()

    private val userId: Int? = savedStateHandle
        .get<String>("userId")
        ?.toIntOrNull()

    private val _uiState = MutableStateFlow(EditUserUiState(isLoading = true))
    val uiState: StateFlow<EditUserUiState> = _uiState.asStateFlow()

    init {
        loadUser()
    }

    fun loadUser() {
        val id = userId
        if (id == null) {
            _uiState.update {
                it.copy(
                    isLoading = false,
                    errorMessage = "Utilizador inválido"
                )
            }
            return
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    errorMessage = null
                )
            }

            repository.loadUser(id)
                .onSuccess { loadedState ->
                    _uiState.value = loadedState
                }
                .onFailure { exception ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = exception.message ?: "Erro ao carregar utilizador"
                        )
                    }
                }
        }
    }

    fun onRoleChange(role: AccountRole) {
        _uiState.update {
            it.copy(
                selectedRole = role,
                errorMessage = null,
                isSaveSuccessful = false
            )
        }
    }

    fun onAdminToggle() {
        _uiState.update {
            it.copy(
                isAdminRole = !it.isAdminRole,
                errorMessage = null,
                isSaveSuccessful = false
            )
        }
    }

    fun saveUser(onSuccess: () -> Unit) {
        val id = userId ?: return
        val role = _uiState.value.selectedRole
        val isAdmin = _uiState.value.isAdminRole

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isSaving = true,
                    errorMessage = null,
                    isSaveSuccessful = false
                )
            }

            repository.updateUserRole(id, role, isAdmin)
                .onSuccess {
                    _uiState.update {
                        it.copy(
                            isSaving = false,
                            isSaveSuccessful = true
                        )
                    }
                    onSuccess()
                }
                .onFailure { exception ->
                    _uiState.update {
                        it.copy(
                            isSaving = false,
                            errorMessage = exception.message ?: "Erro ao guardar utilizador"
                        )
                    }
                }
        }
    }

    fun onToggleSuspend() {
        val id = userId ?: return
        val nextSuspendedValue = !_uiState.value.isSuspended

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isSaving = true,
                    errorMessage = null
                )
            }

            repository.updateSuspension(id, nextSuspendedValue)
                .onSuccess {
                    _uiState.update {
                        it.copy(
                            isSaving = false,
                            isSuspended = nextSuspendedValue
                        )
                    }
                }
                .onFailure { exception ->
                    _uiState.update {
                        it.copy(
                            isSaving = false,
                            errorMessage = exception.message ?: "Erro ao actualizar estado da conta"
                        )
                    }
                }
        }
    }

    fun deleteUser(onSuccess: () -> Unit) {
        val id = userId ?: return

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isDeleting = true,
                    errorMessage = null,
                    isDeleteSuccessful = false
                )
            }

            repository.deleteUserProfile(id)
                .onSuccess {
                    _uiState.update {
                        it.copy(
                            isDeleting = false,
                            isDeleteSuccessful = true
                        )
                    }
                    onSuccess()
                }
                .onFailure { exception ->
                    _uiState.update {
                        it.copy(
                            isDeleting = false,
                            errorMessage = exception.message ?: "Erro ao apagar utilizador"
                        )
                    }
                }
        }
    }
}
