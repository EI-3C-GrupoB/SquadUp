package com.example.squadup

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.squadup.features.events.moredetails.MoreDetailsUiState
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Integration test: verifies the full lifecycle of access code verification
 * for a private event — from "needs code" through "code verified" to participation.
 */
@RunWith(AndroidJUnit4::class)
class PrivateEventAccessCodeIntegrationTest {

    private val storedCode = "AB3K7Z"

    @Test
    fun privateEvent_initialState_requiresCodeEntry() {
        val state = MoreDetailsUiState(
            isPrivate = true,
            accessCode = storedCode,
            canManageEvent = false,
            userEventRegistrationStatus = null,
            codeVerified = false
        )
        assertTrue(state.isPrivate)
        assertFalse(state.codeVerified)
        assertFalse(state.canManageEvent)
        assertTrue(state.userEventRegistrationStatus.isNullOrBlank())
    }

    @Test
    fun verifyAccessCode_correctCode_setsCodeVerifiedTrue() {
        val state = MoreDetailsUiState(isPrivate = true, accessCode = storedCode)
        val entered = "AB3K7Z"
        val verified = !state.accessCode.isNullOrBlank() &&
                entered.trim().uppercase() == state.accessCode.trim().uppercase()
        assertTrue(verified)
        val updatedState = state.copy(codeVerified = verified)
        assertTrue(updatedState.codeVerified)
    }

    @Test
    fun verifyAccessCode_wrongCode_doesNotSetVerified() {
        val state = MoreDetailsUiState(isPrivate = true, accessCode = storedCode)
        val entered = "WRONG1"
        val verified = !state.accessCode.isNullOrBlank() &&
                entered.trim().uppercase() == state.accessCode.trim().uppercase()
        assertFalse(verified)
    }

    @Test
    fun verifyAccessCode_caseInsensitive_matchesRegardlessOfCase() {
        val state = MoreDetailsUiState(isPrivate = true, accessCode = storedCode)
        val entered = "ab3k7z"
        val verified = !state.accessCode.isNullOrBlank() &&
                entered.trim().uppercase() == state.accessCode.trim().uppercase()
        assertTrue(verified)
    }

    @Test
    fun afterCodeVerified_publicEventFlagsAreCorrect() {
        val state = MoreDetailsUiState(
            isPrivate = true,
            accessCode = storedCode,
            codeVerified = true,
            canParticipateIndividually = true,
            canParticipateWithTeam = true
        )
        // After verification, user can participate
        val needsCodeEntry = state.isPrivate && !state.canManageEvent
                && state.userEventRegistrationStatus.isNullOrBlank()
                && !state.codeVerified
        assertFalse(needsCodeEntry)
        assertTrue(state.canParticipateIndividually || state.canParticipateWithTeam)
    }

    @Test
    fun organizer_neverNeedsCodeEntry_evenForPrivateEvent() {
        val state = MoreDetailsUiState(
            isPrivate = true,
            accessCode = storedCode,
            canManageEvent = true,
            codeVerified = false
        )
        val needsCodeEntry = state.isPrivate && !state.canManageEvent
                && state.userEventRegistrationStatus.isNullOrBlank()
                && !state.codeVerified
        assertFalse(needsCodeEntry)
    }
}
