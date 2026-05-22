package com.example.squadup.ui.screens.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.squadup.data.repositories.AuthRepository
import com.example.squadup.data.supabase.SupabaseClientProvider

@Composable
fun HomeRoute(
    selectedRoute: String,
    onNavItemClick: (String) -> Unit,
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onViewMatchDetailsClick: () -> Unit,
    onSeeAllEventsClick: () -> Unit,
    onJoinEventClick: () -> Unit
) {
    val authRepository = AuthRepository(
        supabaseClient = SupabaseClientProvider.client
    )

    val viewModel: HomeViewModel = viewModel(
        factory = HomeViewModelFactory(authRepository)
    )

    val uiState by viewModel.uiState.collectAsState()

    HomeScreen(
        uiState = uiState,
        selectedRoute = selectedRoute,
        onNavItemClick = onNavItemClick,
        onLoginClick = onLoginClick,
        onRegisterClick = onRegisterClick,
        onNotificationsClick = onNotificationsClick,
        onSettingsClick = onSettingsClick,
        onViewMatchDetailsClick = {
            if (uiState.isLoggedIn) onViewMatchDetailsClick() else onLoginClick()
        },
        onSeeAllEventsClick = onSeeAllEventsClick,
        onJoinEventClick = {
            if (uiState.isLoggedIn) onJoinEventClick() else onLoginClick()
        }
    )
}
