package com.example.squadup.core.navigation

sealed class AppRoutes(val route: String) {
    data object Onboarding : AppRoutes("onboarding")
    data object Home       : AppRoutes("home")
    data object Events     : AppRoutes("events")
    data object Teams      : AppRoutes("teams")
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

    data object SelectLocation : AppRoutes("select_location")
    data object MoreDetails : AppRoutes("more_details")
    data object Calendar : AppRoutes("calendar")
    data object MyTickets        : AppRoutes("my_tickets")
    data object TicketDetails    : AppRoutes("ticket_details/{ticketId}") {
        fun createRoute(ticketId: String) = "ticket_details/$ticketId"
    }
}
