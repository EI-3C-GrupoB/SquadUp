package com.example.squadup

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.squadup.core.enums.EventStatus
import com.example.squadup.core.enums.SportType
import com.example.squadup.features.profile.myevents.MyEventItem
import com.example.squadup.features.profile.myevents.MyEventsFilter
import com.example.squadup.features.profile.myevents.MyEventsUiState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Integration test: validates that MyEventsUiState correctly combines
 * the search query and status filter to produce filteredEvents.
 * Simulates a real user session where events evolve through different statuses.
 */
@RunWith(AndroidJUnit4::class)
class MyEventsFilterIntegrationTest {

    private val events = listOf(
        event("1", "Torneio de Futebol",  EventStatus.REGISTRATION_OPEN,   isCreator = true),
        event("2", "Copa de Padel",        EventStatus.ONGOING,             isCreator = false),
        event("3", "Liga de Basquetebol",  EventStatus.FINISHED,            isCreator = true),
        event("4", "Campeonato Futsal",    EventStatus.CANCELLED,           isCreator = false),
        event("5", "Torneio de Voleibol",  EventStatus.REGISTRATION_CLOSED, isCreator = true)
    )

    @Test
    fun allFilter_noSearch_returnsAllEvents() {
        val state = MyEventsUiState(events = events, selectedFilter = MyEventsFilter.All)
        assertEquals(5, state.filteredEvents.size)
    }

    @Test
    fun activeFilter_returnsRegistrationOpenOngoingAndClosed() {
        val state = MyEventsUiState(events = events, selectedFilter = MyEventsFilter.Active)
        val statuses = state.filteredEvents.map { it.status }
        assertTrue(statuses.contains(EventStatus.REGISTRATION_OPEN))
        assertTrue(statuses.contains(EventStatus.ONGOING))
        assertTrue(statuses.contains(EventStatus.REGISTRATION_CLOSED))
        assertFalse(statuses.contains(EventStatus.FINISHED))
        assertFalse(statuses.contains(EventStatus.CANCELLED))
    }

    @Test
    fun completedFilter_returnsFinishedAndCancelledOnly() {
        val state = MyEventsUiState(events = events, selectedFilter = MyEventsFilter.Completed)
        val statuses = state.filteredEvents.map { it.status }
        assertTrue(statuses.all { it == EventStatus.FINISHED || it == EventStatus.CANCELLED })
        assertEquals(2, state.filteredEvents.size)
    }

    @Test
    fun searchQuery_torneio_matchesMultipleEvents() {
        val state = MyEventsUiState(events = events, searchQuery = "torneio")
        assertEquals(2, state.filteredEvents.size)
        assertTrue(state.filteredEvents.all { it.title.contains("Torneio", ignoreCase = true) })
    }

    @Test
    fun activeFilterWithSearch_narrowsToActiveMatchingEvents() {
        val state = MyEventsUiState(
            events = events,
            selectedFilter = MyEventsFilter.Active,
            searchQuery = "torneio"
        )
        // "Torneio de Futebol" is REGISTRATION_OPEN (active) ✓
        // "Torneio de Voleibol" is REGISTRATION_CLOSED (active) ✓
        assertEquals(2, state.filteredEvents.size)
    }

    @Test
    fun completedFilterWithSearch_findsOnlyCancelledMatch() {
        val state = MyEventsUiState(
            events = events,
            selectedFilter = MyEventsFilter.Completed,
            searchQuery = "futsal"
        )
        assertEquals(1, state.filteredEvents.size)
        assertEquals("4", state.filteredEvents.first().id)
    }

    @Test
    fun creatorEvents_areIncludedInFilteredResults() {
        val state = MyEventsUiState(events = events, selectedFilter = MyEventsFilter.All)
        val creatorIds = state.filteredEvents.filter { it.isCreator }.map { it.id }
        assertTrue(creatorIds.containsAll(listOf("1", "3", "5")))
    }

    private fun event(id: String, title: String, status: EventStatus, isCreator: Boolean) =
        MyEventItem(
            id = id, title = title, location = "Porto", date = "15 Jun",
            teamsCount = 0, playersCount = 0, status = status,
            sportType = SportType.SOCCER, isCreator = isCreator
        )
}
