package com.example.squadup.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.squadup.ui.components.AppLanguage
import com.example.squadup.ui.screens.auth.login.LoginRoute
import com.example.squadup.ui.screens.auth.register.RegisterRoute
import com.example.squadup.ui.screens.events.ExploreEventsScreen
import com.example.squadup.ui.screens.home.HomeRoute
import com.example.squadup.ui.screens.onboarding.OnboardingRoute
import com.example.squadup.ui.screens.team.InviteSquadScreen
import com.example.squadup.ui.screens.team.TeamsHubScreen
import com.example.squadup.ui.components.TeamsHubTab
import com.example.squadup.ui.utils.getCurrentLanguage
import com.example.squadup.ui.utils.setAppLanguage
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var selectedLanguage by rememberSaveable {
        mutableStateOf(getCurrentLanguage(context))
    }
    var selectedTeamsTab by remember {
        mutableStateOf(TeamsHubTab.Discover)
    }
    var selectedTeamId by rememberSaveable {
        mutableStateOf<String?>("lv_ponds")
    }
    var isManagingRoster by rememberSaveable {
        mutableStateOf(false)
    }
    var manualInviteValue by rememberSaveable {
        mutableStateOf("")
    }
    var eventsSearchQuery by rememberSaveable {
        mutableStateOf("")
    }

    fun changeLanguage(language: AppLanguage) {
        if (selectedLanguage == language) return
        
        selectedLanguage = language
        
        // Dá tempo à animação do switch para terminar antes de recriar a Activity
        scope.launch {
            delay(300)
            setAppLanguage(context, language)
        }
    }

    fun navigateToHomeAfterAuth(popUpToRoute: String) {
        navController.navigate(AppRoutes.HOME) {
            popUpTo(popUpToRoute) {
                inclusive = true
            }
            launchSingleTop = true
        }
    }

    NavHost(
        navController = navController,
        startDestination = AppRoutes.ONBOARDING
    ) {
        composable(AppRoutes.ONBOARDING) {
            OnboardingRoute(
                selectedLanguage = selectedLanguage,
                onLanguageChange = ::changeLanguage,
                onFinish = {
                    navController.navigate(AppRoutes.HOME) {
                        popUpTo(AppRoutes.ONBOARDING) {
                            inclusive = true
                        }
                    }
                },
                onLoginClick = {
                    navController.navigate(AppRoutes.LOGIN)
                }
            )
        }

        composable(AppRoutes.LOGIN) {
            LoginRoute(
                selectedLanguage = selectedLanguage,
                onLanguageChange = ::changeLanguage,
                onLoginSuccess = {
                    val previousRoute = navController.previousBackStackEntry?.destination?.route
                    val popUpToRoute = if (previousRoute == AppRoutes.ONBOARDING) {
                        AppRoutes.ONBOARDING
                    } else {
                        AppRoutes.LOGIN
                    }

                    navigateToHomeAfterAuth(popUpToRoute)
                },
                onForgotPasswordClick = {
                    //navController.navigate(AppRoutes.FORGOT_PASSWORD)
                },
                onCreateAccountClick = {
                    navController.navigate(AppRoutes.REGISTER)
                }
            )
        }

        composable(AppRoutes.REGISTER) {
            RegisterRoute(
                onRegisterSuccess = {
                    val previousRoute = navController.previousBackStackEntry?.destination?.route
                    val popUpToRoute = if (previousRoute == AppRoutes.LOGIN) {
                        AppRoutes.LOGIN
                    } else {
                        AppRoutes.REGISTER
                    }

                    navigateToHomeAfterAuth(popUpToRoute)
                },
                onLoginClick = {
                    navController.popBackStack()
                }
            )
        }

        composable(AppRoutes.HOME) {
            HomeRoute(
                selectedRoute = AppRoutes.HOME,
                onNavItemClick = { route ->
                    if (route == AppRoutes.HOME) return@HomeRoute
                    when (route) {
                        AppRoutes.EVENTS -> navController.navigate(AppRoutes.EVENTS)
                        AppRoutes.TEAMS -> navController.navigate(AppRoutes.TEAMS)
                    }
                },
                onLoginClick = {
                    navController.navigate(AppRoutes.LOGIN)
                },
                onRegisterClick = {
                    navController.navigate(AppRoutes.REGISTER)
                },
                onNotificationsClick = {
                    // navController.navigate(AppRoutes.NOTIFICATIONS)
                },
                onSettingsClick = {
                    // navController.navigate(AppRoutes.SETTINGS)
                },
                onViewMatchDetailsClick = {
                    // navController.navigate(AppRoutes.MATCH_DETAILS)
                },
                onSeeAllEventsClick = {
                    navController.navigate(AppRoutes.EVENTS)
                },
                onJoinEventClick = {
                    // Depois ligamos a inscricao em evento.
                }
            )
        }

        composable(AppRoutes.EVENTS) {
            ExploreEventsScreen(
                selectedRoute = AppRoutes.EVENTS,
                searchQuery = eventsSearchQuery,
                onSearchQueryChange = { eventsSearchQuery = it },
                onMapClick = {},
                onFilterClick = {},
                onFeaturedEventClick = {},
                onFilterByTeamsClick = {},
                onEventActionClick = {},
                onNotificationsClick = {
                    // navController.navigate(AppRoutes.NOTIFICATIONS)
                },
                onSettingsClick = {
                    // navController.navigate(AppRoutes.SETTINGS)
                },
                onNavItemClick = { route ->
                    when (route) {
                        AppRoutes.HOME -> navController.navigate(AppRoutes.HOME)
                        AppRoutes.EVENTS -> Unit
                        AppRoutes.TEAMS -> navController.navigate(AppRoutes.TEAMS)
                    }
                }
            )
        }

        composable(AppRoutes.TEAMS) {
            TeamsHubScreen(
                selectedRoute = AppRoutes.TEAMS,
                selectedTab = selectedTeamsTab,
                selectedTeamId = selectedTeamId,
                isManagingRoster = isManagingRoster,
                onTabSelected = { selectedTeamsTab = it },
                onTeamClick = { selectedTeamId = it },
                onCreateTeamClick = {
                    // Create team screen is built separately and can be wired here.
                },
                onNotificationsClick = {
                    // navController.navigate(AppRoutes.NOTIFICATIONS)
                },
                onSettingsClick = {
                    // navController.navigate(AppRoutes.SETTINGS)
                },
                onInviteMembersClick = {
                    navController.navigate(AppRoutes.INVITE_SQUAD)
                },
                onTeamSettingsClick = {
                    isManagingRoster = !isManagingRoster
                },
                onCopyInviteCodeClick = {},
                onPromoteMemberClick = {},
                onRemoveMemberClick = {},
                onNavItemClick = { route ->
                    when (route) {
                        AppRoutes.HOME -> navController.navigate(AppRoutes.HOME)
                        AppRoutes.EVENTS -> navController.navigate(AppRoutes.EVENTS)
                        AppRoutes.TEAMS -> Unit
                    }
                }
            )
        }

        composable(AppRoutes.INVITE_SQUAD) {
            InviteSquadScreen(
                inviteCode = "SQUAD-\nX92",
                manualInviteValue = manualInviteValue,
                onManualInviteChange = { manualInviteValue = it },
                onCopyInviteCodeClick = {},
                onShareOptionClick = {},
                onSyncContactsClick = {},
                onInviteContactClick = {},
                onSendManualInviteClick = {},
                onBackClick = {
                    navController.popBackStack()
                },
                onNotificationsClick = {
                    // navController.navigate(AppRoutes.NOTIFICATIONS)
                },
                onSettingsClick = {
                    // navController.navigate(AppRoutes.SETTINGS)
                }
            )
        }
    }
}
