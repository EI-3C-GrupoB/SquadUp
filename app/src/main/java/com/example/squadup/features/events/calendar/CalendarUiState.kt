package com.example.squadup.features.events.calendar

data class CalendarUiState(
    val monthTitle: String = "April 2026",
    val matchesScheduled: Int = 4,
    val selectedDay: Int = 9,
    val eventDays: Set<Int> = setOf(9, 12, 19, 20, 26),
    val highlightedMatch: CalendarMatchItem = CalendarMatchItem(),
    val dailySchedule: List<DailyScheduleItem> = emptyList(),
    val nextAwayGame: AwayGameItem = AwayGameItem()
)

data class CalendarMatchItem(
    val label: String = "UPCOMING: OCT 12",
    val title: String = "The Mavericks vs.\nRed Eagles",
    val homeTeam: String = "Strikers",
    val awayTeam: String = "Metro"
)

data class DailyScheduleItem(
    val time: String,
    val title: String,
    val location: String
)

data class AwayGameItem(
    val label: String = "Next Away Game",
    val city: String = "Chicago Fire",
    val date: String = "Oct 19 • Soldier Field"
)