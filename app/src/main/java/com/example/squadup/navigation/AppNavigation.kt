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
import com.example.squadup.ui.components.defaultExploreEvents
import com.example.squadup.ui.screens.events.EventCalendarScreen
import com.example.squadup.ui.screens.events.EventDetailsScreen
import com.example.squadup.ui.screens.events.ExploreEventsScreen
import com.example.squadup.ui.screens.events.MatchRegistrationSuccessScreen
import com.example.squadup.ui.screens.events.PaymentMethod
import com.example.squadup.ui.screens.events.RegistrationPaymentScreen
import com.example.squadup.ui.screens.events.TournamentDetailsScreen
import com.example.squadup.ui.screens.home.HomeRoute
import com.example.squadup.ui.screens.onboarding.OnboardingRoute
import com.example.squadup.ui.screens.profile.ChangePasswordScreen
import com.example.squadup.ui.screens.profile.EditProfileScreen
import com.example.squadup.ui.screens.profile.ProfileScreen
import com.example.squadup.ui.components.TeamDetailsTab
import com.example.squadup.ui.components.TicketTab
import com.example.squadup.ui.screens.tickets.MyTicketsScreen
import com.example.squadup.ui.screens.tickets.TicketDetailsScreen
import com.example.squadup.ui.screens.team.CreateTeamScreen
import com.example.squadup.ui.screens.team.InviteSquadScreen
import com.example.squadup.ui.screens.team.TeamDetailsScreen
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
    var selectedEventTitle by rememberSaveable {
        mutableStateOf(defaultExploreEvents().first().title)
    }
    var selectedPaymentMethod by rememberSaveable {
        mutableStateOf(PaymentMethod.CreditCard)
    }
    var profileUsername by rememberSaveable {
        mutableStateOf("Alex Hunter")
    }
    var profilePlayStyle by rememberSaveable {
        mutableStateOf("High Energy")
    }
    var profilePreferredSport by rememberSaveable {
        mutableStateOf("Football")
    }
    var currentPassword by rememberSaveable {
        mutableStateOf("")
    }
    var newPassword by rememberSaveable {
        mutableStateOf("")
    }
    var confirmNewPassword by rememberSaveable {
        mutableStateOf("")
    }
    var createTeamName by rememberSaveable {
        mutableStateOf("")
    }
    var createTeamDescription by rememberSaveable {
        mutableStateOf("")
    }
    var createTeamSports by rememberSaveable {
        mutableStateOf(listOf<String>())
    }
    var isCreateTeamPrivate by rememberSaveable {
        mutableStateOf(false)
    }
    var selectedTeamDetailsTab by remember {
        mutableStateOf(TeamDetailsTab.Members)
    }
    var selectedTicketTab by remember {
        mutableStateOf(TicketTab.Upcoming)
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

    fun navigateBottom(route: String) {
        when (route) {
            AppRoutes.HOME,
            AppRoutes.EVENTS,
            AppRoutes.TEAMS,
            AppRoutes.PROFILE -> {
                navController.navigate(route) {
                    launchSingleTop = true
                    restoreState = true
                    popUpTo(AppRoutes.HOME) {
                        saveState = true
                    }
                }
            }
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
                    navigateBottom(route)
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
                onFeaturedEventClick = {
                    selectedEventTitle = defaultExploreEvents().first().title
                    navController.navigate(AppRoutes.EVENT_DETAILS)
                },
                onViewCalendarClick = {
                    navController.navigate(AppRoutes.EVENT_CALENDAR)
                },
                onFilterByTeamsClick = {},
                onEventActionClick = { event ->
                    selectedEventTitle = event.title

                    if (event.title.contains("Sand Pro Tour")) {
                        navController.navigate(AppRoutes.TOURNAMENT_DETAILS)
                    } else {
                        navController.navigate(AppRoutes.EVENT_DETAILS)
                    }
                },
                onNotificationsClick = {
                    // navController.navigate(AppRoutes.NOTIFICATIONS)
                },
                onSettingsClick = {
                    // navController.navigate(AppRoutes.SETTINGS)
                },
                onNavItemClick = { route ->
                    if (route != AppRoutes.EVENTS) navigateBottom(route)
                }
            )
        }

        composable(AppRoutes.EVENT_DETAILS) {
            val selectedEvent = defaultExploreEvents().firstOrNull { event ->
                event.title == selectedEventTitle
            } ?: defaultExploreEvents().first()

            EventDetailsScreen(
                event = selectedEvent,
                date = "Oct 24,\n2026",
                time = "6:00 PM -\n9:00 PM",
                rules = listOf(
                    "Standard rules apply for the selected sport.",
                    "Game duration is confirmed by the event organizer.",
                    "Referees are provided for official matches.",
                    "Fair play is mandatory for all participants."
                ),
                onJoinClick = {
                    navController.navigate(AppRoutes.REGISTRATION_PAYMENT)
                },
                onNotificationsClick = {
                    // navController.navigate(AppRoutes.NOTIFICATIONS)
                },
                onSettingsClick = {
                    // navController.navigate(AppRoutes.SETTINGS)
                }
            )
        }

        composable(AppRoutes.EVENT_CALENDAR) {
            EventCalendarScreen(
                selectedRoute = AppRoutes.EVENTS,
                onBackClick = {
                    navController.popBackStack()
                },
                onPreviousMonthClick = {},
                onNextMonthClick = {},
                onTodayClick = {},
                onMatchDetailsClick = {
                    navController.navigate(AppRoutes.EVENT_DETAILS)
                },
                onTravelInfoClick = {},
                onNotificationsClick = {
                    // navController.navigate(AppRoutes.NOTIFICATIONS)
                },
                onSettingsClick = {
                    // navController.navigate(AppRoutes.SETTINGS)
                },
                onNavItemClick = { route ->
                    if (route != AppRoutes.EVENTS) navigateBottom(route)
                }
            )
        }

        composable(AppRoutes.TOURNAMENT_DETAILS) {
            TournamentDetailsScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onJoinIndividuallyClick = {
                    navController.navigate(AppRoutes.REGISTRATION_PAYMENT)
                }
            )
        }

        composable(AppRoutes.REGISTRATION_PAYMENT) {
            RegistrationPaymentScreen(
                selectedPaymentMethod = selectedPaymentMethod,
                onPaymentMethodSelected = { selectedPaymentMethod = it },
                onBackClick = {
                    navController.popBackStack()
                },
                onChangePaymentClick = {},
                onPayClick = {
                    navController.navigate(AppRoutes.MATCH_REGISTRATION_SUCCESS)
                }
            )
        }

        composable(AppRoutes.MATCH_REGISTRATION_SUCCESS) {
            MatchRegistrationSuccessScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onNotificationsClick = {
                    // navController.navigate(AppRoutes.NOTIFICATIONS)
                },
                onSettingsClick = {
                    // navController.navigate(AppRoutes.SETTINGS)
                },
                onAddToCalendarClick = {
                    navController.navigate(AppRoutes.EVENT_CALENDAR)
                },
                onInviteFriendsClick = {
                    navController.navigate(AppRoutes.INVITE_SQUAD)
                },
                onViewTicketsClick = {
                    navController.navigate(AppRoutes.MY_TICKETS)
                },
                onBackToHomeClick = {
                    navigateBottom(AppRoutes.HOME)
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
                    navController.navigate(AppRoutes.CREATE_TEAM)
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
                    navController.navigate(AppRoutes.TEAM_DETAILS)
                },
                onCopyInviteCodeClick = {},
                onPromoteMemberClick = {},
                onRemoveMemberClick = {},
                onNavItemClick = { route ->
                    if (route != AppRoutes.TEAMS) navigateBottom(route)
                }
            )
        }

        composable(AppRoutes.CREATE_TEAM) {
            CreateTeamScreen(
                teamName = createTeamName,
                description = createTeamDescription,
                selectedSports = createTeamSports,
                isPrivateTeam = isCreateTeamPrivate,
                onTeamNameChange = { createTeamName = it },
                onDescriptionChange = { createTeamDescription = it },
                onSportToggle = { sport ->
                    createTeamSports = if (createTeamSports.contains(sport)) {
                        createTeamSports - sport
                    } else {
                        createTeamSports + sport
                    }
                },
                onPrivateTeamChange = { isCreateTeamPrivate = it },
                onUploadLogoClick = {},
                onCreateSquadClick = {
                    selectedTeamsTab = TeamsHubTab.MyTeams
                    selectedTeamId = "lv_ponds"
                    navController.popBackStack()
                },
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

        composable(AppRoutes.TEAM_DETAILS) {
            TeamDetailsScreen(
                selectedTab = selectedTeamDetailsTab,
                onTabSelected = { selectedTeamDetailsTab = it },
                onNotificationsClick = {
                    // navController.navigate(AppRoutes.NOTIFICATIONS)
                },
                onSettingsClick = {
                    // navController.navigate(AppRoutes.SETTINGS)
                },
                onGoBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable(AppRoutes.PROFILE) {
            ProfileScreen(
                selectedRoute = AppRoutes.PROFILE,
                onNavItemClick = { route ->
                    if (route != AppRoutes.PROFILE) navigateBottom(route)
                },
                onNotificationsClick = {
                    // navController.navigate(AppRoutes.NOTIFICATIONS)
                },
                onSettingsClick = {
                    // navController.navigate(AppRoutes.SETTINGS)
                },
                onEditProfileClick = {
                    navController.navigate(AppRoutes.EDIT_PROFILE)
                },
                onChangePasswordClick = {
                    navController.navigate(AppRoutes.CHANGE_PASSWORD)
                },
                onLogoutClick = {
                    navController.navigate(AppRoutes.LOGIN) {
                        popUpTo(AppRoutes.HOME) {
                            inclusive = true
                        }
                    }
                }
            )
        }

        composable(AppRoutes.EDIT_PROFILE) {
            EditProfileScreen(
                username = profileUsername,
                playStyle = profilePlayStyle,
                preferredSport = profilePreferredSport,
                selectedRoute = AppRoutes.PROFILE,
                onUsernameChange = { profileUsername = it },
                onPlayStyleChange = { profilePlayStyle = it },
                onPreferredSportChange = { profilePreferredSport = it },
                onSaveChangesClick = {
                    navController.popBackStack()
                },
                onDeleteAccountClick = {},
                onBackToProfileClick = {
                    navController.popBackStack()
                },
                onNotificationsClick = {
                    // navController.navigate(AppRoutes.NOTIFICATIONS)
                },
                onSettingsClick = {
                    // navController.navigate(AppRoutes.SETTINGS)
                },
                onNavItemClick = ::navigateBottom
            )
        }

        composable(AppRoutes.CHANGE_PASSWORD) {
            ChangePasswordScreen(
                currentPassword = currentPassword,
                newPassword = newPassword,
                confirmNewPassword = confirmNewPassword,
                selectedRoute = AppRoutes.PROFILE,
                onCurrentPasswordChange = { currentPassword = it },
                onNewPasswordChange = { newPassword = it },
                onConfirmNewPasswordChange = { confirmNewPassword = it },
                onChangePasswordClick = {
                    currentPassword = ""
                    newPassword = ""
                    confirmNewPassword = ""
                    navController.popBackStack()
                },
                onBackToProfileClick = {
                    navController.popBackStack()
                },
                onNotificationsClick = {
                    // navController.navigate(AppRoutes.NOTIFICATIONS)
                },
                onSettingsClick = {
                    // navController.navigate(AppRoutes.SETTINGS)
                },
                onNavItemClick = ::navigateBottom
            )
        }

        composable(AppRoutes.MY_TICKETS) {
            MyTicketsScreen(
                selectedRoute = AppRoutes.PROFILE,
                selectedTab = selectedTicketTab,
                onTabSelected = { selectedTicketTab = it },
                onBackClick = {
                    navController.popBackStack()
                },
                onNotificationsClick = {
                    // navController.navigate(AppRoutes.NOTIFICATIONS)
                },
                onSettingsClick = {
                    // navController.navigate(AppRoutes.SETTINGS)
                },
                onViewDetailsClick = {
                    navController.navigate(AppRoutes.TICKET_DETAILS)
                },
                onReferClick = {
                    navController.navigate(AppRoutes.INVITE_SQUAD)
                },
                onNavItemClick = ::navigateBottom
            )
        }

        composable(AppRoutes.TICKET_DETAILS) {
            TicketDetailsScreen(
                selectedRoute = AppRoutes.PROFILE,
                onBackClick = {
                    navController.popBackStack()
                },
                onAddToCalendarClick = {
                    navController.navigate(AppRoutes.EVENT_CALENDAR)
                },
                onShareTicketClick = {},
                onSupportClick = {},
                onNavItemClick = ::navigateBottom
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
