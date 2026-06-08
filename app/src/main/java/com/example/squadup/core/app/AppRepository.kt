package com.example.squadup.core.app

import com.example.squadup.core.SupabaseClientProvider
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.status.SessionStatus
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.flow.StateFlow

class AppRepository(
    private val supabaseClient: SupabaseClient = SupabaseClientProvider.client
) {
    val sessionStatus: StateFlow<SessionStatus> = supabaseClient.auth.sessionStatus

    suspend fun loadCurrentUser(): Result<LoggedInUser> {
        return try {
            val authUserId = supabaseClient.auth.currentUserOrNull()?.id
                ?: return Result.failure(Exception("No authenticated user"))

            // Tenta carregar o perfil com retentativas (útil logo após o registo)
            var row: LoggedInUserRow? = null
            repeat(3) { attempt ->
                row = supabaseClient
                    .from("utilizador")
                    .select {
                        filter {
                            eq("auth_user_id", authUserId)
                        }
                    }
                    .decodeList<LoggedInUserRow>()
                    .firstOrNull()
                
                if (row != null) return@repeat
                if (attempt < 2) kotlinx.coroutines.delay(1000)
            }

            val userRow = row ?: return Result.failure(Exception("User profile not found in database"))

            Result.success(
                LoggedInUser(
                    id = userRow.id,
                    displayName = userRow.name.orEmpty(),
                    username = userRow.username.orEmpty(),
                    isAdmin = userRow.isAdmin ?: false,
                    photoUrl = userRow.photoUrl,
                    userRole = com.example.squadup.core.enums.UserRole.fromInt(userRow.accountType)
                )
            )
        } catch (exception: Exception) {
            android.util.Log.e("AppRepository", "Error loading current user: ${exception.message}", exception)
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
