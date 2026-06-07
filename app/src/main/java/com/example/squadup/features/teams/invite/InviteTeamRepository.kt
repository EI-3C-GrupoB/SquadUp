package com.example.squadup.features.teams.invite

import com.example.squadup.core.SupabaseClientProvider
import com.example.squadup.features.teams.NotificationInsert
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from

class InviteTeamRepository(
    private val supabaseClient: SupabaseClient = SupabaseClientProvider.client
) {
    suspend fun getInviteState(teamId: String): Result<InviteTeamUiState> {
        return try {
            val currentUser = getCurrentUserRow()
                ?: throw Exception("Utilizador não encontrado")

            val team = supabaseClient
                .from("equipa")
                .select {
                    filter {
                        eq("id", teamId.toInt())
                    }
                }
                .decodeSingle<InviteTeamTeamRow>()

            if (team.ownerId != currentUser.id) {
                throw Exception("Apenas o capitão pode convidar membros para esta equipa.")
            }

            val existingMembers = supabaseClient
                .from("inscricao")
                .select {
                    filter {
                        eq("equipa_id", team.id)
                    }
                }
                .decodeList<InviteTeamRegistrationRow>()
                .mapNotNull { it.userId }
                .toSet()

            val pendingInvites = supabaseClient
                .from("convite")
                .select {
                    filter {
                        eq("equipa_id", team.id)
                        eq("estado", "pendente")
                    }
                }
                .decodeList<InviteTeamInviteRow>()
                .mapNotNull { it.invitedUserId }
                .toSet()

            val contacts = supabaseClient
                .from("utilizador")
                .select()
                .decodeList<InviteTeamUserRow>()
                .filter { user ->
                    user.id != currentUser.id &&
                            user.id !in existingMembers
                }
                .take(5)
                .map { user ->
                    SuggestedContactItem(
                        id = user.id.toString(),
                        name = user.name,
                        username = "@${user.username}",
                        subtitle = user.email.orEmpty(),
                        initials = user.name.initials(),
                        status = if (user.id in pendingInvites) {
                            InviteStatus.SENT
                        } else {
                            InviteStatus.INVITE
                        }
                    )
                }

            Result.success(
                InviteTeamUiState(
                    inviteCode = team.inviteCode.orEmpty(),
                    selectedTeamId = team.id.toString(),
                    suggestedContacts = contacts
                )
            )
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }

    suspend fun inviteUser(
        teamId: String,
        userId: String
    ): Result<Unit> {
        return try {
            val inviter = getCurrentUserRow()
                ?: throw Exception("Utilizador não encontrado")

            val parsedTeamId = teamId.toIntOrNull()
                ?: throw Exception("Equipa inválida")

            val parsedUserId = userId.toIntOrNull()
                ?: throw Exception("Utilizador inválido")

            if (parsedUserId == inviter.id) {
                throw Exception("Não podes convidar-te a ti próprio.")
            }

            val team = supabaseClient
                .from("equipa")
                .select {
                    filter {
                        eq("id", parsedTeamId)
                    }
                }
                .decodeSingle<InviteTeamTeamRow>()

            if (team.ownerId != inviter.id) {
                throw Exception("Apenas o capitão pode convidar membros para esta equipa.")
            }

            val existingRegistrations = supabaseClient
                .from("inscricao")
                .select {
                    filter {
                        eq("equipa_id", parsedTeamId)
                        eq("user_id", parsedUserId)
                    }
                }
                .decodeList<InviteTeamRegistrationRow>()

            if (existingRegistrations.isNotEmpty()) {
                throw Exception("Este utilizador já pertence à equipa.")
            }

            val pendingInvites = supabaseClient
                .from("convite")
                .select {
                    filter {
                        eq("equipa_id", parsedTeamId)
                        eq("convidado_user_id", parsedUserId)
                        eq("tipo", "convite")
                        eq("estado", "pendente")
                    }
                }
                .decodeList<InviteTeamInviteRow>()

            if (pendingInvites.isNotEmpty()) {
                throw Exception("Este utilizador já tem um convite pendente.")
            }

            val invitedUser = supabaseClient
                .from("utilizador")
                .select {
                    filter {
                        eq("id", parsedUserId)
                    }
                }
                .decodeSingle<InviteTeamUserRow>()

            val insertResult = supabaseClient
                .from("convite")
                .insert(
                    InviteTeamInsertRow(
                        teamId = parsedTeamId,
                        invitedUserId = parsedUserId,
                        inviterUserId = inviter.id,
                        status = "pendente",
                        type = "convite"
                    )
                ) {
                    select()
                }

            val createdInvite = insertResult.decodeSingle<InviteTeamInviteRow>()

            supabaseClient
                .from("notificacao")
                .insert(
                    NotificationInsert(
                        userId = invitedUser.id,
                        title = "Convite para equipa",
                        description = "${inviter.name} convidou-te para entrar na equipa ${team.name}.",
                        tipo = "equipa",
                        referenceId = createdInvite.id,
                        referenceType = "convite"
                    )
                )

            Result.success(Unit)
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }

    suspend fun inviteByUsernameOrEmail(teamId: String, value: String): Result<Unit> {
        return try {
            val normalizedValue = value.trim().removePrefix("@")

            if (normalizedValue.isBlank()) {
                throw Exception("Introduz um username ou email.")
            }

            val user = supabaseClient
                .from("utilizador")
                .select()
                .decodeList<InviteTeamUserRow>()
                .firstOrNull {
                    it.username.equals(normalizedValue, ignoreCase = true) ||
                            it.email.equals(normalizedValue, ignoreCase = true)
                }
                ?: throw Exception("Utilizador não encontrado.")

            inviteUser(teamId, user.id.toString())
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }

    private suspend fun getCurrentUserRow(): InviteTeamUserRow? {
        val authUserId = supabaseClient.auth.currentUserOrNull()?.id ?: return null

        return runCatching {
            supabaseClient
                .from("utilizador")
                .select {
                    filter {
                        eq("auth_user_id", authUserId)
                    }
                }
                .decodeSingle<InviteTeamUserRow>()
        }.getOrNull()
    }

    private fun String.initials(): String {
        return split(" ")
            .filter { it.isNotBlank() }
            .take(2)
            .joinToString("") { it.first().uppercase() }
    }
}
