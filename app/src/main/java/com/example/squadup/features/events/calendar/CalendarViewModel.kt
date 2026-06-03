package com.example.squadup.features.events.calendar

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class CalendarViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(
        CalendarUiState(
            dailySchedule = listOf(
                DailyScheduleItem(
                    time = "08:00",
                    title = "Morning Conditioning",
                    location = "Main Field"
                ),
                DailyScheduleItem(
                    time = "10:30",
                    title = "Tactical Review",
                    location = "Media Room"
                ),
                DailyScheduleItem(
                    time = "15:00",
                    title = "Scrimmage Match",
                    location = "Practice Area B"
                )
            )
        )
    )

    val uiState: StateFlow<CalendarUiState> = _uiState.asStateFlow()

    fun onPreviousMonthClick() {
        // TODO: alterar mês
    }

    fun onNextMonthClick() {
        // TODO: alterar mês
    }

    fun onTodayClick() {
        // TODO: voltar ao dia atual
    }

    fun onDayClick(day: Int) {
        _uiState.value = _uiState.value.copy(selectedDay = day)
    }
}