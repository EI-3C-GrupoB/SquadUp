package com.example.squadup.features.admin.manageaccounts

enum class AccountRole { Admin, Organizer, Player }

data class ManageAccountsUiState(
    val searchQuery: String = "",
    val users: List<ManageAccountItem> = emptyList(),
    val selectedRoleFilter: AccountRole? = null,
    val pendingRoleFilter: AccountRole? = null,
    val showFilterDialog: Boolean = false,
    val sortAscending: Boolean = true,
    val totalUsers: Int = 24512
) {
    val filteredUsers: List<ManageAccountItem>
        get() {
            val filtered = users
                .filter { selectedRoleFilter == null || it.role == selectedRoleFilter }
                .filter {
                    searchQuery.isBlank() ||
                    it.name.contains(searchQuery, ignoreCase = true) ||
                    it.email.contains(searchQuery, ignoreCase = true)
                }
            return if (sortAscending) filtered.sortedBy { it.name }
                   else filtered.sortedByDescending { it.name }
        }
}

data class ManageAccountItem(
    val id: String,
    val initials: String,
    val name: String,
    val email: String,
    val role: AccountRole
)
