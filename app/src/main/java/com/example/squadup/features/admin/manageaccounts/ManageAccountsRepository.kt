package com.example.squadup.features.admin.manageaccounts

import android.util.Log
import com.example.squadup.core.SupabaseClientProvider
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from

class ManageAccountsRepository(
    private val supabaseClient: SupabaseClient = SupabaseClientProvider.client
) {
    suspend fun loadUsers(): Result<List<ManageAccountItem>> {
        return try {
            val rows = supabaseClient
                .from("utilizador")
                .select()
                .decodeList<ManageAccountRow>()

            Result.success(
                rows.map { row -> row.toManageAccountItem() }
            )
        } catch (exception: Exception) {
            Log.e("ManageAccountsRepository", "Error loading users", exception)
            Result.failure(exception)
        }
    }
}
