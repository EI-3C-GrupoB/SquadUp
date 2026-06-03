package com.example.squadup.features.events.livematch

import com.example.squadup.core.enums.SportType

enum class LiveMatchPhase { PRE_MATCH, LIVE, FINISHED }
enum class LiveMatchTab { EVENTS, STATS }
enum class MatchEventType { SCORE, INFRACTION, SUBSTITUTION, TIMEOUT }

data class LiveMatchUiState(
    val gameId: String = "",
    val isOrganizer: Boolean = true,  // false = jogador/espectador (read-only)
    val phase: LiveMatchPhase = LiveMatchPhase.PRE_MATCH,
    val sportType: SportType = SportType.SOCCER,

    // Teams
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

    // Events (offline-first — synced flag para Room mais tarde)
    val events: List<MatchEventItem> = emptyList(),
    val selectedTab: LiveMatchTab = LiveMatchTab.EVENTS,

    // Forms
    val showGoalForm: Boolean = false,
    val showInfractionForm: Boolean = false,
    val showSubstitutionForm: Boolean = false,

    // Stats — hardcoded por agora, derivadas dos eventos quando ligarmos à BD
    val homeStats: LiveTeamStats = LiveTeamStats(),
    val awayStats: LiveTeamStats = LiveTeamStats()
) {
    val timerDisplay: String
        get() {
            val m = timerSeconds / 60
            val s = timerSeconds % 60
            return "${m.toString().padStart(2, '0')}:${s.toString().padStart(2, '0')}"
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
    val synced: Boolean = false  // offline-first
)

// Stats — hardcoded por agora, derivadas dos eventos quando ligarmos à BD
data class LiveTeamStats(
    val shots: Int = 0,
    val shotsOnGoal: Int = 0,
    val corners: Int = 0,
    val fouls: Int = 0,
    val yellowCards: Int = 0,
    val redCards: Int = 0,
    val offsides: Int = 0,
    val saves: Int = 0
)
