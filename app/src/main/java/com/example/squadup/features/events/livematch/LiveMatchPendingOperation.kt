package com.example.squadup.features.events.livematch

import kotlinx.serialization.Serializable

@Serializable
sealed class PendingOperation {

    @Serializable
    data class InsertEvent(
        val eventId: String,
        val type: MatchEventType,
        val isHome: Boolean,
        val playerName: String,
        val description: String,
        val minute: Int
    ) : PendingOperation()

    @Serializable
    data class UpdateScore(
        val homeScore: Int,
        val awayScore: Int
    ) : PendingOperation()

    @Serializable
    data class UpdateStatus(
        val phase: LiveMatchPhase
    ) : PendingOperation()

    @Serializable
    data class MarkLoserEliminated(
        val loserTeamId: Int?
    ) : PendingOperation()
}
