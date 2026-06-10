package com.example.squadup.features.admin.home
data class AdminHomeUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val totalUsers: Int = 0,
    val usersGrowthPercent: Float = 0f,
    val activeMatches: Int = 0,
    val activeSportsCount: Int = 0,
    val activeEvents: Int = 0,
    val sportPopularity: List<SportPopularityItem> = emptyList()
) {
    companion object {
        fun fromDashboard(dashboard: AdminHomeDashboard): AdminHomeUiState {
            return AdminHomeUiState(
                isLoading = false,
                errorMessage = null,
                totalUsers = dashboard.totalUsers,
                usersGrowthPercent = dashboard.usersGrowthPercent,
                activeMatches = dashboard.activeMatches,
                activeSportsCount = dashboard.activeSportsCount,
                activeEvents = dashboard.activeEvents,
                sportPopularity = dashboard.sportPopularity
            )
        }
    }
}

