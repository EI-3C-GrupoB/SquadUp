package com.example.squadup.features.events.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth

class CalendarViewModel : ViewModel() {

    private val repository = CalendarRepository()
    private val _uiState = MutableStateFlow(CalendarUiState())
    val uiState: StateFlow<CalendarUiState> = _uiState.asStateFlow()

    init {
        val today = LocalDate.now()
        loadCalendar(today.year, today.monthValue, today.dayOfMonth)
    }

    fun onPreviousMonthClick() {
        val current = YearMonth.of(_uiState.value.currentYear, _uiState.value.currentMonth)
        val prev = current.minusMonths(1)
        loadCalendar(prev.year, prev.monthValue, 1)
    }

    fun onNextMonthClick() {
        val current = YearMonth.of(_uiState.value.currentYear, _uiState.value.currentMonth)
        val next = current.plusMonths(1)
        loadCalendar(next.year, next.monthValue, 1)
    }

    fun onTodayClick() {
        val today = LocalDate.now()
        loadCalendar(today.year, today.monthValue, today.dayOfMonth)
    }

    fun onDayClick(day: Int) {
        loadCalendar(_uiState.value.currentYear, _uiState.value.currentMonth, day)
    }

    private fun loadCalendar(year: Int, month: Int, selectedDay: Int) {
        viewModelScope.launch {
            repository.getCalendar(year, month, selectedDay).onSuccess { calendar ->
                _uiState.value = calendar
            }
        }
    }
}
