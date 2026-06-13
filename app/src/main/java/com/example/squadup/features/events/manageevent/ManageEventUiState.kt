package com.example.squadup.features.events.manageevent

import com.example.squadup.core.enums.EventFormat
import com.example.squadup.core.enums.EventStatus
import com.example.squadup.core.enums.GameStatus
import com.example.squadup.core.enums.PlayerValidationState
import com.example.squadup.core.enums.SportType
import com.example.squadup.core.enums.TeamEventStatus

enum class ManageEventTab { OVERVIEW, TEAMS, GAMES, STATS, MATCH }

data class ManageEventUiState(
    val isLoading: Boolean = true,
    val eventId: String = "",
    val eventName: String = "",
    val venue: String = "",
    val dateRange: String = "",
    val sportType: SportType = SportType.SOCCER,
    val eventFormat: EventFormat? = null,
    val formatName: String = "",
    val status: EventStatus = EventStatus.DRAFT,
    val isPublic: Boolean = true,
    val accessCode: String? = null,
    val allowTeams: Boolean = false,
    val allowFreeAgents: Boolean = false,
    val registeredTeams: Int = 0,
    val maxTeams: Int = 0,
    val activePlayers: Int = 0,
    val freeAgentsCount: Int = 0,
    val entryFee: String = "",
    val waitlistCount: Int = 0,
    val completedGames: Int = 0,
    val totalGames: Int = 0,
    val matchProgress: Float = 0f,
    val selectedTab: ManageEventTab = ManageEventTab.OVERVIEW,
    val errorMessage: String? = null,
    val activeRegistrationActionId: Int? = null,

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
    val individualRegistrationRequests: List<IndividualRegistrationRequestItem> = emptyList(),
    val teamRegistrationRequests: List<TeamRegistrationRequestItem> = emptyList(),
    val currentGame: CurrentGameBanner? = null,

    // Games
    val gameSearchQuery: String = "",
    val scheduledGames: List<ScheduledGameItem> = emptyList(),

    // Create Game Dialog
    val showCreateGameDialog: Boolean = false,
    val createGameHomeTeamId: String = "",
    val createGameAwayTeamId: String = "",
    val createGameDate: String = "",
    val createGameTime: String = "",
    val createGameVenue: String = "",
    val isCreatingGame: Boolean = false,
    val createGameError: String? = null,

    // Edit Game Dialog
    val showEditGameDialog: Boolean = false,
    val editGameId: String = "",
    val editGameHomeTeamId: String = "",
    val editGameAwayTeamId: String = "",
    val editGameDate: String = "",
    val editGameTime: String = "",
    val editGameVenue: String = "",
    val isEditingGame: Boolean = false,
    val editGameError: String? = null,

    // Stats
    val bestScorer: ScorerItem? = null,
    val gamesPlayed: Int = 0,
    val gamesWeeklyDelta: Int = 0,
    val nextBigMatch: String = "",
    val standings: List<StandingItem> = emptyList(),
    val topScorers: List<ScorerItem> = emptyList(),
    val eventSummaryStats: EventSummaryStats = EventSummaryStats()
) {
    // Single match: only if SINGLE_MATCH format, or format not set and ≤1 game exists
    val isSingleMatch: Boolean get() =
        eventFormat == EventFormat.SINGLE_MATCH ||
        (eventFormat == null && scheduledGames.size <= 1)

    val sportHasDraws: Boolean get() =
        sportType == SportType.SOCCER ||
        sportType == SportType.FUTSAL

    // Format helpers for stats tab
    val showLeagueTable: Boolean get() =
        standings.isNotEmpty() &&
        (eventFormat == EventFormat.LEAGUE ||
         eventFormat == EventFormat.GROUP_KNOCKOUT ||
         (eventFormat == null && standings.size > 1))

    val showDisciplineSection: Boolean get() =
        sportType == SportType.SOCCER || sportType == SportType.FUTSAL ||
        sportType == SportType.BASKETBALL

    val showSetsSection: Boolean get() =
        sportType == SportType.VOLLEYBALL || sportType == SportType.PADDLE

    val standingsLabel: String get() = when (eventFormat) {
        EventFormat.GROUP_KNOCKOUT -> "Fase de Grupos"
        EventFormat.KNOCKOUT       -> "Eliminatórias"
        else                       -> "Classificação"
    }

    // Points label in standings depends on sport
    val standingsPtsLabel: String get() = when (sportType) {
        SportType.PADDLE     -> "V"    // vitórias (no PTS concept)
        SportType.VOLLEYBALL -> "PTS"
        SportType.BASKETBALL -> "PTS"
        else                 -> "PTS"
    }

    // Whether standings use goal columns
    val standingsShowGoals: Boolean get() =
        sportType == SportType.SOCCER || sportType == SportType.FUTSAL

    // Whether standings show sets column
    val standingsShowSets: Boolean get() =
        sportType == SportType.VOLLEYBALL
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

data class IndividualRegistrationRequestItem(
    val registrationId: Int,
    val userId: Int,
    val name: String,
    val initials: String,
    val experienceLevel: Int?,
    val requestedAt: String
)

data class TeamRegistrationRequestItem(
    val registrationId: Int,
    val teamId: Int,
    val captainUserId: Int?,
    val teamName: String,
    val captainName: String,
    val requestedAt: String
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
    val homeTeamId: String = "",
    val awayTeamId: String = "",
    val sport: String,
    val month: String,
    val day: String,
    val time: String,
    val rawDate: String = "",   // ISO date "YYYY-MM-DD" for edit form
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
    val points: Int,
    // Soccer / Futsal
    val goalsFor: Int = 0,
    val goalsAgainst: Int = 0,
    // Volleyball / Paddle
    val setsWon: Int = 0,
    val setsLost: Int = 0,
) {
    val goalDiff: Int get() = goalsFor - goalsAgainst
}

data class ScorerItem(
    val name: String,
    val teamName: String,
    val score: Int  // genérico: golos, pontos, vitórias — depende do desporto
)

data class EventSummaryStats(
    val totalScore: Int = 0,           // golos / pontos / vitórias — depende do desporto
    val totalInfractions: Int = 0,     // faltas / cartões / infrações — genérico
    // Soccer / Futsal discipline
    val yellowCards: Int = 0,
    val redCards: Int = 0,
    val cleanSheets: Int = 0,          // jogos sem sofrer golos
    // Basketball
    val personalFouls: Int = 0,
    val technicalFouls: Int = 0,
    // Volleyball / Paddle sets
    val totalSetsWon: Int = 0,
    // Per-player discipline (top infractors)
    val topInfractors: List<ScorerItem> = emptyList()
)
