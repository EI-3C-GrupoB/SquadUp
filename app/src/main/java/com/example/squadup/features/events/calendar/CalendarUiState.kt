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
    val highlightedMatch: CalendarMatchItem = CalendarMatchItem(),
    val dailySchedule: List<DailyScheduleItem> = emptyList(),
    val nextAwayGame: AwayGameItem = AwayGameItem()
)

data class CalendarMatchItem(
    val label: String = "",
    val title: String = "",
    val homeTeam: String = "",
    val awayTeam: String = ""
)

data class DailyScheduleItem(
    val time: String,
    val title: String,
    val location: String
)

data class AwayGameItem(
    val label: String = "Próximo jogo fora",
    val city: String = "",
    val date: String = ""
)
