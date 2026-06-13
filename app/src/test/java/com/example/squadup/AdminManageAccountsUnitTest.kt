package com.example.squadup

import com.example.squadup.features.admin.manageaccounts.AccountRole
import com.example.squadup.features.admin.manageaccounts.ManageAccountItem
import com.example.squadup.features.admin.manageaccounts.ManageAccountRow
import com.example.squadup.features.admin.manageaccounts.ManageAccountsUiState
import com.example.squadup.features.admin.manageaccounts.SortOrder
import com.example.squadup.features.admin.manageaccounts.toAccountRole
import com.example.squadup.features.admin.manageaccounts.toAccountType
import com.example.squadup.features.admin.manageaccounts.toManageAccountItem
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AdminManageAccountsUnitTest {

    // ── toAccountRole ──────────────────────────────────────────────────────────

    @Test
    fun manageAccountRow_withAccountType3_mapsToPlayerOrganizer() {
        val row = ManageAccountRow(id = 1, name = "User", accountType = 3)
        assertEquals(AccountRole.PlayerOrganizer, row.toAccountRole())
    }

    @Test
    fun manageAccountRow_withAccountType2_mapsToOrganizer() {
        val row = ManageAccountRow(id = 2, name = "Organizer User", accountType = 2)
        assertEquals(AccountRole.Organizer, row.toAccountRole())
    }

    @Test
    fun manageAccountRow_withAccountType1_mapsToPlayer() {
        val row = ManageAccountRow(id = 3, name = "Player User", accountType = 1)
        assertEquals(AccountRole.Player, row.toAccountRole())
    }

    @Test
    fun manageAccountRow_withNullAccountType_defaultsToPlayer() {
        val row = ManageAccountRow(id = 4, name = "Unknown", accountType = null)
        assertEquals(AccountRole.Player, row.toAccountRole())
    }

    // ── toAccountType ──────────────────────────────────────────────────────────

    @Test
    fun accountRole_playerOrganizer_toAccountType_returns3() {
        assertEquals(3, AccountRole.PlayerOrganizer.toAccountType())
    }

    @Test
    fun accountRole_organizer_toAccountType_returns2() {
        assertEquals(2, AccountRole.Organizer.toAccountType())
    }

    @Test
    fun accountRole_player_toAccountType_returns1() {
        assertEquals(1, AccountRole.Player.toAccountType())
    }

    // ── toManageAccountItem ────────────────────────────────────────────────────

    @Test
    fun manageAccountRow_toManageAccountItem_mapsFieldsCorrectly() {
        val row = ManageAccountRow(
            id = 4,
            name = "Maria Silva",
            username = "maria_silva",
            email = "maria@example.com",
            accountType = 1
        )
        val item = row.toManageAccountItem()

        assertEquals("4", item.id)
        assertEquals("Maria Silva", item.name)
        assertEquals("MS", item.initials)
        assertEquals("maria@example.com", item.email)
    }

    @Test
    fun manageAccountRow_toManageAccountItem_marksSuspendedWhenAccountStateIsSuspenso() {
        val row = ManageAccountRow(
            id = 5,
            name = "Suspended User",
            email = "suspended@example.com",
            accountState = "suspenso"
        )
        assertTrue(row.toManageAccountItem().isSuspended)
    }

    @Test
    fun manageAccountRow_toManageAccountItem_notSuspendedWhenStateIsAtivo() {
        val row = ManageAccountRow(
            id = 6,
            name = "Active User",
            email = "active@example.com",
            accountState = "ativo"
        )
        assertFalse(row.toManageAccountItem().isSuspended)
    }

    @Test
    fun manageAccountRow_toManageAccountItem_fallsBackToUsernameWhenNameIsBlank() {
        val row = ManageAccountRow(id = 7, name = "", username = "jsilva", email = "j@example.com")
        assertEquals("jsilva", row.toManageAccountItem().name)
    }

    // ── ManageAccountsUiState.filteredUsers ────────────────────────────────────

    @Test
    fun manageAccountsUiState_filteredUsers_appliesRoleFilterSearchAndSort() {
        val state = ManageAccountsUiState(
            searchQuery = "joao",
            users = listOf(
                accountItem(id = "1", name = "Ana", email = "ana@example.com", role = AccountRole.PlayerOrganizer),
                accountItem(id = "2", name = "Joao Silva", email = "joao@example.com", role = AccountRole.Player),
                accountItem(id = "3", name = "Joao Sousa", email = "sousa@example.com", role = AccountRole.Organizer)
            ),
            selectedRoleFilters = setOf(AccountRole.Player, AccountRole.Organizer),
            currentSortOrder = SortOrder.NameZA
        )

        assertEquals(listOf("3", "2"), state.filteredUsers.map { it.id })
        assertFalse(state.filteredUsers.any { it.role == AccountRole.PlayerOrganizer })
    }

    @Test
    fun manageAccountsUiState_filteredUsers_emptyFilterShowsAll() {
        val state = ManageAccountsUiState(
            users = listOf(
                accountItem(id = "1", name = "Ana", role = AccountRole.Player),
                accountItem(id = "2", name = "Bruno", role = AccountRole.Organizer)
            ),
            selectedRoleFilters = emptySet()
        )
        assertEquals(2, state.filteredUsers.size)
    }

    @Test
    fun manageAccountsUiState_filteredUsers_sortNameAZ() {
        val state = ManageAccountsUiState(
            users = listOf(
                accountItem(id = "1", name = "Zara", role = AccountRole.Player),
                accountItem(id = "2", name = "Ana", role = AccountRole.Player)
            ),
            currentSortOrder = SortOrder.NameAZ
        )
        assertEquals(listOf("2", "1"), state.filteredUsers.map { it.id })
    }

    // ── helpers ────────────────────────────────────────────────────────────────

    private fun accountItem(
        id: String,
        name: String,
        email: String = "$name@example.com",
        role: AccountRole
    ) = ManageAccountItem(id = id, initials = name.take(1), name = name, email = email, role = role)
}
