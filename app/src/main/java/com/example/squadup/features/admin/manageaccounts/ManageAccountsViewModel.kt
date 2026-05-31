package com.example.squadup.features.admin.manageaccounts

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ManageAccountsViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ManageAccountsUiState())
    val uiState: StateFlow<ManageAccountsUiState> = _uiState.asStateFlow()

    init {
        loadStaticData()
    }

    private fun loadStaticData() {
        _uiState.value = ManageAccountsUiState(
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

    fun onSearchQueryChange(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
    }

    fun onToggleSort() {
        _uiState.value = _uiState.value.copy(sortAscending = !_uiState.value.sortAscending)
    }

    fun onFilterDialogOpen() {
        _uiState.value = _uiState.value.copy(
            pendingRoleFilter = _uiState.value.selectedRoleFilter,
            showFilterDialog = true
        )
    }

    fun onFilterDialogDismiss() {
        _uiState.value = _uiState.value.copy(showFilterDialog = false)
    }

    fun onPendingRoleChange(role: AccountRole?) {
        _uiState.value = _uiState.value.copy(pendingRoleFilter = role)
    }

    fun onApplyFilter() {
        _uiState.value = _uiState.value.copy(
            selectedRoleFilter = _uiState.value.pendingRoleFilter,
            showFilterDialog = false
        )
    }
}
