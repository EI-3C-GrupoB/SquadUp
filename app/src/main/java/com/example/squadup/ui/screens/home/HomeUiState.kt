package com.example.squadup.ui.screens.home

data class HomeUiState(
    val isLoggedIn: Boolean = false,
    val displayName: String = "Player",
    val currentMatch: HomeMatch? = null,
    val nearbyEvents: List<HomeEvent> = emptyList(),
    val teams: List<HomeTeam> = emptyList()
)

data class HomeMatch(
    val title: String,
    val date: String,
    val location: String
)

data class HomeEvent(
    val title: String,
    val location: String,
    val distance: String,
    val intensity: Float
)

data class HomeTeam(
    val name: String,
    val details: String,
    val badge: String? = null
)
