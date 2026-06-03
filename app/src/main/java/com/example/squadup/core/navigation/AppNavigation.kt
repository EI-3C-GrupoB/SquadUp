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
import com.example.squadup.features.events.EventsRoute
import com.example.squadup.features.organizer.createevent.CreateEventRoute
import com.example.squadup.features.home.HomeRoute
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.squadup.features.admin.manageaccounts.createuser.CreateUserRoute
import com.example.squadup.features.organizer.myevents.MyEventsRoute
import com.example.squadup.features.admin.manageaccounts.ManageAccountsRoute
import com.example.squadup.features.admin.manageaccounts.edituser.EditUserRoute
import com.example.squadup.features.profile.changepassword.ChangePasswordRoute
import com.example.squadup.features.profile.tickets.MyTicketsRoute
import com.example.squadup.features.profile.tickets.details.TicketDetailsRoute
import com.example.squadup.features.profile.edit.EditProfileRoute
import com.example.squadup.features.profile.ProfileRoute
import com.example.squadup.features.login.LoginRoute
import com.example.squadup.features.register.RegisterRoute

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
                    navController.navigate(AppRoutes.Login.route)
                }
            )
        }

        composable(AppRoutes.Login.route) {
            LoginRoute(
                onLoginSuccess = {
                    navController.navigate(AppRoutes.Home.route) {
                        popUpTo(AppRoutes.Login.route) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onCreateAccountClick = {
                    navController.navigate(AppRoutes.Register.route)
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable(AppRoutes.Register.route) {
            RegisterRoute(
                onRegisterSuccess = {
                    navController.navigate(AppRoutes.Home.route) {
                        popUpTo(AppRoutes.Register.route) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onLoginClick = {
                    navController.navigate(AppRoutes.Login.route) {
                        popUpTo(AppRoutes.Register.route) { inclusive = true }
                    }
                },
                onBackClick = {
                    navController.popBackStack()
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
                    navController.navigate(AppRoutes.MyEvents.route)
                },
                onJoinEventClick = { eventId ->
                    // TODO: navegar para Detalhe do Evento
                },
                onEventDetailsClick = { eventId ->
                    // TODO: navegar para Detalhe do Evento
                },
                onLoginClick = {
                    navController.navigate(AppRoutes.Login.route)
                },
                onRegisterClick = {
                    navController.navigate(AppRoutes.Register.route)
                },
                appViewModel = appViewModel
            )
        }

        composable(AppRoutes.Events.route) {
            EventsRoute(
                selectedRoute = AppRoutes.Events.route,
                onNavItemClick = { route ->
                    navController.navigate(route) {
                        popUpTo(AppRoutes.Home.route) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                onEventClick = {},
                onViewCalendarClick = {},
                onFilterByMyTeamsClick = {},
                onMapClick = {},
                onCreateEventClick = {
                    navController.navigate(AppRoutes.CreateEvent.route)
                },
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
            ProfileRoute(
                selectedRoute = AppRoutes.Profile.route,
                onNavItemClick = { route ->
                    navController.navigate(route) {
                        popUpTo(AppRoutes.Home.route) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                onTicketsClick = {
                    navController.navigate(AppRoutes.MyTickets.route)
                },
                onMyEventsClick = {
                    navController.navigate(AppRoutes.MyEvents.route)
                },
                onManageAccountsClick = {
                    navController.navigate(AppRoutes.ManageAccounts.route)
                },
                onEditProfileClick = {
                    navController.navigate(AppRoutes.EditProfile.route)
                },
                onChangePasswordClick = {
                    navController.navigate(AppRoutes.ChangePassword.route)
                },
                onLogoutClick = {
                    navController.navigate(AppRoutes.Login.route) {
                        popUpTo(AppRoutes.Profile.route) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                appViewModel = appViewModel
            )
        }

        composable(AppRoutes.EditProfile.route) {
            EditProfileRoute(
                selectedRoute = AppRoutes.Profile.route,
                onNavItemClick = { route ->
                    navController.navigate(route) {
                        popUpTo(AppRoutes.Home.route) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                onSaveChangesClick = {
                    navController.popBackStack()
                },
                onDeleteAccountClick = {},
                onBackClick = {
                    navController.popBackStack()
                },
                appViewModel = appViewModel
            )
        }
        composable(AppRoutes.MyTickets.route) {
            MyTicketsRoute(
                selectedRoute = AppRoutes.Profile.route,
                onNavItemClick = { route ->
                    navController.navigate(route) {
                        popUpTo(AppRoutes.Home.route) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                onBackClick = { navController.popBackStack() },
                onViewDetailsClick = {
                    navController.navigate(AppRoutes.TicketDetails.route)
                },
                onReferClick = {},
                appViewModel = appViewModel
            )
        }

        composable(AppRoutes.TicketDetails.route) {
            TicketDetailsRoute(
                selectedRoute = AppRoutes.Profile.route,
                onNavItemClick = { route ->
                    navController.navigate(route) {
                        popUpTo(AppRoutes.Home.route) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                onBackClick = { navController.popBackStack() },
                onAddToCalendarClick = {},
                onShareTicketClick = {},
                onSupportClick = {},
                appViewModel = appViewModel
            )
        }

        composable(AppRoutes.ManageAccounts.route) {
            ManageAccountsRoute(
                selectedRoute = AppRoutes.Profile.route,
                onUserClick = { userId ->
                    navController.navigate(AppRoutes.EditUser.createRoute(userId))
                },
                onCreateUserClick = {
                    navController.navigate(AppRoutes.CreateUser.route)
                },
                onNavItemClick = { route ->
                    navController.navigate(route) {
                        popUpTo(AppRoutes.Home.route) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                onBackClick = { navController.popBackStack() },
                onPreviousClick = {},
                onNextClick = {},
                appViewModel = appViewModel
            )
        }

        composable(AppRoutes.CreateEvent.route) {
            CreateEventRoute(
                onBackClick = { navController.popBackStack() },
                onEventCreated = { navController.popBackStack() },
                appViewModel = appViewModel
            )
        }

        composable(AppRoutes.MyEvents.route) {
            MyEventsRoute(
                selectedRoute = AppRoutes.Profile.route,
                onNavItemClick = { route ->
                    navController.navigate(route) {
                        popUpTo(AppRoutes.Home.route) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                onBackClick = { navController.popBackStack() },
                onManageEventClick = {},
                onViewResultsClick = {},
                onCreateEventClick = {
                    navController.navigate(AppRoutes.CreateEvent.route)
                },
                appViewModel = appViewModel
            )
        }

        composable(AppRoutes.CreateUser.route) {
            CreateUserRoute(
                selectedRoute = AppRoutes.Profile.route,
                onNavItemClick = { route ->
                    navController.navigate(route) {
                        popUpTo(AppRoutes.Home.route) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                onBackClick = { navController.popBackStack() },
                onCreateClick = { navController.popBackStack() },
                appViewModel = appViewModel
            )
        }

        composable(
            route = AppRoutes.EditUser.route,
            arguments = listOf(navArgument("userId") { type = NavType.StringType })
        ) {
            EditUserRoute(
                selectedRoute = AppRoutes.Profile.route,
                onNavItemClick = { route ->
                    navController.navigate(route) {
                        popUpTo(AppRoutes.Home.route) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                onBackClick = { navController.popBackStack() },
                onSendMessageClick = {},
                onDeleteClick = { navController.popBackStack() },
                appViewModel = appViewModel
            )
        }

        composable(AppRoutes.ChangePassword.route) {
            ChangePasswordRoute(
                selectedRoute = AppRoutes.Profile.route,
                onNavItemClick = { route ->
                    navController.navigate(route) {
                        popUpTo(AppRoutes.Home.route) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                onChangePasswordClick = {
                    navController.popBackStack()
                },
                onBackClick = {
                    navController.popBackStack()
                },
                appViewModel = appViewModel
            )
        }
    }
}
