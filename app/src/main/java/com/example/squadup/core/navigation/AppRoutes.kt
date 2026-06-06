package com.example.squadup.core.navigation

sealed class AppRoutes(val route: String) {
    data object Onboarding : AppRoutes("onboarding")
    data object Login      : AppRoutes("login")
    data object Register   : AppRoutes("register")
    data object Notifications : AppRoutes("notifications")
    data object Home       : AppRoutes("home")
    data object Events     : AppRoutes("events")
    data object EventsMap : AppRoutes("events_map")
    data object Teams      : AppRoutes("teams")

    data object CreateTeam : AppRoutes("create_team")

    data object InviteTeam : AppRoutes("invite_team")
    data object Profile      : AppRoutes("profile")
    data object EditProfile      : AppRoutes("edit_profile")
    data object ChangePassword   : AppRoutes("change_password")
    data object ManageAccounts   : AppRoutes("manage_accounts")
    data object MyEvents         : AppRoutes("my_events_organizer")
    data object CreateEvent      : AppRoutes("create_event")
    data object CreateUser       : AppRoutes("create_user")
    data object EditUser         : AppRoutes("edit_user/{userId}") {
        fun createRoute(userId: String) = "edit_user/$userId"
    }

    data object ManageEvent : AppRoutes("manage_event/{eventId}") {
        fun createRoute(eventId: String) = "manage_event/$eventId"
    }
    data object LiveMatch : AppRoutes("live_match/{gameId}") {
        fun createRoute(gameId: String) = "live_match/$gameId"
    }
    data object SelectLocation : AppRoutes("select_location")
    data object MoreDetails : AppRoutes("more_details/{eventId}") {
        fun createRoute(eventId: String) = "more_details/$eventId"
    }
    data object Calendar : AppRoutes("calendar")
    data object MyTickets        : AppRoutes("my_tickets")
    data object TicketDetails    : AppRoutes("ticket_details/{ticketId}") {
        fun createRoute(ticketId: String) = "ticket_details/$ticketId"
    }
}
