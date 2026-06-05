package com.example.squadup.features.profile.myevents

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MyEventsViewModel : ViewModel() {

    private val repository = MyEventsRepository()
    private val _uiState = MutableStateFlow(MyEventsUiState())
    val uiState: StateFlow<MyEventsUiState> = _uiState.asStateFlow()

    init {
        loadMyEvents()
    }

    private fun loadMyEvents() {
        viewModelScope.launch {
            repository.getMyEvents().onSuccess { events ->
                _uiState.value = _uiState.value.copy(events = events)
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
    }

    fun onFilterChange(filter: MyEventsFilter) {
        _uiState.value = _uiState.value.copy(selectedFilter = filter)
    }
}
