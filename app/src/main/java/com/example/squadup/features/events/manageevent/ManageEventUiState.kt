package com.example.squadup.features.events.manageevent

import com.example.squadup.core.enums.EventStatus
import com.example.squadup.core.enums.GameStatus
import com.example.squadup.core.enums.PlayerValidationState
import com.example.squadup.core.enums.SportType
import com.example.squadup.core.enums.TeamEventStatus

enum class ManageEventTab { OVERVIEW, TEAMS, GAMES, STATS, MATCH }

data class ManageEventUiState(
    val eventId: String = "",
    val eventName: String = "",
    val venue: String = "",
    val dateRange: String = "",
    val sportType: SportType = SportType.SOCCER,
    val status: EventStatus = EventStatus.DRAFT,
    val isPublic: Boolean = true,
    val allowTeams: Boolean = false,
    val allowFreeAgents: Boolean = false,
    val registeredTeams: Int = 0,
    val maxTeams: Int = 0,
    val activePlayers: Int = 0,
    val freeAgentsCount: Int = 0,
    val entryFee: String = "",
    val waitlistCount: Int = 0,
    val isSingleMatch: Boolean = false,
    val completedGames: Int = 0,
    val totalGames: Int = 0,
    val matchProgress: Float = 0f,
    val selectedTab: ManageEventTab = ManageEventTab.OVERVIEW,

    // Overview
    val recentRegistrations: List<RecentRegistrationItem> = emptyList(),

    // Teams
    val teamSearchQuery: String = "",
    val freeAgentSearchQuery: String = "",
    val teams: List<ManageTeamItem> = emptyList(),
    val expandedTeamId: String? = null,
    val teamsDisplayCount: Int = 5,
    val freeAgents: List<FreeAgentItem> = emptyList(),
    val freeAgentsDisplayCount: Int = 5,
    val waitlistItems: List<WaitlistItem> = emptyList(),
    val currentGame: CurrentGameBanner? = null,

    // Games
    val gameSearchQuery: String = "",
    val scheduledGames: List<ScheduledGameItem> = emptyList(),

    // Stats
    val bestScorer: ScorerItem? = null,
    val gamesPlayed: Int = 0,
    val gamesWeeklyDelta: Int = 0,
    val nextBigMatch: String = "",
    val standings: List<StandingItem> = emptyList(),
    val topScorers: List<ScorerItem> = emptyList(),
    val eventSummaryStats: EventSummaryStats = EventSummaryStats()
) {
    val sportHasDraws: Boolean get() =
        sportType == com.example.squadup.core.enums.SportType.SOCCER ||
        sportType == com.example.squadup.core.enums.SportType.FUTSAL
}

data class RecentRegistrationItem(
    val id: String,
    val name: String,
    val position: String,
    val timeAgo: String
)

data class ManageTeamItem(
    val id: String,
    val name: String,
    val abbreviation: String,
    val location: String,
    val playerCount: Int,
    val badge: String,
    val eventStatus: TeamEventStatus = TeamEventStatus.PENDING,
    val players: List<ManagePlayerItem> = emptyList()
)

data class ManagePlayerItem(
    val id: String,
    val name: String,
    val initials: String,
    val playerId: String,
    val state: PlayerValidationState
)

data class FreeAgentItem(
    val id: String,
    val name: String,
    val initials: String,
    val position: String,
    val experienceLevel: Int // 1 = Beginner, 2 = Intermediate, 3 = Advanced
)

data class WaitlistItem(
    val id: String,
    val name: String,
    val initials: String,
    val isTeam: Boolean,
    val waitlistPosition: Int
)

data class CurrentGameBanner(
    val label: String,
    val homeTeamAbbr: String,
    val awayTeamAbbr: String,
    val homeTeamName: String,
    val awayTeamName: String,
    val venue: String
)

data class ScheduledGameItem(
    val id: String,
    val homeTeam: String,
    val awayTeam: String,
    val homeTeamAbbr: String = "",
    val awayTeamAbbr: String = "",
    val sport: String,
    val month: String,
    val day: String,
    val time: String,
    val venue: String = "",
    val status: GameStatus = GameStatus.SCHEDULED,
    val homeScore: Int? = null,
    val awayScore: Int? = null,
    val liveTimer: String? = null
)

data class StandingItem(
    val position: Int,
    val teamName: String,
    val teamAbbr: String,
    val played: Int,
    val wins: Int,
    val draws: Int = 0,
    val losses: Int,
    val points: Int
)

data class ScorerItem(
    val name: String,
    val teamName: String,
    val score: Int  // genérico: golos, pontos, vitórias — depende do desporto
)

data class EventSummaryStats(
    val totalScore: Int = 0,      // golos / pontos / sets — depende do desporto
    val totalInfractions: Int = 0 // faltas / cartões / erros — genérico
)
