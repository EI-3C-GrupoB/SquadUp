package com.example.squadup

import com.example.squadup.features.admin.manageaccounts.AccountRole
import com.example.squadup.features.admin.manageaccounts.edituser.EditUserRow
import com.example.squadup.features.admin.manageaccounts.edituser.toEditUserUiState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class EditUserModelsTest {

    @Test
    fun editUserRow_withName_usesNameAsDisplayName() {
        val row = EditUserRow(id = 1, name = "Carlos Matos", email = "carlos@example.com")
        val state = row.toEditUserUiState()
        assertEquals("Carlos Matos", state.userName)
    }

    @Test
    fun editUserRow_withBlankName_fallsBackToUsername() {
        val row = EditUserRow(id = 1, name = "", username = "cmatos", email = "carlos@example.com")
        val state = row.toEditUserUiState()
        assertEquals("cmatos", state.userName)
    }

    @Test
    fun editUserRow_withNoNameOrUsername_fallsBackToEmailPrefix() {
        val row = EditUserRow(id = 1, name = null, username = null, email = "carlos@example.com")
        val state = row.toEditUserUiState()
        assertEquals("carlos", state.userName)
    }

    @Test
    fun editUserRow_withAccountType2_mapsToOrganizerRole() {
        val row = EditUserRow(id = 1, name = "Test", accountType = 2)
        assertEquals(AccountRole.Organizer, row.toEditUserUiState().selectedRole)
    }

    @Test
    fun editUserRow_withAccountType3_mapsToPlayerOrganizerRole() {
        val row = EditUserRow(id = 1, name = "Test", accountType = 3)
        assertEquals(AccountRole.PlayerOrganizer, row.toEditUserUiState().selectedRole)
    }

    @Test
    fun editUserRow_withAccountType1_mapsToPlayerRole() {
        val row = EditUserRow(id = 1, name = "Test", accountType = 1)
        assertEquals(AccountRole.Player, row.toEditUserUiState().selectedRole)
    }

    @Test
    fun editUserRow_withSuspensoState_marksSuspended() {
        val row = EditUserRow(id = 1, name = "Test", accountState = "suspenso")
        assertTrue(row.toEditUserUiState().isSuspended)
    }

    @Test
    fun editUserRow_withAtivoState_notSuspended() {
        val row = EditUserRow(id = 1, name = "Test", accountState = "ativo")
        assertFalse(row.toEditUserUiState().isSuspended)
    }

    @Test
    fun editUserRow_initialsFromFullName_takesTwoWords() {
        val row = EditUserRow(id = 1, name = "Ana Beatriz Costa")
        val state = row.toEditUserUiState()
        assertEquals("AB", state.userInitials)
    }

    @Test
    fun editUserRow_initialsFromSingleName_takesSingleLetter() {
        val row = EditUserRow(id = 1, name = "Pedro")
        val state = row.toEditUserUiState()
        assertEquals("P", state.userInitials)
    }
}
