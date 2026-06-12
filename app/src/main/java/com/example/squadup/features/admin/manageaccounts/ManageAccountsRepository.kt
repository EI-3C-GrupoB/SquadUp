package com.example.squadup.features.admin.manageaccounts

import com.example.squadup.core.SupabaseClientProvider
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.realtime.PostgresAction
import io.github.jan.supabase.realtime.channel
import io.github.jan.supabase.realtime.postgresChangeFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class ManageAccountsRepository(
    private val supabaseClient: SupabaseClient = SupabaseClientProvider.client
) {
    suspend fun getUsers(): Result<List<ManageAccountItem>> {
        return try {
            val rows = supabaseClient
                .from("utilizador")
                .select()
                .decodeList<ManageAccountRow>()
            Result.success(rows.map { it.toManageAccountItem() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getUsersRealtime(): Flow<List<ManageAccountItem>> = flow {
        // Emitir estado inicial
        val initialUsers = getUsers().getOrDefault(emptyList())
        emit(initialUsers)

        val channel = supabaseClient.channel("manage_accounts_realtime_${System.currentTimeMillis()}")
        
        val changes = channel.postgresChangeFlow<PostgresAction>(schema = "public") {
            table = "utilizador"
        }

        channel.subscribe()

        emitAll(
            changes.map {
                // Sempre que houver uma mudança, recarregamos a lista completa
                // para garantir consistência (especialmente em deletes ou updates complexos)
                getUsers().getOrDefault(emptyList())
            }
        )
    }
}
