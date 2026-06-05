package com.example.squadup.features.admin.manageaccounts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ManageAccountsViewModel : ViewModel() {

    private val repository = ManageAccountsRepository()
    private val _uiState = MutableStateFlow(ManageAccountsUiState())
    val uiState: StateFlow<ManageAccountsUiState> = _uiState.asStateFlow()

    init {
        loadUsers()
    }

    private fun loadUsers() {
        viewModelScope.launch {
            repository.getUsers().onSuccess { usersState ->
                _uiState.value = usersState.copy(
                    searchQuery = _uiState.value.searchQuery,
                    selectedRoleFilters = _uiState.value.selectedRoleFilters,
                    currentSortOrder = _uiState.value.currentSortOrder
                )
            }
        }
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
