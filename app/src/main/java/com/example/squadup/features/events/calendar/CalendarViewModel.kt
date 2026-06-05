package com.example.squadup.features.events.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CalendarViewModel : ViewModel() {

    private val repository = CalendarRepository()
    private val _uiState = MutableStateFlow(CalendarUiState())
    val uiState: StateFlow<CalendarUiState> = _uiState.asStateFlow()

    init {
        loadCalendar(_uiState.value.selectedDay)
    }

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
        loadCalendar(day)
    }

    private fun loadCalendar(selectedDay: Int) {
        viewModelScope.launch {
            repository.getCalendar(selectedDay).onSuccess { calendar ->
                _uiState.value = calendar
            }
        }
    }
}
