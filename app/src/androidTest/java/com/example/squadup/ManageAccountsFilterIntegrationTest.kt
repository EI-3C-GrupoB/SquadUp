package com.example.squadup

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.squadup.features.admin.manageaccounts.AccountRole
import com.example.squadup.features.admin.manageaccounts.ManageAccountItem
import com.example.squadup.features.admin.manageaccounts.ManageAccountsUiState
import com.example.squadup.features.admin.manageaccounts.SortOrder
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Integration test: verifies that search, role filter and sort work together
 * correctly when combined in ManageAccountsUiState.
 */
@RunWith(AndroidJUnit4::class)
class ManageAccountsFilterIntegrationTest {

    private val accounts = listOf(
        item("1", "Ana Costa",    "ana@a.com",    AccountRole.Player),
        item("2", "Bruno Silva",  "bruno@a.com",  AccountRole.Organizer),
        item("3", "Carlos Matos", "carlos@a.com", AccountRole.PlayerOrganizer),
        item("4", "Ana Lima",     "alima@a.com",  AccountRole.Player)
    )

    @Test
    fun searchByName_returnsMatchingAccountsOnly() {
        val state = ManageAccountsUiState(users = accounts, searchQuery = "ana")
        val ids = state.filteredUsers.map { it.id }
        assertTrue(ids.contains("1"))
        assertTrue(ids.contains("4"))
        assertEquals(2, state.filteredUsers.size)
    }

    @Test
    fun roleFilter_playerOnly_excludesOrganizersAndPlayerOrganizers() {
        val state = ManageAccountsUiState(
            users = accounts,
            selectedRoleFilters = setOf(AccountRole.Player)
        )
        assertTrue(state.filteredUsers.all { it.role == AccountRole.Player })
        assertEquals(2, state.filteredUsers.size)
    }

    @Test
    fun searchAndRoleFilter_combinedNarrowsResults() {
        val state = ManageAccountsUiState(
            users = accounts,
            searchQuery = "ana",
            selectedRoleFilters = setOf(AccountRole.Organizer, AccountRole.PlayerOrganizer)
        )
        // Ana Costa and Ana Lima are Players → excluded by role filter
        assertTrue(state.filteredUsers.isEmpty())
    }

    @Test
    fun sortNameAZ_withSearch_returnsSortedFilteredList() {
        val state = ManageAccountsUiState(
            users = accounts,
            searchQuery = "ana",
            currentSortOrder = SortOrder.NameAZ
        )
        // Ana Costa < Ana Lima alphabetically
        assertEquals(listOf("1", "4"), state.filteredUsers.map { it.id })
    }

    @Test
    fun sortNameZA_withSearch_returnsReverseSortedList() {
        val state = ManageAccountsUiState(
            users = accounts,
            searchQuery = "ana",
            currentSortOrder = SortOrder.NameZA
        )
        assertEquals(listOf("4", "1"), state.filteredUsers.map { it.id })
    }

    @Test
    fun allFiltersActive_noMatchingAccount_returnsEmptyList() {
        val state = ManageAccountsUiState(
            users = accounts,
            searchQuery = "xyz",
            selectedRoleFilters = setOf(AccountRole.Player)
        )
        assertTrue(state.filteredUsers.isEmpty())
    }

    private fun item(id: String, name: String, email: String, role: AccountRole) =
        ManageAccountItem(id = id, initials = name.take(2), name = name, email = email, role = role)
}
