package com.example.squadup.features.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class OnboardingViewModel(
    private val preferences: OnboardingPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    val hasCompletedOnboarding = preferences.hasCompletedOnboarding

    fun onPageChanged(page: Int, totalPages: Int) {
        _uiState.update { state ->
            state.copy(
                currentPage = page,
                isLastPage = page == totalPages - 1
            )
        }
    }

    fun onFinish(onNavigate: () -> Unit) {
        viewModelScope.launch {
            preferences.setOnboardingCompleted()
            onNavigate()
        }
    }
}