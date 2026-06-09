package com.example.squadup.features.events.formteams

import com.example.squadup.core.SupabaseClientProvider
import com.example.squadup.core.enums.SportType
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

class FormTeamsRepository(
    private val supabaseClient: SupabaseClient = SupabaseClientProvider.client
) {
    suspend fun loadEvent(eventId: String): Result<FormTeamsUiState> {
        return try {
            val id = eventId.toIntOrNull() ?: throw Exception("Evento inválido.")

            val event = supabaseClient.from("evento")
                .select { filter { eq("id", id) } }
                .decodeSingle<FormEventRow>()

            val modality = event.modalityId?.let { mid ->
                runCatching {
                    supabaseClient.from("modalidade")
                        .select { filter { eq("id", mid) } }
                        .decodeSingle<FormModalityRow>()
                        .modalityName
                }.getOrNull()
            }

            // All accepted individual registrations for this event
            val registrations = supabaseClient.from("inscricao")
                .select {
                    filter {
                        eq("evento_id", id)
                        eq("tipo_inscricao", "evento_individual")
                        eq("estado_inscricao", "aceite")
                    }
                }
                .decodeList<FormRegistrationRow>()

            val userIds = registrations.mapNotNull { it.userId }.toSet()
            val users: Map<Int, FormUserRow> = if (userIds.isEmpty()) emptyMap() else {
                val all = supabaseClient.from("utilizador").select().decodeList<FormUserRow>()
                all.filter { row -> row.userId in userIds }.associateBy { row -> row.userId }
            }

            // Load existing evento_equipa entries (already formed teams)
            val existingEventTeams = supabaseClient.from("evento_equipa")
                .select { filter { eq("evento_id", id) } }
                .decodeList<FormEventTeamRow>()

            val existingTeamIds = existingEventTeams.map { it.teamId }.toSet()

            // Load equipa names for already-created teams
            val existingTeams: Map<Int, String> = if (existingTeamIds.isEmpty()) emptyMap() else {
                supabaseClient.from("equipa").select().decodeList<FormTeamNameRow>()
                    .filter { it.id in existingTeamIds }
                    .associate { it.id to it.name }
            }

            val sportType = sportTypeFromModality(modality)
            val capacity = sportCapacity(sportType)

            // Build player map
            fun FormRegistrationRow.toPlayer(): FormTeamPlayer? {
                val uid = userId ?: return null
                val user = users[uid] ?: return null
                return FormTeamPlayer(
                    userId = user.userId,
                    registrationId = id,
                    name = user.userName,
                    initials = user.userName.initials(),
                    experienceLevel = user.playStyle ?: 1
                )
            }

            val teams: List<FormTeam>
            val unassigned: List<FormTeamPlayer>

            if (existingTeamIds.isNotEmpty()) {
                // Already saved — restore state from DB
                val teamsList = existingEventTeams.mapIndexed { i, et ->
                    val teamName = existingTeams[et.teamId] ?: "Equipa ${('A' + i)}"
                    val assigned = registrations
                        .filter { it.teamId == et.teamId }
                        .mapNotNull { it.toPlayer() }
                    FormTeam(
                        index = i,
                        name = teamName,
                        players = assigned,
                        capacity = capacity,
                        savedTeamId = et.teamId
                    )
                }
                val assignedRegIds = registrations.filter { it.teamId != null }.map { it.id }.toSet()
                unassigned = registrations
                    .filter { it.id !in assignedRegIds }
                    .mapNotNull { it.toPlayer() }
                teams = teamsList
            } else {
                // Fresh — no teams yet, all players unassigned
                val numTeams = (event.maxTeams ?: 2).coerceAtLeast(2)
                unassigned = registrations.mapNotNull { it.toPlayer() }
                teams = (0 until numTeams).map { i ->
                    FormTeam(index = i, name = "Equipa ${('A' + i)}", capacity = capacity)
                }
            }

            Result.success(
                FormTeamsUiState(
                    isLoading = false,
                    eventId = eventId,
                    eventName = event.title,
                    sportType = sportType,
                    maxTeams = teams.size,
                    unassignedPlayers = unassigned,
                    teams = teams
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun saveTeams(
        eventId: String,
        teams: List<FormTeam>,
        unassignedPlayers: List<FormTeamPlayer>,
        sportType: SportType
    ): Result<Unit> {
        return try {
            val id = eventId.toIntOrNull() ?: throw Exception("Evento inválido.")
            val modalityId = getModalityId(sportType)

            // 1. Clear equipa_id for unassigned players first
            unassignedPlayers.forEach { player ->
                supabaseClient.from("inscricao")
                    .update(FormRegistrationClearTeamRow()) {
                        filter { eq("id", player.registrationId) }
                    }
            }

            // 2. Process each non-empty team
            teams.filter { it.players.isNotEmpty() }.forEach { team ->
                val teamId: Int

                if (team.savedTeamId != null) {
                    teamId = team.savedTeamId
                    // Update name in DB if changed
                    supabaseClient.from("equipa")
                        .update(FormTeamNameUpdateRow(name = team.name)) {
                            filter { eq("id", teamId) }
                        }
                } else {
                    // Create new equipa
                    val insertResult = supabaseClient.from("equipa")
                        .insert(FormTeamInsertRow(name = team.name, modalityId = modalityId)) {
                            select()
                        }
                    val newTeam = insertResult.decodeSingle<FormTeamCreatedRow>()
                    teamId = newTeam.teamId

                    // Link to event
                    supabaseClient.from("evento_equipa")
                        .insert(FormEventTeamInsertRow(eventId = id, teamId = teamId, status = "confirmada"))
                }

                // Assign players
                team.players.forEach { player ->
                    supabaseClient.from("inscricao")
                        .update(FormRegistrationUpdateRow(teamId = teamId)) {
                            filter { eq("id", player.registrationId) }
                        }
                }
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun getModalityId(sportType: SportType): Int? {
        return runCatching {
            val all = supabaseClient.from("modalidade").select().decodeList<FormModalityRow>()
            all.firstOrNull { sportType.matchesModality(it.modalityName) }?.modalityId
        }.getOrNull()
    }

    private fun SportType.matchesModality(name: String): Boolean {
        val n = name.lowercase()
        return when (this) {
            SportType.SOCCER -> "futeb" in n || "soccer" in n
            SportType.FUTSAL -> "futsal" in n
            SportType.BASKETBALL -> "basket" in n || "basquet" in n
            SportType.VOLLEYBALL -> "volei" in n || "volley" in n
            SportType.PADDLE -> "padel" in n || "paddle" in n || "tenis" in n || "tennis" in n
        }
    }

    private fun sportTypeFromModality(name: String?): SportType {
        val n = name?.lowercase() ?: return SportType.SOCCER
        return when {
            "futsal" in n -> SportType.FUTSAL
            "futeb" in n || "soccer" in n -> SportType.SOCCER
            "basket" in n -> SportType.BASKETBALL
            "volei" in n || "volley" in n -> SportType.VOLLEYBALL
            "padel" in n || "paddle" in n || "tenis" in n -> SportType.PADDLE
            else -> SportType.SOCCER
        }
    }

    private fun sportCapacity(sportType: SportType): Int = when (sportType) {
        SportType.SOCCER -> 11
        SportType.FUTSAL -> 5
        SportType.BASKETBALL -> 5
        SportType.VOLLEYBALL -> 6
        SportType.PADDLE -> 2
    }

    private fun String.initials(): String {
        val parts = trim().split(" ")
        return when {
            parts.size >= 2 -> "${parts.first().first()}${parts.last().first()}".uppercase()
            parts.isNotEmpty() -> parts.first().take(2).uppercase()
            else -> "??"
        }
    }
}

// ─── Serializable rows ────────────────────────────────────────────────────────

@Serializable
private data class FormEventRow(
    val id: Int,
    @SerialName("titulo") val title: String,
    @SerialName("max_equipas") val maxTeams: Int? = null,
    @SerialName("modalidade_id") val modalityId: Int? = null
)

@Serializable
private data class FormModalityRow(
    @SerialName("id") val modalityId: Int,
    @SerialName("nome") val modalityName: String
)

@Serializable
private data class FormRegistrationRow(
    val id: Int,
    @SerialName("user_id") val userId: Int? = null,
    @SerialName("equipa_id") val teamId: Int? = null
)

@Serializable
private data class FormUserRow(
    @SerialName("id") val userId: Int,
    @SerialName("nome") val userName: String,
    @SerialName("play_style") val playStyle: Int? = null
)

@Serializable
private data class FormEventTeamRow(
    @SerialName("equipa_id") val teamId: Int,
    @SerialName("evento_id") val eventId: Int
)

@Serializable
private data class FormTeamNameRow(
    val id: Int,
    @SerialName("nome") val name: String
)

@Serializable
private data class FormTeamInsertRow(
    @SerialName("nome") val name: String,
    @SerialName("modalidade_id") val modalityId: Int? = null
)

@Serializable
private data class FormTeamCreatedRow(
    @SerialName("id") val teamId: Int
)

@Serializable
private data class FormEventTeamInsertRow(
    @SerialName("evento_id") val eventId: Int,
    @SerialName("equipa_id") val teamId: Int,
    @SerialName("estado") val status: String
)

@Serializable
private data class FormRegistrationUpdateRow(
    @SerialName("equipa_id") val teamId: Int
)

@Serializable
private data class FormRegistrationClearTeamRow(
    @SerialName("equipa_id") val teamId: Int? = null
)

@Serializable
private data class FormTeamNameUpdateRow(
    @SerialName("nome") val name: String
)
