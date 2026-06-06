package com.example.squadup.features.profile.edit

import com.example.squadup.R
import com.example.squadup.core.SupabaseClientProvider
import com.example.squadup.core.enums.PlayStyle
import com.example.squadup.core.enums.SportType
import com.example.squadup.core.ui.components.SelectedLocation
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import kotlinx.serialization.Serializable

class EditProfileRepository(
    private val supabaseClient: SupabaseClient = SupabaseClientProvider.client
) {
    suspend fun getCurrentProfile(): Result<EditableProfile> {
        return try {
            val authUserId = supabaseClient.auth.currentUserOrNull()?.id
                ?: return Result.failure(EditProfileException(R.string.profile_error_not_logged_in))

            val profile = supabaseClient
                .from("utilizador")
                .select() {
                    filter {
                        eq("auth_user_id", authUserId)
                    }
                }
                .decodeList<EditableProfileRow>()
                .firstOrNull() ?: throw Exception("Profile not found")

            android.util.Log.d("EditProfileRepo", "Loaded user: $profile")

            val selectedSports = getUserSports(profile.id)
            val location = profile.locId?.let { getLoc(it) }

            Result.success(
                EditableProfile(
                    id = profile.id,
                    name = profile.name.orEmpty(),
                    username = profile.username.orEmpty(),
                    playStyle = PlayStyle.fromLevel(profile.playStyle ?: 3),
                    sports = selectedSports,
                    photoUrl = profile.photoUrl,
                    location = location
                )
            )
        } catch (exception: Exception) {
            android.util.Log.e("EditProfileRepo", "Error: ${exception.message}", exception)
            Result.failure(EditProfileException(R.string.editProfile_error_load))
        }
    }

    suspend fun updateProfile(update: EditProfileUpdate): Result<Unit> {
        return try {
            val authUserId = supabaseClient.auth.currentUserOrNull()?.id
                ?: return Result.failure(EditProfileException(R.string.profile_error_not_logged_in))

            val profile = supabaseClient
                .from("utilizador")
                .select() { filter { eq("auth_user_id", authUserId) } }
                .decodeSingle<EditableProfileRow>()

            var newLocId: Long? = profile.locId

            if (update.location != null) {
                newLocId = supabaseClient
                    .from("localizacao")
                    .insert(
                        LocInsert(
                            geopos = "SRID=4326;POINT(${update.location.lng} ${update.location.lat})",
                            morada = update.location.address
                        )
                    ) { select() }
                    .decodeSingle<LocRow>()
                    .id
            }

            supabaseClient
                .from("utilizador")
                .update(
                    EditUserProfileUpdateRow(
                        name = update.name,
                        username = update.username,
                        playStyle = update.playStyle.level,
                        locId = newLocId
                    )
                ) {
                    filter {
                        eq("auth_user_id", authUserId)
                    }
                }

            replaceUserSports(profile.id, update.sports)

            Result.success(Unit)
        } catch (exception: Exception) {
            android.util.Log.e("EditProfileRepo", "Save error: ${exception.message}", exception)
            Result.failure(EditProfileException(R.string.editProfile_error_save))
        }
    }

    private suspend fun getLoc(locId: Long): SelectedLocation? {
        return try {
            // Acedemos apenas ao campo 'morada' da tabela localizacao
            val response = supabaseClient
                .from("localizacao")
                .select(io.github.jan.supabase.postgrest.query.Columns.raw("morada")) {
                    filter { eq("id", locId) }
                }
            
            val data = response.decodeSingle<LocRowSimple>()
            
            SelectedLocation(
                lat = 38.7223, // Posição default (Lisboa)
                lng = -9.1393,
                address = data.morada.orEmpty()
            )
        } catch (e: Exception) {
            android.util.Log.e("EditProfileRepo", "Erro ao ler morada: ${e.message}")
            null
        }
    }

    @Serializable
    private data class LocRowSimple(val morada: String? = null)

    @Serializable
    private data class LocRow(val id: Long)

    @Serializable
    private data class LocInsert(val geopos: String, val morada: String?)

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
