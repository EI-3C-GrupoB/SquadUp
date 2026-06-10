package com.example.squadup.features.admin.manageaccounts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ManageAccountsViewModel(
    private val repository: ManageAccountsRepository = ManageAccountsRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(ManageAccountsUiState(isLoading = true))
    val uiState: StateFlow<ManageAccountsUiState> = _uiState.asStateFlow()

    init {
        loadUsers()
    }

    fun loadUsers() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    errorMessage = null
                )
            }

            repository.loadUsers()
                .onSuccess { users ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            users = users,
                            totalUsers = users.size,
                            errorMessage = null
                        )
                    }
                }
                .onFailure { exception ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = exception.message ?: "Erro ao carregar utilizadores"
                        )
                    }
                }
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun onSortByName() {
        _uiState.update {
            val newSort = if (it.currentSortOrder == SortOrder.NameAZ) {
                SortOrder.NameZA
            } else {
                SortOrder.NameAZ
            }
            it.copy(currentSortOrder = newSort)
        }
    }

    fun onSortByRole() {
        _uiState.update {
            val newSort = if (it.currentSortOrder == SortOrder.RoleAZ) {
                SortOrder.RoleZA
            } else {
                SortOrder.RoleAZ
            }
            it.copy(currentSortOrder = newSort)
        }
    }

    fun onFilterDialogOpen() {
        _uiState.update {
            it.copy(
                pendingRoleFilters = it.selectedRoleFilters,
                showFilterDialog = true
            )
        }
    }

    fun onFilterDialogDismiss() {
        _uiState.update { it.copy(showFilterDialog = false) }
    }

    fun onTogglePendingRole(role: AccountRole) {
        _uiState.update { state ->
            val current = state.pendingRoleFilters
            val updated = if (role in current) current - role else current + role
            state.copy(pendingRoleFilters = updated)
        }
    }

    fun onApplyFilter() {
        _uiState.update {
            it.copy(
                selectedRoleFilters = it.pendingRoleFilters,
                showFilterDialog = false
            )
        }
    }
}
