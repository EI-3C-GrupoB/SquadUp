package com.example.squadup

import com.example.squadup.features.events.moredetails.MoreDetailsUiState
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class MoreDetailsUiStateTest {

    @Test
    fun hasImage_whenImageUrlIsNotBlank_returnsTrue() {
        val state = MoreDetailsUiState(imageUrl = "https://example.com/img.jpg")
        assertTrue(state.hasImage)
    }

    @Test
    fun hasImage_whenImageUrlIsNull_returnsFalse() {
        val state = MoreDetailsUiState(imageUrl = null)
        assertFalse(state.hasImage)
    }

    @Test
    fun hasImage_whenImageUrlIsBlank_returnsFalse() {
        val state = MoreDetailsUiState(imageUrl = "  ")
        assertFalse(state.hasImage)
    }

    @Test
    fun hasDescription_whenDescriptionIsNotBlank_returnsTrue() {
        val state = MoreDetailsUiState(description = "Torneio anual de futebol")
        assertTrue(state.hasDescription)
    }

    @Test
    fun hasDescription_whenDescriptionIsBlank_returnsFalse() {
        val state = MoreDetailsUiState(description = "")
        assertFalse(state.hasDescription)
    }

    @Test
    fun hasRules_whenRulesListIsNotEmpty_returnsTrue() {
        val state = MoreDetailsUiState(rules = listOf("Regra 1", "Regra 2"))
        assertTrue(state.hasRules)
    }

    @Test
    fun hasRules_whenRulesListIsEmpty_returnsFalse() {
        val state = MoreDetailsUiState(rules = emptyList())
        assertFalse(state.hasRules)
    }

    @Test
    fun hasLocation_whenBothLatAndLonArePresent_returnsTrue() {
        val state = MoreDetailsUiState(latitude = 41.15, longitude = -8.61)
        assertTrue(state.hasLocation)
    }

    @Test
    fun hasLocation_whenLatitudeIsNull_returnsFalse() {
        val state = MoreDetailsUiState(latitude = null, longitude = -8.61)
        assertFalse(state.hasLocation)
    }

    @Test
    fun hasLocation_whenLongitudeIsNull_returnsFalse() {
        val state = MoreDetailsUiState(latitude = 41.15, longitude = null)
        assertFalse(state.hasLocation)
    }

    @Test
    fun privateEvent_withNoCodeVerified_needsCodeEntry() {
        val state = MoreDetailsUiState(
            isPrivate = true,
            canManageEvent = false,
            userEventRegistrationStatus = null,
            codeVerified = false
        )
        val needsCode = state.isPrivate && !state.canManageEvent
            && state.userEventRegistrationStatus.isNullOrBlank()
            && !state.codeVerified
        assertTrue(needsCode)
    }

    @Test
    fun privateEvent_afterCodeVerified_noLongerNeedsCodeEntry() {
        val state = MoreDetailsUiState(
            isPrivate = true,
            canManageEvent = false,
            userEventRegistrationStatus = null,
            codeVerified = true
        )
        val needsCode = state.isPrivate && !state.canManageEvent
            && state.userEventRegistrationStatus.isNullOrBlank()
            && !state.codeVerified
        assertFalse(needsCode)
    }
}
