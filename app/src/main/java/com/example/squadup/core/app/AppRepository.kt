package com.example.squadup.core.app

import com.example.squadup.core.SupabaseClientProvider
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from

class AppRepository(
    private val supabaseClient: SupabaseClient = SupabaseClientProvider.client
) {
    fun isLoggedIn(): Boolean = supabaseClient.auth.currentUserOrNull() != null

    suspend fun loadCurrentUser(): Result<LoggedInUser> {
        return try {
            val authUserId = supabaseClient.auth.currentUserOrNull()?.id
                ?: return Result.failure(Exception("No authenticated user"))

            val row = supabaseClient
                .from("utilizador")
                .select {
                    filter {
                        eq("auth_user_id", authUserId)
                    }
                }
                .decodeSingle<LoggedInUserRow>()

            Result.success(
                LoggedInUser(
                    id = row.id,
                    displayName = row.name,
                    username = row.username,
                    isAdmin = row.isAdmin
                )
            )
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }

    suspend fun logout(): Result<Unit> {
        return try {
            supabaseClient.auth.signOut()
            Result.success(Unit)
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }
}
