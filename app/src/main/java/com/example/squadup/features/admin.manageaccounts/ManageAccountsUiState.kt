package com.example.squadup.features.admin.manageaccounts

enum class AccountRole { Admin, Organizer, Player }

enum class SortOrder { NameAZ, NameZA, RoleAZ, RoleZA }

data class ManageAccountsUiState(
    val searchQuery: String = "",
    val users: List<ManageAccountItem> = emptyList(),
    val selectedRoleFilters: Set<AccountRole> = emptySet(),
    val pendingRoleFilters: Set<AccountRole> = emptySet(),
    val showFilterDialog: Boolean = false,
    val currentSortOrder: SortOrder = SortOrder.RoleAZ,
    val totalUsers: Int = 0
) {
    val filteredUsers: List<ManageAccountItem>
        get() {
            val filtered = users
                .filter { selectedRoleFilters.isEmpty() || it.role in selectedRoleFilters }
                .filter {
                    searchQuery.isBlank() ||
                    it.name.contains(searchQuery, ignoreCase = true) ||
                    it.email.contains(searchQuery, ignoreCase = true)
                }
            
            return when (currentSortOrder) {
                SortOrder.NameAZ -> filtered.sortedBy { it.name }
                SortOrder.NameZA -> filtered.sortedByDescending { it.name }
                SortOrder.RoleAZ -> filtered.sortedBy { it.role.name }
                SortOrder.RoleZA -> filtered.sortedByDescending { it.role.name }
            }
        }
}

data class ManageAccountItem(
    val id: String,
    val initials: String,
    val name: String,
    val email: String,
    val role: AccountRole
)
