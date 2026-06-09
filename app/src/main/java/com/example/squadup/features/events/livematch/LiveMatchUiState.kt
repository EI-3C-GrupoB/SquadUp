package com.example.squadup.features.events.livematch

import com.example.squadup.core.enums.EventFormat
import com.example.squadup.core.enums.SportType

enum class LiveMatchPhase { PRE_MATCH, LIVE, FINISHED }
enum class LiveMatchTab { EVENTS, STATS }
enum class MatchEventType { SCORE, INFRACTION, SUBSTITUTION, TIMEOUT }

data class LiveMatchUiState(
    val gameId: String = "",
    val eventId: String = "",
    val isOrganizer: Boolean = true,
    val phase: LiveMatchPhase = LiveMatchPhase.PRE_MATCH,
    val sportType: SportType = SportType.SOCCER,
    val eventFormat: EventFormat? = null,

    // Teams
    val homeTeamId: Int? = null,
    val awayTeamId: Int? = null,
    val homeTeamName: String = "",
    val awayTeamName: String = "",
    val homeTeamAbbr: String = "",
    val awayTeamAbbr: String = "",
    val venue: String = "",
    val scheduledDate: String = "",
    val scheduledTime: String = "",

    // Players
    val homePlayers: List<LiveMatchPlayer> = emptyList(),
    val awayPlayers: List<LiveMatchPlayer> = emptyList(),

    // Live
    val homeScore: Int = 0,
    val awayScore: Int = 0,
    val timerSeconds: Int = 0,
    val isTimerRunning: Boolean = false,

    // Events
    val events: List<MatchEventItem> = emptyList(),
    val selectedTab: LiveMatchTab = LiveMatchTab.EVENTS,

    // Forms
    val showGoalForm: Boolean = false,
    val showInfractionForm: Boolean = false,
    val showSubstitutionForm: Boolean = false,
    val showAdvancedStatsForm: Boolean = false,

    // Stats
    val homeStats: LiveTeamStats = LiveTeamStats(),
    val awayStats: LiveTeamStats = LiveTeamStats()
) {
    val timerDisplay: String get() {
        val m = timerSeconds / 60
        val s = timerSeconds % 60
        return "${m.toString().padStart(2, '0')}:${s.toString().padStart(2, '0')}"
    }

    val isKnockout: Boolean get() =
        eventFormat == EventFormat.KNOCKOUT || eventFormat == EventFormat.GROUP_KNOCKOUT

    // Score label depends on sport
    val scoreUnitLabel: String get() = when (sportType) {
        SportType.SOCCER, SportType.FUTSAL -> "GOLOS"
        SportType.BASKETBALL -> "PONTOS"
        SportType.VOLLEYBALL, SportType.PADDLE -> "SETS"
    }
}

data class LiveMatchPlayer(
    val id: String,
    val name: String,
    val initials: String,
    val isHome: Boolean
)

data class MatchEventItem(
    val id: String,
    val minute: Int,
    val type: MatchEventType,
    val playerName: String,
    val teamAbbr: String,
    val description: String,
    val isHome: Boolean,
    val scoreDelta: Int = 1, // pts added per event (basketball: 1/2/3)
    val synced: Boolean = false
)

data class LiveTeamStats(
    // Soccer / Futsal
    val shots: Int = 0,
    val shotsOnGoal: Int = 0,
    val corners: Int = 0,
    val fouls: Int = 0,
    val yellowCards: Int = 0,
    val redCards: Int = 0,
    val offsides: Int = 0,
    val saves: Int = 0,
    // Basketball
    val points1: Int = 0,   // free throws
    val points2: Int = 0,   // 2-pointers
    val points3: Int = 0,   // 3-pointers
    val personalFouls: Int = 0,
    val technicalFouls: Int = 0,
    val assists: Int = 0,
    val rebounds: Int = 0,
    // Volleyball / Paddle
    val aces: Int = 0,
    val blocks: Int = 0,
    val errors: Int = 0,
    val setsWon: Int = 0
)
