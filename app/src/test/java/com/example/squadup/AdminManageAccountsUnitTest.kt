package com.example.squadup

import com.example.squadup.features.admin.manageaccounts.AccountRole
import com.example.squadup.features.admin.manageaccounts.ManageAccountItem
import com.example.squadup.features.admin.manageaccounts.ManageAccountRow
import com.example.squadup.features.admin.manageaccounts.ManageAccountsUiState
import com.example.squadup.features.admin.manageaccounts.SortOrder
import com.example.squadup.features.admin.manageaccounts.toAccountRole
import com.example.squadup.features.admin.manageaccounts.toAccountUpdatePayload
import com.example.squadup.features.admin.manageaccounts.toManageAccountItem
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AdminManageAccountsUnitTest {

    @Test
    fun manageAccountRow_withAdminFlag_mapsToAdminRole() {
        val row = ManageAccountRow(
            id = 1,
            name = "Admin User",
            isAdmin = true,
            accountType = 1
        )

        assertEquals(AccountRole.Admin, row.toAccountRole())
    }

    @Test
    fun manageAccountRow_withOrganizerAccountType_mapsToOrganizerRole() {
        val row = ManageAccountRow(
            id = 2,
            name = "Organizer User",
            isAdmin = false,
            accountType = 2
        )

        assertEquals(AccountRole.Organizer, row.toAccountRole())
    }

    @Test
    fun manageAccountRow_withoutAdminOrOrganizer_mapsToPlayerRole() {
        val row = ManageAccountRow(
            id = 3,
            name = "Player User",
            isAdmin = false,
            accountType = 1
        )

        assertEquals(AccountRole.Player, row.toAccountRole())
    }

    @Test
    fun manageAccountRow_toManageAccountItem_usesNameAndEmailFromDatabase() {
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
    fun accountRole_toAccountUpdatePayload_usesDatabaseColumnsForAdmin() {
        val payload = AccountRole.Admin.toAccountUpdatePayload()

        assertEquals(true, payload["is_admin"])
        assertEquals(2, payload["tipo_conta"])
    }

    @Test
    fun accountRole_toAccountUpdatePayload_usesDatabaseColumnsForPlayer() {
        val payload = AccountRole.Player.toAccountUpdatePayload()

        assertEquals(false, payload["is_admin"])
        assertEquals(1, payload["tipo_conta"])
    }

    @Test
    fun manageAccountsUiState_filteredUsers_appliesRoleFilterSearchAndSort() {
        val state = ManageAccountsUiState(
            searchQuery = "joao",
            users = listOf(
                accountItem(id = "1", name = "Ana", email = "ana@example.com", role = AccountRole.Admin),
                accountItem(id = "2", name = "Joao Silva", email = "joao@example.com", role = AccountRole.Player),
                accountItem(id = "3", name = "Joao Sousa", email = "sousa@example.com", role = AccountRole.Organizer)
            ),
            selectedRoleFilters = setOf(AccountRole.Player, AccountRole.Organizer),
            currentSortOrder = SortOrder.NameZA
        )

        assertEquals(listOf("3", "2"), state.filteredUsers.map { it.id })
        assertFalse(state.filteredUsers.any { it.role == AccountRole.Admin })
    }

    private fun accountItem(
        id: String,
        name: String,
        email: String,
        role: AccountRole
    ): ManageAccountItem {
        return ManageAccountItem(
            id = id,
            initials = name.take(1),
            name = name,
            email = email,
            role = role
        )
    }
}
