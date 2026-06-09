package com.example.squadup.features.profile.myevents

import com.example.squadup.core.SupabaseClientProvider
import com.example.squadup.core.enums.EventStatus
import com.example.squadup.core.enums.SportType
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class MyEventsRepository(
    private val supabaseClient: SupabaseClient = SupabaseClientProvider.client
) {
    suspend fun getMyEvents(): Result<List<MyEventItem>> {
        return try {
            val authUserId = supabaseClient.auth.currentUserOrNull()?.id
                ?: return Result.success(emptyList())

            val currentUser = supabaseClient
                .from("utilizador")
                .select { filter { eq("auth_user_id", authUserId) } }
                .decodeList<MyEventsUserRow>()
                .firstOrNull()
                ?: return Result.success(emptyList())

            val userId = currentUser.id

            // All events and registrations (small datasets)
            val allEvents = supabaseClient
                .from("evento")
                .select()
                .decodeList<MyEventsEventRow>()

            val allRegistrations = supabaseClient
                .from("inscricao")
                .select()
                .decodeList<MyEventsRegistrationRow>()

            val allEventTeams = supabaseClient
                .from("evento_equipa")
                .select()
                .decodeList<MyEventsEventTeamRow>()

            val allGames = supabaseClient
                .from("jogo")
                .select()
                .decodeList<MyEventsGameRow>()

            val modalities = supabaseClient
                .from("modalidade")
                .select()
                .decodeList<MyEventsModalityRow>()
                .associateBy { it.id }

            // Created by user
            val createdEventIds = allEvents
                .filter { it.creatorId == userId }
                .map { it.id }
                .toSet()

            // Participating in (via inscricao)
            val participatingEventIds = allRegistrations
                .filter { reg ->
                    reg.userId == userId &&
                            reg.status != "recusada" &&
                            reg.status != "banido" &&
                            reg.eventId != null &&
                            reg.eventId !in createdEventIds
                }
                .mapNotNull { it.eventId }
                .toSet()

            val relevantEventIds = createdEventIds + participatingEventIds

            if (relevantEventIds.isEmpty()) return Result.success(emptyList())

            val relevantEvents = allEvents
                .filter { it.id in relevantEventIds }
                .sortedByDescending { it.startDate.orEmpty() }

            val items = relevantEvents.map { event ->
                val eventTeams = allEventTeams.filter { it.eventId == event.id && it.status == "confirmada" }
                val individualAccepted = allRegistrations.count { reg ->
                    reg.eventId == event.id &&
                            reg.registrationType == "evento_individual" &&
                            reg.status == "aceite"
                }
                val liveGames = allGames.count { it.eventId == event.id && it.status == "a_decorrer" }
                val modality = event.modalityId?.let { modalities[it] }

                event.toMyEventItem(
                    teamsCount = eventTeams.size,
                    playersCount = individualAccepted + eventTeams.size,
                    registeredCount = eventTeams.size + individualAccepted,
                    matchesInProgress = liveGames,
                    sportType = sportTypeFrom(modality?.name),
                    isCreator = event.id in createdEventIds
                )
            }

            Result.success(items)
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }

    private fun MyEventsEventRow.toMyEventItem(
        teamsCount: Int,
        playersCount: Int,
        registeredCount: Int,
        matchesInProgress: Int,
        sportType: SportType,
        isCreator: Boolean
    ): MyEventItem {
        val dateTime = startDate.toLocalDateTimeOrNull()
        val dateLabel = dateTime
            ?.format(DateTimeFormatter.ofPattern("dd MMM", Locale("pt", "PT")))
            .orEmpty()

        return MyEventItem(
            id = id.toString(),
            title = title,
            location = address.orEmpty(),
            date = dateLabel,
            teamsCount = teamsCount,
            playersCount = playersCount,
            status = eventStatus.toEventStatus(),
            sportType = sportType,
            registeredCount = registeredCount,
            matchesInProgress = matchesInProgress,
            isCreator = isCreator
        )
    }

    private fun String?.toEventStatus(): EventStatus {
        return when (this) {
            "rascunho" -> EventStatus.DRAFT
            "publicado" -> EventStatus.REGISTRATION_OPEN
            "a_decorrer" -> EventStatus.ONGOING
            "terminado" -> EventStatus.FINISHED
            "cancelado" -> EventStatus.CANCELLED
            else -> EventStatus.DRAFT
        }
    }

    private fun sportTypeFrom(name: String?): SportType {
        val n = name.orEmpty().lowercase()
        return when {
            n in listOf("futebol", "football", "soccer") -> SportType.SOCCER
            n in listOf("basquetebol", "basketball") -> SportType.BASKETBALL
            n in listOf("padel", "paddle") -> SportType.PADDLE
            n in listOf("voleibol", "volleyball") -> SportType.VOLLEYBALL
            n == "futsal" -> SportType.FUTSAL
            else -> SportType.SOCCER
        }
    }

    private fun String?.toLocalDateTimeOrNull(): LocalDateTime? {
        if (isNullOrBlank()) return null
        return runCatching { LocalDateTime.parse(replace(" ", "T").take(19)) }.getOrNull()
    }
}

@Serializable
private data class MyEventsUserRow(
    val id: Int
)

@Serializable
private data class MyEventsEventRow(
    val id: Int,
    @SerialName("titulo") val title: String,
    @SerialName("morada") val address: String? = null,
    @SerialName("data_inicio") val startDate: String? = null,
    @SerialName("estado_evento") val eventStatus: String? = null,
    @SerialName("modalidade_id") val modalityId: Int? = null,
    @SerialName("criador_id") val creatorId: Int? = null
)

@Serializable
private data class MyEventsRegistrationRow(
    val id: Int,
    @SerialName("evento_id") val eventId: Int? = null,
    @SerialName("user_id") val userId: Int? = null,
    @SerialName("estado_inscricao") val status: String? = null,
    @SerialName("tipo_inscricao") val registrationType: String? = null
)

@Serializable
private data class MyEventsEventTeamRow(
    val id: Long,
    @SerialName("evento_id") val eventId: Int,
    @SerialName("equipa_id") val teamId: Int,
    @SerialName("estado") val status: String? = null
)

@Serializable
private data class MyEventsModalityRow(
    val id: Int,
    @SerialName("nome") val name: String
)

@Serializable
private data class MyEventsGameRow(
    val id: Int,
    @SerialName("evento_id") val eventId: Int? = null,
    @SerialName("estado_jogo") val status: String? = null
)
