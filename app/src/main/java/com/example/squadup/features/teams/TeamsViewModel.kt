package com.example.squadup.features.teams

import androidx.lifecycle.ViewModel
import com.example.squadup.core.enums.SportType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class TeamsViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(
        TeamsUiState(
            selectedTab = TeamsTab.MY_TEAMS,
            expandedTeamId = null,
            settingsTeamId = null,
            myTeams = listOf(
                TeamListItem(
                    id = "1",
                    name = "Midnight Hoops",
                    membersCount = 8,
                    sportType = SportType.BASKETBALL,
                    location = "Downtown Arena",
                    inviteCode = "CD-982X",
                    roster = listOf(
                        TeamRosterMember("1", "Benjamin Sesko", TeamRosterRole.CAPTAIN),
                        TeamRosterMember("2", "Bruno Fernandes", TeamRosterRole.MEMBER),
                        TeamRosterMember("3", "Casemiro", TeamRosterRole.MEMBER),
                        TeamRosterMember("4", "Kobbie Mainoo", TeamRosterRole.MEMBER),
                        TeamRosterMember("5", "Antony", TeamRosterRole.MEMBER)
                    )
                ),
                TeamListItem(
                    id = "2",
                    name = "The Mavericks",
                    membersCount = 12,
                    sportType = SportType.SOCCER,
                    location = "Main Field",
                    inviteCode = "MV-120A",
                    roster = listOf(
                        TeamRosterMember("1", "Alex Hunter", TeamRosterRole.CAPTAIN),
                        TeamRosterMember("2", "Harry Maguire", TeamRosterRole.MEMBER),
                        TeamRosterMember("3", "Matheus Cunha", TeamRosterRole.MEMBER),
                        TeamRosterMember("4", "Kobbie Mainoo", TeamRosterRole.MEMBER)
                    )
                ),
                TeamListItem(
                    id = "3",
                    name = "CPL",
                    membersCount = 0,
                    sportType = SportType.BASKETBALL,
                    location = "Downtown Arena",
                    inviteCode = "CD-982X",
                    roster = emptyList()
                )
            ),
            discoverTeams = listOf(
                TeamListItem(
                    id = "4",
                    name = "LV Ponds",
                    membersCount = 8,
                    sportType = SportType.BASKETBALL,
                    location = "Downtown Arena",
                    roster = listOf(
                        TeamRosterMember("1", "Lebron Tiago", TeamRosterRole.CAPTAIN),
                        TeamRosterMember("2", "Bruno Fernandes", TeamRosterRole.MEMBER),
                        TeamRosterMember("3", "Casemiro", TeamRosterRole.MEMBER)
                    )
                ),
                TeamListItem(
                    id = "5",
                    name = "Red Eagles",
                    membersCount = 12,
                    sportType = SportType.SOCCER,
                    location = "North Stadium",
                    roster = listOf(
                        TeamRosterMember("1", "Marcus Silva", TeamRosterRole.CAPTAIN),
                        TeamRosterMember("2", "Pedro Costa", TeamRosterRole.MEMBER)
                    )
                ),
                TeamListItem(
                    id = "6",
                    name = "Florida Fire",
                    membersCount = 8,
                    sportType = SportType.BASKETBALL,
                    location = "Fire Court",
                    roster = listOf(
                        TeamRosterMember("1", "John Carter", TeamRosterRole.CAPTAIN),
                        TeamRosterMember("2", "Mike Green", TeamRosterRole.MEMBER)
                    )
                ),
                TeamListItem(
                    id = "7",
                    name = "Blue Dragons",
                    membersCount = 12,
                    sportType = SportType.SOCCER,
                    location = "Blue Arena",
                    roster = listOf(
                        TeamRosterMember("1", "Leo Martins", TeamRosterRole.CAPTAIN),
                        TeamRosterMember("2", "Tom Wilson", TeamRosterRole.MEMBER)
                    )
                )
            )
        )
    )

    val uiState: StateFlow<TeamsUiState> = _uiState.asStateFlow()

    fun onTabSelected(tab: TeamsTab) {
        _uiState.value = _uiState.value.copy(
            selectedTab = tab,
            searchQuery = "",
            expandedTeamId = null,
            settingsTeamId = null
        )
    }

    fun onSearchQueryChange(value: String) {
        _uiState.value = _uiState.value.copy(searchQuery = value)
    }

    fun onTeamToggle(teamId: String) {
        val currentExpandedId = _uiState.value.expandedTeamId
        val isClosing = currentExpandedId == teamId

        _uiState.value = _uiState.value.copy(
            expandedTeamId = if (isClosing) null else teamId,
            settingsTeamId = if (isClosing) null else _uiState.value.settingsTeamId
        )
    }

    fun onTeamSettingsToggle(teamId: String) {
        val currentSettingsTeamId = _uiState.value.settingsTeamId

        _uiState.value = _uiState.value.copy(
            settingsTeamId = if (currentSettingsTeamId == teamId) null else teamId
        )
    }
}