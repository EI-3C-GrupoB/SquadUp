package com.example.squadup.features.teams.createteam

import androidx.compose.ui.graphics.toArgb
import com.example.squadup.core.SupabaseClientProvider
import com.example.squadup.features.teams.TeamsTeamRow
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.storage.storage
import io.ktor.http.ContentType
import java.util.UUID
import kotlin.random.Random

class CreateTeamRepository(
    private val supabaseClient: SupabaseClient = SupabaseClientProvider.client
) {
    suspend fun createTeam(state: CreateTeamUiState, logoBytes: ByteArray?): Result<Unit> {
        return try {
            val owner = getCurrentUserRow() ?: throw Exception("Utilizador não encontrado")

            // 1. Inserir a equipa primeiro (sem logo)
            val insertResult = supabaseClient
                .from("equipa")
                .insert(
                    CreateTeamInsertRow(
                        name = state.teamName.trim(),
                        inviteCode = generateInviteCode(state.teamName),
                        ownerId = owner.id,
                        modalidadeId = state.selectedSportType.value + 1,
                        description = state.teamDescription.trim().ifBlank { null },
                        isPrivate = state.isPrivateTeam,
                        primaryColor = String.format("#%06X", (state.selectedSportType.color.toArgb() and 0xFFFFFF)),
                        logoUrl = null // Logo será feito upload depois
                    )
                ) {
                    select()
                }

            val createdTeam = insertResult.decodeSingle<TeamsTeamRow>()

            // 2. Upload do logo se existir (agora temos o ID da equipa)
            if (logoBytes != null) {
                val fileName = "team_${createdTeam.id}/logo_${UUID.randomUUID()}.jpg"
                val bucket = supabaseClient.storage.from("team-logos")
                bucket.upload(fileName, logoBytes) {
                    contentType = ContentType.Image.JPEG
                }
                
                val url = bucket.publicUrl(fileName)
                
                // Atualizar a equipa com o URL do logo
                supabaseClient
                    .from("equipa")
                    .update(
                        mapOf("emblema" to url)
                    ) {
                        filter {
                            eq("id", createdTeam.id)
                        }
                    }
            }

            // 3. Criar a inscrição automática do criador como Capitão
            supabaseClient
                .from("inscricao")
                .insert(
                    CreateTeamRegistrationInsertRow(
                        teamId = createdTeam.id,
                        userId = owner.id
                    )
                )

            Result.success(Unit)
        } catch (exception: Exception) {
            android.util.Log.e("CreateTeamRepo", "Erro detalhado: ${exception.message}", exception)
            Result.failure(exception)
        }
    }

    private suspend fun getCurrentUserRow(): CreateTeamUserRow? {
        val authUserId = supabaseClient.auth.currentUserOrNull()?.id ?: return null
        return runCatching {
            supabaseClient
                .from("utilizador")
                .select {
                    filter {
                        eq("auth_user_id", authUserId)
                    }
                }
                .decodeSingle<CreateTeamUserRow>()
        }.getOrNull()
    }

    private fun generateInviteCode(teamName: String): String {
        val prefix = teamName
            .filter { it.isLetterOrDigit() }
            .take(3)
            .uppercase()
            .padEnd(3, 'X')

        return "$prefix-${Random.nextInt(1000, 9999)}"
    }
}
