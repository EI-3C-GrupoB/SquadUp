package com.example.squadup

import com.example.squadup.core.enums.EventStatus
import com.example.squadup.core.enums.SportType
import com.example.squadup.features.profile.myevents.MyEventItem
import com.example.squadup.features.profile.myevents.MyEventsFilter
import com.example.squadup.features.profile.myevents.MyEventsUiState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class MyEventsUiStateTest {

    private val sampleEvents = listOf(
        event(id = "1", title = "Torneio Futebol", status = EventStatus.REGISTRATION_OPEN),
        event(id = "2", title = "Liga Padel", status = EventStatus.ONGOING),
        event(id = "3", title = "Campeonato Basquet", status = EventStatus.FINISHED),
        event(id = "4", title = "Copa Futsal", status = EventStatus.CANCELLED)
    )

    @Test
    fun filteredEvents_noFilters_returnsAll() {
        val state = MyEventsUiState(events = sampleEvents)
        assertEquals(4, state.filteredEvents.size)
    }

    @Test
    fun filteredEvents_activeFilter_returnsOnlyActiveEvents() {
        val state = MyEventsUiState(
            events = sampleEvents,
            selectedFilter = MyEventsFilter.Active
        )
        val ids = state.filteredEvents.map { it.id }
        assertTrue(ids.contains("1"))
        assertTrue(ids.contains("2"))
        assertFalse(ids.contains("3"))
        assertFalse(ids.contains("4"))
    }

    @Test
    fun filteredEvents_completedFilter_returnsOnlyFinishedAndCancelled() {
        val state = MyEventsUiState(
            events = sampleEvents,
            selectedFilter = MyEventsFilter.Completed
        )
        val ids = state.filteredEvents.map { it.id }
        assertTrue(ids.contains("3"))
        assertTrue(ids.contains("4"))
        assertFalse(ids.contains("1"))
        assertFalse(ids.contains("2"))
    }

    @Test
    fun filteredEvents_searchQuery_filtersByTitle() {
        val state = MyEventsUiState(
            events = sampleEvents,
            searchQuery = "padel"
        )
        assertEquals(1, state.filteredEvents.size)
        assertEquals("2", state.filteredEvents.first().id)
    }

    @Test
    fun filteredEvents_searchQuery_isCaseInsensitive() {
        val state = MyEventsUiState(events = sampleEvents, searchQuery = "FUTEBOL")
        assertEquals(1, state.filteredEvents.size)
        assertEquals("1", state.filteredEvents.first().id)
    }

    @Test
    fun filteredEvents_searchQueryAndActiveFilter_combinesCorrectly() {
        val state = MyEventsUiState(
            events = sampleEvents,
            searchQuery = "copa",
            selectedFilter = MyEventsFilter.Active
        )
        // Copa Futsal is CANCELLED, not active → should be excluded
        assertTrue(state.filteredEvents.isEmpty())
    }

    @Test
    fun filteredEvents_emptySearchQuery_doesNotFilter() {
        val state = MyEventsUiState(events = sampleEvents, searchQuery = "")
        assertEquals(4, state.filteredEvents.size)
    }

    private fun event(id: String, title: String, status: EventStatus) = MyEventItem(
        id = id,
        title = title,
        location = "Lisboa",
        date = "15 Jun",
        teamsCount = 0,
        playersCount = 0,
        status = status,
        sportType = SportType.SOCCER
    )
}
