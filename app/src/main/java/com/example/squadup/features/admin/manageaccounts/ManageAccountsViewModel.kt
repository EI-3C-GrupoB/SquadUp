package com.example.squadup.features.admin.manageaccounts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ManageAccountsViewModel : ViewModel() {
    private val repository = ManageAccountsRepository()

    private val _uiState = MutableStateFlow(ManageAccountsUiState())
    val uiState: StateFlow<ManageAccountsUiState> = _uiState.asStateFlow()

    private var usersJob: Job? = null

    init {
        startObservingUsers()
    }

    private fun startObservingUsers() {
        usersJob?.cancel()
        usersJob = viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            repository.getUsersRealtime()
                .catch { exception ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = exception.message ?: "Erro ao carregar utilizadores"
                        )
                    }
                }
                .collect { users ->
                    _uiState.update {
                        it.copy(
                            users = users,
                            totalUsers = users.size,
                            isLoading = false,
                            errorMessage = null
                        )
                    }
                }
        }
    }

    fun loadUsers() {
        startObservingUsers()
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun onSortByName() {
        _uiState.update {
            val newSort = if (it.currentSortOrder == SortOrder.NameAZ) SortOrder.NameZA else SortOrder.NameAZ
            it.copy(currentSortOrder = newSort)
        }
    }

    fun onSortByRole() {
        _uiState.update {
            val newSort = if (it.currentSortOrder == SortOrder.RoleAZ) SortOrder.RoleZA else SortOrder.RoleAZ
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
