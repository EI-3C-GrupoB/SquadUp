package com.example.squadup.core.navigation

import android.app.Application
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.squadup.core.app.AppViewModel
import com.example.squadup.features.onboarding.OnboardingPreferences
import com.example.squadup.features.onboarding.OnboardingRoute
import com.example.squadup.features.onboarding.OnboardingViewModel
import com.example.squadup.features.onboarding.OnboardingViewModelFactory
import com.example.squadup.features.home.HomeRoute

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val context = LocalContext.current

    val appViewModel: AppViewModel = viewModel(
        factory = ViewModelProvider.AndroidViewModelFactory.getInstance(
            context.applicationContext as Application
        )
    )

    val onboardingViewModel: OnboardingViewModel = viewModel(
        factory = OnboardingViewModelFactory(
            preferences = OnboardingPreferences(context)
        )
    )

    NavHost(
        navController = navController,
        startDestination = AppRoutes.Onboarding.route
    ) {
        composable(AppRoutes.Onboarding.route) {
            OnboardingRoute(
                viewModel = onboardingViewModel,
                appViewModel = appViewModel,
                onFinish = {
                    navController.navigate(AppRoutes.Home.route) {
                        popUpTo(AppRoutes.Onboarding.route) { inclusive = true }
                    }
                },
                onLoginClick = {
                    // TODO: navegar para Auth
                }
            )
        }

        composable(AppRoutes.Home.route) {
            HomeRoute(
                selectedRoute = AppRoutes.Home.route,
                onNavItemClick = { route ->
                    navController.navigate(route) {
                        popUpTo(AppRoutes.Home.route) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                onNotificationsClick = {
                    // TODO: navegar para Notificações
                },
                onViewMatchDetailsClick = {
                    // TODO: navegar para Detalhe do Jogo
                },
                onSeeAllEventsClick = {
                    // TODO: navegar para Eventos
                },
                onJoinEventClick = { eventId ->
                    // TODO: navegar para Detalhe do Evento
                },
                onEventDetailsClick = { eventId ->
                    // TODO: navegar para Detalhe do Evento
                },
                onLoginClick = {
                    // TODO: navegar para Auth
                },
                onRegisterClick = {
                    // TODO: navegar para Auth
                },
                appViewModel = appViewModel
            )
        }

        composable(AppRoutes.Events.route) {
            HomeRoute(
                selectedRoute = AppRoutes.Events.route,
                onNavItemClick = { route ->
                    navController.navigate(route) {
                        popUpTo(AppRoutes.Home.route) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                onNotificationsClick = {},
                onViewMatchDetailsClick = {},
                onSeeAllEventsClick = {},
                onJoinEventClick = {},
                onEventDetailsClick = {},
                onLoginClick = {},
                onRegisterClick = {},
                appViewModel = appViewModel
            )
        }

        composable(AppRoutes.Teams.route) {
            HomeRoute(
                selectedRoute = AppRoutes.Teams.route,
                onNavItemClick = { route ->
                    navController.navigate(route) {
                        popUpTo(AppRoutes.Home.route) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                onNotificationsClick = {},
                onViewMatchDetailsClick = {},
                onSeeAllEventsClick = {},
                onJoinEventClick = {},
                onEventDetailsClick = {},
                onLoginClick = {},
                onRegisterClick = {},
                appViewModel = appViewModel
            )
        }

        composable(AppRoutes.Profile.route) {
            HomeRoute(
                selectedRoute = AppRoutes.Profile.route,
                onNavItemClick = { route ->
                    navController.navigate(route) {
                        popUpTo(AppRoutes.Home.route) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                onNotificationsClick = {},
                onViewMatchDetailsClick = {},
                onSeeAllEventsClick = {},
                onJoinEventClick = {},
                onEventDetailsClick = {},
                onLoginClick = {},
                onRegisterClick = {},
                appViewModel = appViewModel
            )
        }
    }
}