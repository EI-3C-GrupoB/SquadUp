package com.example.squadup.features.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private val repository = HomeRepository()
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private var fetchJob: kotlinx.coroutines.Job? = null

    init {
        // Carrega imediatamente detectando a sessão do Supabase directamente
        loadHome()
    }

    // Chamado pelo LaunchedEffect no HomeRoute quando o AppViewModel
    // termina de carregar o utilizador — garante dados frescos com o userId correcto
    fun loadHome(userId: Int? = null, displayName: String = "") {
        fetchJob?.cancel()
        fetchJob = viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            repository.getHome(userId, displayName)
                .onSuccess { home -> _uiState.value = home }
                .onFailure { _uiState.value = _uiState.value.copy(isLoading = false) }
        }
    }
}
