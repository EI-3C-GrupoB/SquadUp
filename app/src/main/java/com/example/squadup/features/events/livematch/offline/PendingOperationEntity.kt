package com.example.squadup.features.events.livematch.offline

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pending_operation")
data class PendingOperationEntity(
    @PrimaryKey(autoGenerate = true) val localId: Long = 0,
    val gameId: String,
    val opJson: String,
    val createdAt: Long
)
