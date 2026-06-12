package com.example.squadup.features.events.livematch

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.squadup.core.network.NetworkMonitor
import com.example.squadup.features.events.livematch.offline.LiveMatchOfflineDatabase

class LiveMatchViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LiveMatchViewModel::class.java)) {
            val dao = LiveMatchOfflineDatabase.getInstance(context).liveMatchOfflineDao()
            val repository = LiveMatchRepository(offlineDao = dao)
            val networkMonitor = NetworkMonitor(context)
            return LiveMatchViewModel(repository, networkMonitor) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
