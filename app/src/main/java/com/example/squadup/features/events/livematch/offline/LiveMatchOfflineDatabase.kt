package com.example.squadup.features.events.livematch.offline

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [CachedGameEntity::class, PendingOperationEntity::class],
    version = 1,
    exportSchema = false
)
abstract class LiveMatchOfflineDatabase : RoomDatabase() {

    abstract fun liveMatchOfflineDao(): LiveMatchOfflineDao

    companion object {
        @Volatile
        private var instance: LiveMatchOfflineDatabase? = null

        fun getInstance(context: Context): LiveMatchOfflineDatabase {
            return instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    LiveMatchOfflineDatabase::class.java,
                    "live_match_offline.db"
                ).fallbackToDestructiveMigration().build().also { instance = it }
            }
        }
    }
}
