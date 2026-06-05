package com.example.squadup.features.events.calendar

data class CalendarUiState(
    val monthTitle: String = "",
    val matchesScheduled: Int = 0,
    val selectedDay: Int = java.time.LocalDate.now().dayOfMonth,
    val eventDays: Set<Int> = emptySet(),
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
    val label: String = "Next Away Game",
    val city: String = "",
    val date: String = ""
)
