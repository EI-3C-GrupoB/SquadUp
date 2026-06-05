package com.example.squadup.core.navigation

import android.app.Application
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.squadup.core.app.AppViewModel
import com.example.squadup.features.admin.manageaccounts.ManageAccountsRoute
import com.example.squadup.features.admin.manageaccounts.createuser.CreateUserRoute
import com.example.squadup.features.admin.manageaccounts.edituser.EditUserRoute
import com.example.squadup.features.events.EventsRoute
import com.example.squadup.features.events.calendar.CalendarRoute
import com.example.squadup.features.events.createevent.CreateEventRoute
import com.example.squadup.features.events.livematch.LiveMatchRoute
import com.example.squadup.features.events.manageevent.ManageEventRoute
import com.example.squadup.features.events.moredetails.MoreDetailsRoute
import com.example.squadup.features.home.HomeRoute
import com.example.squadup.features.notifications.NotificationsRoute
import com.example.squadup.features.onboarding.OnboardingPreferences
import com.example.squadup.features.onboarding.OnboardingRoute
import com.example.squadup.features.onboarding.OnboardingViewModel
import com.example.squadup.features.onboarding.OnboardingViewModelFactory
import com.example.squadup.features.profile.ProfileRoute
import com.example.squadup.features.profile.changepassword.ChangePasswordRoute
import com.example.squadup.features.profile.edit.EditProfileRoute
import com.example.squadup.features.profile.myevents.MyEventsRoute
import com.example.squadup.features.profile.tickets.MyTicketsRoute
import com.example.squadup.features.profile.tickets.details.TicketDetailsRoute
import com.example.squadup.features.teams.TeamsRoute
import com.example.squadup.features.teams.createteam.CreateTeamRoute
import com.example.squadup.features.teams.invite.InviteTeamRoute

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

    val openNotifications: () -> Unit = {
        navController.navigate(AppRoutes.Notifications.route) {
            launchSingleTop = true
        }
    }

    val navigateWithBottomBar: (String) -> Unit = { route ->
        navController.navigate(route) {
            popUpTo(AppRoutes.Home.route) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

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
                        popUpTo(AppRoutes.Onboarding.route) {
                            inclusive = true
                        }
                    }
                },
                onLoginClick = {
                    // TODO: navegar para Auth
                }
            )
        }

        composable(AppRoutes.Notifications.route) {
            NotificationsRoute(
                selectedRoute = AppRoutes.Notifications.route,
                onNavItemClick = navigateWithBottomBar,
                appViewModel = appViewModel
            )
        }

        composable(AppRoutes.Home.route) {
            HomeRoute(
                selectedRoute = AppRoutes.Home.route,
                onNavItemClick = navigateWithBottomBar,
                onNotificationsClick = openNotifications,
                onViewMatchDetailsClick = {
                    // TODO: navegar para detalhe do jogo
                },
                onSeeAllEventsClick = {
                    navController.navigate(AppRoutes.MyEvents.route)
                },
                onJoinEventClick = {
                    // TODO: navegar para detalhe do evento
                },
                onEventDetailsClick = {
                    // TODO: navegar para detalhe do evento
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
            EventsRoute(
                selectedRoute = AppRoutes.Events.route,
                onNavItemClick = navigateWithBottomBar,
                onNotificationsClick = openNotifications,
                onEventClick = {
                    // TODO: navegar para detalhe do evento
                },
                onViewCalendarClick = {
                    navController.navigate(AppRoutes.Calendar.route)
                },
                onFilterByMyTeamsClick = {},
                onMapClick = {},
                onCreateEventClick = {
                    navController.navigate(AppRoutes.CreateEvent.route)
                },
                appViewModel = appViewModel
            )
        }

        composable(AppRoutes.Teams.route) {
            TeamsRoute(
                selectedRoute = AppRoutes.Teams.route,
                onNavItemClick = navigateWithBottomBar,
                onCreateTeamClick = {
                    navController.navigate(AppRoutes.CreateTeam.route)
                },
                onInviteMembersClick = {
                    navController.navigate(AppRoutes.InviteTeam.route)
                },
                onNotificationsClick = openNotifications,
                appViewModel = appViewModel
            )
        }

        composable(AppRoutes.CreateTeam.route) {
            CreateTeamRoute(
                onBackClick = {
                    navController.navigate(AppRoutes.Teams.route) {
                        popUpTo(AppRoutes.Teams.route) {
                            inclusive = false
                        }
                        launchSingleTop = true
                    }
                },
                onNotificationsClick = openNotifications,
                onCreateTeamClick = {
                    navController.navigate(AppRoutes.Teams.route) {
                        popUpTo(AppRoutes.Teams.route) {
                            inclusive = false
                        }
                        launchSingleTop = true
                    }
                },
                appViewModel = appViewModel
            )
        }

        composable(AppRoutes.InviteTeam.route) {
            InviteTeamRoute(
                onBackClick = {
                    navController.navigate(AppRoutes.Teams.route) {
                        popUpTo(AppRoutes.Teams.route) {
                            inclusive = false
                        }
                        launchSingleTop = true
                    }
                },
                onNotificationsClick = openNotifications,
                appViewModel = appViewModel
            )
        }

        composable(AppRoutes.Profile.route) {
            ProfileRoute(
                selectedRoute = AppRoutes.Profile.route,
                onNavItemClick = navigateWithBottomBar,
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
                onLogoutClick = {},
                appViewModel = appViewModel
            )
        }

        composable(AppRoutes.EditProfile.route) {
            EditProfileRoute(
                selectedRoute = AppRoutes.Profile.route,
                onNavItemClick = navigateWithBottomBar,
                onSaveChangesClick = {
                    navController.popBackStack()
                },
                onDeleteAccountClick = {},
                onBackClick = {
                    navController.popBackStack()
                },
                onNotificationsClick = openNotifications,
                onLocationClick = {
                    navController.navigate(AppRoutes.SelectLocation.route)
                },
                appViewModel = appViewModel
            )
        }

        composable(AppRoutes.MyTickets.route) {
            MyTicketsRoute(
                selectedRoute = AppRoutes.Profile.route,
                onNavItemClick = navigateWithBottomBar,
                onBackClick = {
                    navController.popBackStack()
                },
                onNotificationsClick = openNotifications,
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
                onNavItemClick = navigateWithBottomBar,
                onBackClick = {
                    navController.popBackStack()
                },
                onNotificationsClick = openNotifications,
                onAddToCalendarClick = {},
                onShareTicketClick = {},
                onSupportClick = {},
                appViewModel = appViewModel
            )
        }

        composable(AppRoutes.ManageAccounts.route) {
            ManageAccountsRoute(
                selectedRoute = AppRoutes.Profile.route,
                onNavItemClick = navigateWithBottomBar,
                onBackClick = {
                    navController.popBackStack()
                },
                onNotificationsClick = openNotifications,
                onUserClick = { userId ->
                    navController.navigate(AppRoutes.EditUser.createRoute(userId))
                },
                onCreateUserClick = {
                    navController.navigate(AppRoutes.CreateUser.route)
                },
                onPreviousClick = {},
                onNextClick = {},
                appViewModel = appViewModel
            )
        }

        composable(AppRoutes.CreateUser.route) {
            CreateUserRoute(
                selectedRoute = AppRoutes.Profile.route,
                onNavItemClick = navigateWithBottomBar,
                onBackClick = {
                    navController.popBackStack()
                },
                onNotificationsClick = openNotifications,
                onCreateClick = {
                    navController.popBackStack()
                },
                appViewModel = appViewModel
            )
        }

        composable(
            route = AppRoutes.EditUser.route,
            arguments = listOf(
                navArgument("userId") {
                    type = NavType.StringType
                }
            )
        ) {
            EditUserRoute(
                selectedRoute = AppRoutes.Profile.route,
                onNavItemClick = navigateWithBottomBar,
                onBackClick = {
                    navController.popBackStack()
                },
                onNotificationsClick = openNotifications,
                onSendMessageClick = {},
                onDeleteClick = {
                    navController.popBackStack()
                },
                appViewModel = appViewModel
            )
        }

        composable(AppRoutes.CreateEvent.route) {
            CreateEventRoute(
                onBackClick = {
                    navController.popBackStack()
                },
                onNotificationsClick = openNotifications,
                onEventCreated = {
                    navController.popBackStack()
                },
                appViewModel = appViewModel
            )
        }

        composable(AppRoutes.MyEvents.route) {
            MyEventsRoute(
                selectedRoute = AppRoutes.Profile.route,
                onNavItemClick = navigateWithBottomBar,
                onBackClick = {
                    navController.popBackStack()
                },
                onNotificationsClick = openNotifications,
                onManageEventClick = { eventId ->
                    navController.navigate(AppRoutes.ManageEvent.createRoute(eventId))
                },
                onViewResultsClick = {},
                onCreateEventClick = {
                    navController.navigate(AppRoutes.CreateEvent.route)
                },
                appViewModel = appViewModel
            )
        }

        composable(AppRoutes.ChangePassword.route) {
            ChangePasswordRoute(
                selectedRoute = AppRoutes.Profile.route,
                onNavItemClick = navigateWithBottomBar,
                onChangePasswordClick = {
                    navController.popBackStack()
                },
                onBackClick = {
                    navController.popBackStack()
                },
                appViewModel = appViewModel
            )
        }

        composable(
            route = AppRoutes.ManageEvent.route,
            arguments = listOf(navArgument("eventId") { type = NavType.StringType })
        ) {
            ManageEventRoute(
                selectedRoute = AppRoutes.Profile.route,
                onNavItemClick = { route ->
                    navController.navigate(route) {
                        popUpTo(AppRoutes.Home.route) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                onBackClick = { navController.popBackStack() },
                onFormTeamsClick = {},
                onEditEventClick = {},
                onManageLiveClick = { gameId ->
                    navController.navigate(AppRoutes.LiveMatch.createRoute(gameId))
                },
                onCreateGameClick = {},
                onEditGameClick = {},
                appViewModel = appViewModel
            )
        }

        composable(
            route = AppRoutes.LiveMatch.route,
            arguments = listOf(navArgument("gameId") { type = NavType.StringType })
        ) { backStackEntry ->
            val gameId = backStackEntry.arguments?.getString("gameId") ?: ""
            LiveMatchRoute(
                gameId = gameId,
                selectedRoute = AppRoutes.Events.route,
                onNavItemClick = { route ->
                    navController.navigate(route) {
                        popUpTo(AppRoutes.Home.route) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(AppRoutes.Calendar.route) {
            CalendarRoute(
                selectedRoute = AppRoutes.Events.route,
                onNavItemClick = navigateWithBottomBar,
                onBackClick = {
                    navController.popBackStack()
                },
                onNotificationsClick = openNotifications,
                onMatchDetailsClick = {
                    // TODO: navegar para detalhes do jogo
                },
                onTravelInfoClick = {
                    // TODO: navegar para informação de viagem
                },
                appViewModel = appViewModel
            )
        }

        composable(AppRoutes.MoreDetails.route) {
            MoreDetailsRoute(
                selectedRoute = AppRoutes.Events.route,
                onNavItemClick = navigateWithBottomBar,
                onBackClick = {
                    navController.popBackStack()
                },
                onNotificationsClick = openNotifications,
                appViewModel = appViewModel
            )
        }
    }
}