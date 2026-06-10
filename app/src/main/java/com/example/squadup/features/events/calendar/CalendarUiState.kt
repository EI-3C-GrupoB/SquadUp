package com.example.squadup.features.events.calendar

import java.time.LocalDate

data class CalendarUiState(
    val monthTitle: String = "",
    val matchesScheduled: Int = 0,
    val selectedDay: Int = LocalDate.now().dayOfMonth,
    val currentYear: Int = LocalDate.now().year,
    val currentMonth: Int = LocalDate.now().monthValue,
    val eventDays: Set<Int> = emptySet(),
    val calendarCells: List<Int?> = emptyList(),
    val highlightedMatch: CalendarMatchItem? = null,
    val dailySchedule: List<DailyScheduleItem> = emptyList()
)

data class CalendarMatchItem(
    val label: String = "",
    val homeTeam: String = "",
    val awayTeam: String = "",
    val time: String = "",
    val location: String = "",
    val gameId: Int = 0,
    val eventId: Int = 0,
    val ticketId: Int = 0
)

data class DailyScheduleItem(
    val gameId: Int = 0,
    val eventId: Int = 0,
    val ticketId: Int = 0,
    val time: String = "",
    val homeTeam: String = "",
    val awayTeam: String = "",
    val eventName: String = "",
    val location: String = ""
)
