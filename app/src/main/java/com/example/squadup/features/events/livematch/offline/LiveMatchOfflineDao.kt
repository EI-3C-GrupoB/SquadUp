package com.example.squadup.features.events.livematch.offline

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface LiveMatchOfflineDao {

    @Query("SELECT * FROM cached_game WHERE gameId = :gameId")
    suspend fun getCachedGame(gameId: String): CachedGameEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertCachedGame(entity: CachedGameEntity)

    @Query("SELECT * FROM pending_operation WHERE gameId = :gameId ORDER BY localId ASC")
    suspend fun getPendingOperations(gameId: String): List<PendingOperationEntity>

    @Insert
    suspend fun insertPendingOperation(entity: PendingOperationEntity)

    @Query("DELETE FROM pending_operation WHERE localId = :localId")
    suspend fun deletePendingOperation(localId: Long)
}
