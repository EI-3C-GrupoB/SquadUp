package com.example.squadup.features.profile

import com.example.squadup.R
import com.example.squadup.core.SupabaseClientProvider
import com.example.squadup.core.enums.UserRole
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.storage.storage
import io.ktor.http.ContentType
import java.util.UUID

class ProfileRepository(
    private val supabaseClient: SupabaseClient = SupabaseClientProvider.client
) {
    suspend fun getCurrentProfile(): Result<ProfileData> {
        return try {
            val authUserId = supabaseClient.auth.currentUserOrNull()?.id
                ?: return Result.failure(ProfileException(R.string.profile_error_not_logged_in))
            
            android.util.Log.d("ProfileRepo", "Loading profile for auth_user_id: $authUserId")

            val response = try {
                supabaseClient
                    .from("utilizador")
                    .select {
                        filter {
                            eq("auth_user_id", authUserId)
                        }
                    }
            } catch (e: Exception) {
                android.util.Log.e("ProfileRepo", "Query failed: ${e.message}", e)
                return Result.failure(ProfileException(R.string.profile_error_load))
            }
            
            android.util.Log.d("ProfileRepo", "Raw data: ${response.data}")
            val user = try {
                response.decodeList<UserProfileRow>().firstOrNull()
            } catch (e: Exception) {
                android.util.Log.e("ProfileRepo", "Decoding failed: ${e.message}", e)
                null
            } ?: run {
                android.util.Log.e("ProfileRepo", "No user found or decoding failed for auth_user_id: $authUserId")
                return Result.failure(ProfileException(R.string.profile_error_load))
            }

            val stats = try { getPlayerStats(user.id) } catch (e: Exception) { null }
            
            Result.success(
                ProfileData(
                    id = user.id,
                    displayName = user.name.orEmpty().ifBlank { user.username.orEmpty() }.ifBlank { "User ${user.id}" },
                    username = user.username.orEmpty(),
                    isAdmin = user.isAdmin ?: false,
                    role = UserRole.fromInt(user.accountType),
                    playStyle = user.playStyle,
                    photoUrl = user.photoUrl,
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

    suspend fun uploadAvatar(photoBytes: ByteArray): Result<String> {
        return try {
            val authUserId = supabaseClient.auth.currentUserOrNull()?.id
                ?: return Result.failure(ProfileException(R.string.profile_error_not_logged_in))
            
            android.util.Log.d("ProfileRepo", "Loading profile for auth_user_id: $authUserId")

            val fileName = "user_${authUserId}/avatar_${UUID.randomUUID()}.jpg"
            val bucket = supabaseClient.storage.from("user-avatars")
            bucket.upload(fileName, photoBytes) {
                contentType = ContentType.Image.JPEG
                upsert = true
            }
            val url = bucket.publicUrl(fileName)

            supabaseClient
                .from("utilizador")
                .update(mapOf("foto_url" to url)) {
                    filter { eq("auth_user_id", authUserId) }
                }

            Result.success(url)
        } catch (exception: Exception) {
            android.util.Log.e("ProfileRepo", "Error uploading avatar: ${exception.message}", exception)
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

}
