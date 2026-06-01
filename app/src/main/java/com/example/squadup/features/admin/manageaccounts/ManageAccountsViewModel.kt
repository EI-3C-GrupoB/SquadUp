package com.example.squadup.features.admin.manageaccounts

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ManageAccountsViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ManageAccountsUiState())
    val uiState: StateFlow<ManageAccountsUiState> = _uiState.asStateFlow()

    init {
        loadStaticData()
    }

    private fun loadStaticData() {
        _uiState.update { 
            it.copy(
                totalUsers = 24512,
                users = listOf(
                    ManageAccountItem("1", "JD", "James D.", "james@squadup.com", AccountRole.Player),
                    ManageAccountItem("2", "SK", "Sarah K.", "sarah.k@club.org", AccountRole.Organizer),
                    ManageAccountItem("3", "ML", "Marcus L.", "ml@proleague.com", AccountRole.Player),
                    ManageAccountItem("4", "AR", "Ana R.", "ana.r@admin.com", AccountRole.Admin),
                    ManageAccountItem("5", "TC", "Tom C.", "tom.c@club.org", AccountRole.Organizer),
                    ManageAccountItem("6", "BW", "Ben W.", "ben.w@league.com", AccountRole.Player),
                    ManageAccountItem("7", "LM", "Lisa M.", "lisa.m@squad.com", AccountRole.Player),
                    ManageAccountItem("8", "PF", "Pedro F.", "pedro.f@admin.com", AccountRole.Admin),
                    ManageAccountItem("9", "KJ", "Kim J.", "kim.j@teams.com", AccountRole.Player)
                )
            )
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
