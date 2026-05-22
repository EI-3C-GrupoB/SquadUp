package com.example.squadup.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.squadup.data.repositories.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class HomeViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(createInitialState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private fun createInitialState(): HomeUiState {
        val isLoggedIn = authRepository.isLoggedIn()
        val displayName = authRepository.currentUserEmail()
            ?.substringBefore("@")
            ?.replaceFirstChar { firstChar ->
                if (firstChar.isLowerCase()) firstChar.titlecase() else firstChar.toString()
            }
            ?: "Player"

        return HomeUiState(
            isLoggedIn = isLoggedIn,
            displayName = displayName,
            currentMatch = if (isLoggedIn) {
                HomeMatch(
                    title = "City Finals: Red\nDragons vs. Blue\nHawks",
                    date = "Today, 18:30",
                    location = "Central Arena"
                )
            } else {
                null
            },
            nearbyEvents = listOf(
                HomeEvent(
                    title = "Open Tennis Doubles",
                    location = "Westside Club",
                    distance = "2.4km",
                    intensity = 0.65f
                ),
                HomeEvent(
                    title = "3x3 Street Basketball",
                    location = "Central Court",
                    distance = "1.1km",
                    intensity = 0.85f
                )
            ),
            teams = if (isLoggedIn) {
                listOf(
                    HomeTeam(
                        name = "The Mavericks",
                        details = "12 Members - Soccer",
                        badge = "LEADER"
                    ),
                    HomeTeam(
                        name = "Midnight Hoop",
                        details = "8 Members - Basketball"
                    )
                )
            } else {
                emptyList()
            }
        )
    }
}

class HomeViewModelFactory(
    private val authRepository: AuthRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(authRepository) as T
        }

        throw IllegalArgumentException("ViewModel desconhecido: ${modelClass.name}")
    }
}
