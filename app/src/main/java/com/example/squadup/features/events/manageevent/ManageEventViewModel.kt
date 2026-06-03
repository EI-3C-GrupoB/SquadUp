package com.example.squadup.features.events.manageevent

import androidx.lifecycle.ViewModel
import com.example.squadup.core.enums.EventStatus
import com.example.squadup.core.enums.GameStatus
import com.example.squadup.core.enums.PlayerValidationState
import com.example.squadup.core.enums.TeamEventStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ManageEventViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(
        ManageEventUiState(
            isSingleMatch = true, // mudar para false para testar torneio
            isPublic = true,
            allowTeams = true,
            allowFreeAgents = true,
            freeAgentsCount = 8,
            entryFee = "€15",
            waitlistCount = 3,
            completedGames = 18,
            totalGames = 50,
            recentRegistrations = listOf(
                RecentRegistrationItem("1", "Marcus Thorne", "Power Forward", "2m ago"),
                RecentRegistrationItem("2", "Elena Rodriguez", "Point Guard", "15m ago"),
                RecentRegistrationItem("3", "David Chen", "Center", "1h ago"),
            ),
            teams = listOf(
                ManageTeamItem(
                    id = "slb", name = "Benfica Stars", abbreviation = "SLB",
                    location = "Lisboa, PT", playerCount = 12, badge = "Federados",
                    eventStatus = TeamEventStatus.CONFIRMED,
                    players = listOf(
                        ManagePlayerItem("p1", "João Silva", "JS", "#44291", PlayerValidationState.VALIDATED),
                        ManagePlayerItem("p2", "Ricardo Lopes", "RL", "#44295", PlayerValidationState.VALIDATED),
                        ManagePlayerItem("p3", "Miguel Costa", "MC", "#44310", PlayerValidationState.PENDING),
                        ManagePlayerItem("p4", "André Martins", "AM", "#44318", PlayerValidationState.VALIDATED),
                    )
                ),
                ManageTeamItem(
                    id = "fcp", name = "FC Porto Elite", abbreviation = "FCP",
                    location = "Porto, PT", playerCount = 11, badge = "Federados",
                    eventStatus = TeamEventStatus.CONFIRMED,
                    players = listOf(
                        ManagePlayerItem("p5", "Ricardo Silva", "RS", "#44220", PlayerValidationState.VALIDATED),
                        ManagePlayerItem("p6", "Bruno Fernandes", "BF", "#44225", PlayerValidationState.VALIDATED),
                        ManagePlayerItem("p7", "Diogo Jota", "DJ", "#44230", PlayerValidationState.PENDING),
                    )
                ),
                ManageTeamItem(
                    id = "scp", name = "Sporting Lions", abbreviation = "SCP",
                    location = "Lisboa, PT", playerCount = 14, badge = "Federados",
                    eventStatus = TeamEventStatus.INCOMPLETE,
                    players = listOf(
                        ManagePlayerItem("p8", "Tiago Costa", "TC", "#44180", PlayerValidationState.VALIDATED),
                        ManagePlayerItem("p9", "Pedro Alves", "PA", "#44185", PlayerValidationState.PENDING),
                    )
                ),
                ManageTeamItem(
                    id = "bcw", name = "Braga Warriors", abbreviation = "BCW",
                    location = "Braga, PT", playerCount = 12, badge = "Federados",
                    eventStatus = TeamEventStatus.PENDING,
                    players = listOf(
                        ManagePlayerItem("p10", "Carlos Matos", "CM", "#44150", PlayerValidationState.VALIDATED),
                        ManagePlayerItem("p11", "Nuno Oliveira", "NO", "#44155", PlayerValidationState.VALIDATED),
                    )
                ),
                ManageTeamItem(
                    id = "vtc", name = "Vitória Titans", abbreviation = "VTC",
                    location = "Guimarães, PT", playerCount = 10, badge = "Amateur",
                    eventStatus = TeamEventStatus.WITHDRAWN,
                    players = listOf(
                        ManagePlayerItem("p12", "Sérgio Mota", "SM", "#44100", PlayerValidationState.PENDING),
                        ManagePlayerItem("p13", "Rafael Santos", "RS", "#44105", PlayerValidationState.VALIDATED),
                    )
                ),
            ),
            freeAgents = listOf(
                FreeAgentItem("fa1", "Alex Rivera", "AR", "Forward", 3),
                FreeAgentItem("fa2", "Sam Jenkins", "SJ", "Midfielder", 2),
                FreeAgentItem("fa3", "Marcus Webb", "MW", "Goalkeeper", 1),
                FreeAgentItem("fa4", "Lena Torres", "LT", "Defender", 3),
                FreeAgentItem("fa5", "Chris Park", "CP", "Forward", 2),
                FreeAgentItem("fa6", "Jordan Mills", "JM", "Midfielder", 1),
                FreeAgentItem("fa7", "Sofia Reyes", "SR", "Defender", 2),
                FreeAgentItem("fa8", "Noah Blake", "NB", "Forward", 3),
            ),
            waitlistItems = listOf(
                WaitlistItem("w1", "Thunder FC", "TFC", isTeam = true, waitlistPosition = 1),
                WaitlistItem("w2", "David Sousa", "DS", isTeam = false, waitlistPosition = 2),
                WaitlistItem("w3", "Eagles United", "EU", isTeam = true, waitlistPosition = 3),
            ),
            currentGame = CurrentGameBanner(
                label = "CURRENT GAME",
                homeTeamAbbr = "SLB", awayTeamAbbr = "FCP",
                homeTeamName = "BENFICA STARS", awayTeamName = "FC PORTO ELITE",
                venue = "Arena Central"
            ),
            scheduledGames = listOf(
                ScheduledGameItem(
                    id = "g0", homeTeam = "Benfica Stars", awayTeam = "FC Porto Elite",
                    homeTeamAbbr = "SLB", awayTeamAbbr = "FCP",
                    sport = "Football", month = "APR", day = "9", time = "18:00",
                    venue = "Arena Central",
                    status = GameStatus.LIVE, homeScore = 2, awayScore = 1, liveTimer = "38:45"
                ),
                ScheduledGameItem(
                    id = "g1", homeTeam = "Braga Warriors", awayTeam = "Sporting Lions",
                    homeTeamAbbr = "BCW", awayTeamAbbr = "SCP",
                    sport = "Football", month = "APR", day = "12", time = "15:00",
                    venue = "Estádio Municipal", status = GameStatus.WARM_UP
                ),
                ScheduledGameItem(
                    id = "g2", homeTeam = "Benfica Stars", awayTeam = "Vitória Titans",
                    homeTeamAbbr = "SLB", awayTeamAbbr = "VTC",
                    sport = "Football", month = "APR", day = "15", time = "17:00",
                    venue = "Arena Central", status = GameStatus.SCHEDULED
                ),
                ScheduledGameItem(
                    id = "g3", homeTeam = "Braga Warriors", awayTeam = "Vitória Titans",
                    homeTeamAbbr = "BCW", awayTeamAbbr = "VTC",
                    sport = "Football", month = "APR", day = "20", time = "20:00",
                    venue = "Estádio Norte", status = GameStatus.SCHEDULED
                ),
                ScheduledGameItem(
                    id = "g4", homeTeam = "FC Porto Elite", awayTeam = "Sporting Lions",
                    homeTeamAbbr = "FCP", awayTeamAbbr = "SCP",
                    sport = "Football", month = "APR", day = "29", time = "20:00",
                    venue = "Arena Central", status = GameStatus.SCHEDULED
                ),
                ScheduledGameItem(
                    id = "g5", homeTeam = "FC Porto Elite", awayTeam = "Vitória Titans",
                    homeTeamAbbr = "FCP", awayTeamAbbr = "VTC",
                    sport = "Football", month = "APR", day = "5", time = "18:00",
                    venue = "Estádio Municipal",
                    status = GameStatus.FINISHED, homeScore = 3, awayScore = 0
                ),
                ScheduledGameItem(
                    id = "g6", homeTeam = "Benfica Stars", awayTeam = "Braga Warriors",
                    homeTeamAbbr = "SLB", awayTeamAbbr = "BCW",
                    sport = "Football", month = "APR", day = "3", time = "16:00",
                    venue = "Arena Central",
                    status = GameStatus.FINISHED, homeScore = 1, awayScore = 1
                ),
            ),
            bestScorer = ScorerItem("Ricardo Silva", "FC Porto Elite", 12),
            gamesPlayed = 50,
            gamesWeeklyDelta = 12,
            nextBigMatch = "24 Oct, 20:30",
            standings = listOf(
                StandingItem(1, "Benfica Stars",  "SLB", played=10, wins=9, draws=0, losses=1, points=27),
                StandingItem(2, "FC Porto Elite", "FCP", played=10, wins=7, draws=2, losses=1, points=23),
                StandingItem(3, "Sporting Lions", "SCP", played=10, wins=6, draws=2, losses=2, points=20),
                StandingItem(4, "Braga Warriors", "BCW", played=10, wins=4, draws=1, losses=5, points=13),
                StandingItem(5, "Vitória Titans", "VTC", played=10, wins=3, draws=1, losses=6, points=10),
            ),
            topScorers = listOf(
                ScorerItem("Ricardo Silva",  "FC Porto Elite", 12),
                ScorerItem("André Martins",  "Benfica Stars",  10),
                ScorerItem("Tiago Costa",    "Sporting Lions",  9),
                ScorerItem("Carlos Matos",   "Braga Warriors",  7),
            ),
            eventSummaryStats = EventSummaryStats(
                totalScore = 38,
                totalInfractions = 16
            )
        )
    )
    val uiState: StateFlow<ManageEventUiState> = _uiState.asStateFlow()

    fun onTabChange(tab: ManageEventTab) {
        _uiState.update { it.copy(selectedTab = tab) }
    }

    fun onTeamSearchQueryChange(query: String) {
        _uiState.update { it.copy(teamSearchQuery = query, teamsDisplayCount = PAGE_SIZE) }
    }

    fun onFreeAgentSearchQueryChange(query: String) {
        _uiState.update { it.copy(freeAgentSearchQuery = query, freeAgentsDisplayCount = PAGE_SIZE) }
    }

    fun onLoadMoreTeams() {
        _uiState.update { it.copy(teamsDisplayCount = it.teamsDisplayCount + PAGE_SIZE) }
    }

    fun onLoadMoreFreeAgents() {
        _uiState.update { it.copy(freeAgentsDisplayCount = it.freeAgentsDisplayCount + PAGE_SIZE) }
    }

    fun onGameSearchQueryChange(query: String) {
        _uiState.update { it.copy(gameSearchQuery = query) }
    }

    companion object {
        private const val PAGE_SIZE = 5
    }

    fun onTeamExpand(teamId: String) {
        _uiState.update { state ->
            state.copy(expandedTeamId = if (state.expandedTeamId == teamId) null else teamId)
        }
    }

    fun onPlayerRemove(teamId: String, playerId: String) {
        // TODO: remover jogador via backend
    }

    fun onStatusAction() {
        val next = when (_uiState.value.status) {
            EventStatus.DRAFT               -> EventStatus.REGISTRATION_OPEN
            EventStatus.REGISTRATION_OPEN   -> EventStatus.REGISTRATION_CLOSED
            EventStatus.REGISTRATION_CLOSED -> EventStatus.ONGOING
            EventStatus.ONGOING             -> EventStatus.FINISHED
            else -> return
        }
        _uiState.update { it.copy(status = next) }
    }

    fun onCancelEvent() {
        _uiState.update { it.copy(status = EventStatus.CANCELLED) }
    }
}
