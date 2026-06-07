package com.example.squadup.features.teams

import com.example.squadup.core.enums.SportType

enum class TeamsTab {
    MY_TEAMS,
    DISCOVER
}

enum class TeamRosterRole {
    CAPTAIN,
    MEMBER
}

data class TeamRosterMember(
    val id: String,
    val name: String,
    val role: TeamRosterRole
)

data class TeamListItem(
    val id: String,
    val name: String,
    val membersCount: Int,
    val sportType: SportType,
    val inviteCode: String? = null,
    val logoUrl: String? = null,
    val isCaptain: Boolean = false,
    val roster: List<TeamRosterMember> = emptyList()
)

data class TeamsUiState(
    val selectedTab: TeamsTab = TeamsTab.MY_TEAMS,
    val searchQuery: String = "",
    val myTeams: List<TeamListItem> = emptyList(),
    val discoverTeams: List<TeamListItem> = emptyList(),
    val expandedTeamId: String? = null,
    val settingsTeamId: String? = null,
    val pendingJoinRequests: Set<String> = emptySet(),

    val showJoinByCodeDialog: Boolean = false,
    val joinByCodeValue: String = "",
    val joinByCodeError: String? = null,
    val isJoiningByCode: Boolean = false
) {
    val visibleTeams: List<TeamListItem>
        get() {
            val source = when (selectedTab) {
                TeamsTab.MY_TEAMS -> myTeams
                TeamsTab.DISCOVER -> discoverTeams
            }

            if (searchQuery.isBlank()) {
                return source
            }

            return source.filter { team ->
                team.name.contains(searchQuery, ignoreCase = true)
            }
        }
}