package com.example.squadup.features.events.livematch.offline

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cached_game")
data class CachedGameEntity(
    @PrimaryKey val gameId: String,
    val stateJson: String,
    val updatedAt: Long
)
