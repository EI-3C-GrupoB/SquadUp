package com.example.squadup.features.profile

import com.example.squadup.R
import com.example.squadup.core.SupabaseClientProvider
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from

class ProfileRepository(
    private val supabaseClient: SupabaseClient = SupabaseClientProvider.client
) {
    suspend fun getCurrentProfile(): Result<ProfileData> {
        return try {
            val authUserId = supabaseClient.auth.currentUserOrNull()?.id
                ?: return Result.failure(ProfileException(R.string.profile_error_not_logged_in))

            var userRow: UserProfileRow? = null
            repeat(3) { attempt ->
                userRow = supabaseClient
                    .from("utilizador")
                    .select {
                        filter {
                            eq("auth_user_id", authUserId)
                        }
                    }
                    .decodeList<UserProfileRow>()
                    .firstOrNull()
                
                if (userRow != null) return@repeat
                if (attempt < 2) kotlinx.coroutines.delay(1000)
            }

            val user = userRow ?: return Result.failure(ProfileException(R.string.profile_error_not_logged_in))

            val stats = try { getPlayerStats(user.id) } catch (e: Exception) { null }
            val roleNames = try { getRoleNames(user.id) } catch (e: Exception) { emptyList() }

            Result.success(
                ProfileData(
                    id = user.id,
                    displayName = user.name.ifBlank { user.username }.ifBlank { "User ${user.id}" },
                    username = user.username,
                    isAdmin = user.isAdmin ?: false,
                    roleNames = roleNames,
                    playStyle = user.playStyle,
                    matchesPlayed = stats?.totalMatches ?: 0,
                    goals = stats?.totalGoals ?: 0,
                    teams = stats?.totalTeams ?: 0
                )
            )
        } catch (exception: Exception) {
            android.util.Log.e("ProfileRepo", "Error loading profile: ${exception.message}", exception)
            Result.failure(ProfileException(R.string.profile_error_load))
        }
    }

    suspend fun logout(): Result<Unit> {
        return try {
            supabaseClient.auth.signOut()
            Result.success(Unit)
        } catch (exception: Exception) {
            Result.failure(ProfileException(R.string.profile_error_logout))
        }
    }

    private suspend fun getPlayerStats(userId: Int): PlayerStatsRow? {
        return runCatching {
            supabaseClient
                .from("estatistica_jogador")
                .select {
                    filter {
                        eq("user_id", userId)
                    }
                    limit(1)
                }
                .decodeSingle<PlayerStatsRow>()
        }.getOrNull()
    }

    private suspend fun getRoleNames(userId: Int): List<String> {
        val links = runCatching {
            supabaseClient
                .from("utilizador_tipoutilizador")
                .select {
                    filter {
                        eq("user_id", userId)
                    }
                }
                .decodeList<UserTypeLinkRow>()
        }.getOrDefault(emptyList())

        if (links.isEmpty()) return emptyList()

        val userTypes = runCatching {
            supabaseClient
                .from("tipo_utilizador")
                .select()
                .decodeList<UserTypeRow>()
        }.getOrDefault(emptyList())

        val typeIds = links.map { it.userTypeId }.toSet()
        return userTypes
            .filter { it.id in typeIds }
            .map { it.type }
    }
}
