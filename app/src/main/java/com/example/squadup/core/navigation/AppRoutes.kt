package com.example.squadup.core.navigation

sealed class AppRoutes(val route: String) {
    data object Onboarding : AppRoutes("onboarding")
    data object Home       : AppRoutes("home")
    data object Events     : AppRoutes("events")
    data object Teams      : AppRoutes("teams")
    data object Profile    : AppRoutes("profile")
}
