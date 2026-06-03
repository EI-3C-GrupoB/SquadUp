package com.example.squadup.features.profile.edit

import com.example.squadup.R
import com.example.squadup.core.SupabaseClientProvider
import com.example.squadup.core.enums.PlayStyle
import com.example.squadup.core.enums.SportType
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from

class EditProfileRepository(
    private val supabaseClient: SupabaseClient = SupabaseClientProvider.client
) {
    suspend fun getCurrentProfile(): Result<EditableProfile> {
        return try {
            val authUserId = supabaseClient.auth.currentUserOrNull()?.id
                ?: return Result.failure(EditProfileException(R.string.profile_error_not_logged_in))

            val profile = supabaseClient
                .from("utilizador")
                .select {
                    filter {
                        eq("auth_user_id", authUserId)
                    }
                }
                .decodeSingle<EditableProfileRow>()

            val selectedSports = getUserSports(profile.id)

            Result.success(
                EditableProfile(
                    id = profile.id,
                    username = profile.username,
                    playStyle = profile.playStyle.toPlayStyle(),
                    sports = selectedSports
                )
            )
        } catch (exception: Exception) {
            Result.failure(EditProfileException(R.string.editProfile_error_load))
        }
    }

    suspend fun updateProfile(update: EditProfileUpdate): Result<Unit> {
        return try {
            val authUserId = supabaseClient.auth.currentUserOrNull()?.id
                ?: return Result.failure(EditProfileException(R.string.profile_error_not_logged_in))

            val profile = supabaseClient
                .from("utilizador")
                .update(
                    EditUserProfileUpdateRow(
                        username = update.username,
                        playStyle = update.playStyle.databaseValue
                    )
                ) {
                    filter {
                        eq("auth_user_id", authUserId)
                    }
                    select()
                }
                .decodeSingle<EditableProfileRow>()

            replaceUserSports(profile.id, update.sports)

            Result.success(Unit)
        } catch (exception: Exception) {
            Result.failure(EditProfileException(R.string.editProfile_error_save))
        }
    }

    private suspend fun getUserSports(userId: Int): List<SportType> {
        val links = runCatching {
            supabaseClient
                .from("utilizador_modalidade")
                .select {
                    filter {
                        eq("user_id", userId)
                    }
                }
                .decodeList<EditUserModalityRow>()
        }.getOrDefault(emptyList())

        if (links.isEmpty()) return emptyList()

        val modalities = supabaseClient
            .from("modalidade")
            .select()
            .decodeList<EditModalityRow>()

        val selectedIds = links.map { it.modalityId }.toSet()
        return modalities
            .filter { it.id in selectedIds }
            .mapNotNull { it.name.toSportType() }
    }

    private suspend fun replaceUserSports(userId: Int, sports: List<SportType>) {
        supabaseClient
            .from("utilizador_modalidade")
            .delete {
                filter {
                    eq("user_id", userId)
                }
            }

        if (sports.isEmpty()) return

        val modalities = supabaseClient
            .from("modalidade")
            .select()
            .decodeList<EditModalityRow>()

        val inserts = sports.mapNotNull { sport ->
            modalities
                .firstOrNull { modality -> sport.matchesModality(modality.name) }
                ?.let { modality ->
                    EditUserModalityInsertRow(
                        userId = userId,
                        modalityId = modality.id
                    )
                }
        }

        if (inserts.isNotEmpty()) {
            supabaseClient
                .from("utilizador_modalidade")
                .insert(inserts)
        }
    }

    private fun String?.toPlayStyle(): PlayStyle {
        return when (this?.lowercase()) {
            "low", "baixa", "baixo" -> PlayStyle.LOW
            "medium", "media", "média", "medio", "médio" -> PlayStyle.MEDIUM
            "high", "alta", "alto" -> PlayStyle.HIGH
            else -> PlayStyle.HIGH
        }
    }

    private val PlayStyle.databaseValue: String
        get() = when (this) {
            PlayStyle.LOW -> "low"
            PlayStyle.MEDIUM -> "medium"
            PlayStyle.HIGH -> "high"
        }

    private fun String.toSportType(): SportType? {
        return SportType.entries.firstOrNull { it.matchesModality(this) }
    }

    private fun SportType.matchesModality(value: String): Boolean {
        val normalized = value.lowercase()
        return when (this) {
            SportType.SOCCER -> normalized in listOf("soccer", "football", "futebol")
            SportType.BASKETBALL -> normalized in listOf("basketball", "basquetebol")
            SportType.PADDLE -> normalized in listOf("paddle", "padel")
            SportType.VOLLEYBALL -> normalized in listOf("volleyball", "voleibol")
            SportType.FUTSAL -> normalized == "futsal"
        }
    }
}
