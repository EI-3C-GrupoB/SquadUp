package com.example.squadup.features.profile.myevents

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MyEventsViewModel : ViewModel() {

    private val repository = MyEventsRepository()

    private val _uiState = MutableStateFlow(MyEventsUiState(isLoading = true))
    val uiState: StateFlow<MyEventsUiState> = _uiState.asStateFlow()

    init {
        loadEvents()
    }

    fun loadEvents() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            repository.getMyEvents()
                .onSuccess { events ->
                    _uiState.value = _uiState.value.copy(isLoading = false, events = events)
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = exception.message ?: "Não foi possível carregar os teus eventos."
                    )
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
