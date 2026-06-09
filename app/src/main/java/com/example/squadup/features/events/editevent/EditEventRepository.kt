package com.example.squadup.features.events.editevent

import com.example.squadup.core.SupabaseClientProvider
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

class EditEventRepository(
    private val supabaseClient: SupabaseClient = SupabaseClientProvider.client
) {
    suspend fun loadEvent(eventId: String): Result<EditEventUiState> {
        return try {
            val id = eventId.toIntOrNull() ?: throw Exception("Evento inválido.")
            val row = supabaseClient.from("evento")
                .select { filter { eq("id", id) } }
                .decodeSingle<EditEventRow>()

            val startParts = row.startDate?.split("T")
            val endParts = row.endDate?.split("T")

            Result.success(
                EditEventUiState(
                    isLoading = false,
                    eventId = eventId,
                    title = row.title,
                    description = row.description ?: "",
                    address = row.address ?: "",
                    startDate = startParts?.getOrNull(0) ?: "",
                    startTime = startParts?.getOrNull(1)?.take(5) ?: "",
                    endDate = endParts?.getOrNull(0) ?: "",
                    endTime = endParts?.getOrNull(1)?.take(5) ?: "",
                    entryFee = row.entryFee?.toString() ?: "",
                    maxTeams = row.maxTeams?.toString() ?: "",
                    participationLimit = row.participationLimit?.toString() ?: "",
                    isPublic = !(row.isPrivate ?: false)
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun saveEvent(state: EditEventUiState): Result<Unit> {
        return try {
            val id = state.eventId.toIntOrNull() ?: throw Exception("Evento inválido.")

            val startDateTime = if (state.startDate.isNotBlank() && state.startTime.isNotBlank())
                "${state.startDate}T${state.startTime}:00" else null
            val endDateTime = if (state.endDate.isNotBlank() && state.endTime.isNotBlank())
                "${state.endDate}T${state.endTime}:00" else null

            supabaseClient.from("evento").update(
                EditEventUpdateRow(
                    title = state.title.trim(),
                    description = state.description.takeIf { it.isNotBlank() },
                    address = state.address.takeIf { it.isNotBlank() },
                    startDate = startDateTime,
                    endDate = endDateTime,
                    entryFee = state.entryFee.toDoubleOrNull() ?: 0.0,
                    maxTeams = state.maxTeams.toIntOrNull(),
                    participationLimit = state.participationLimit.toIntOrNull(),
                    isPrivate = !state.isPublic
                )
            ) { filter { eq("id", id) } }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

@Serializable
private data class EditEventRow(
    val id: Int,
    @SerialName("titulo") val title: String,
    @SerialName("descricao") val description: String? = null,
    @SerialName("morada") val address: String? = null,
    @SerialName("data_inicio") val startDate: String? = null,
    @SerialName("data_fim") val endDate: String? = null,
    @SerialName("taxa_inscricao") val entryFee: Double? = null,
    @SerialName("max_equipas") val maxTeams: Int? = null,
    @SerialName("limite_participacoes") val participationLimit: Int? = null,
    @SerialName("is_private") val isPrivate: Boolean? = null
)

@Serializable
private data class EditEventUpdateRow(
    @SerialName("titulo") val title: String,
    @SerialName("descricao") val description: String? = null,
    @SerialName("morada") val address: String? = null,
    @SerialName("data_inicio") val startDate: String? = null,
    @SerialName("data_fim") val endDate: String? = null,
    @SerialName("taxa_inscricao") val entryFee: Double = 0.0,
    @SerialName("max_equipas") val maxTeams: Int? = null,
    @SerialName("limite_participacoes") val participationLimit: Int? = null,
    @SerialName("is_private") val isPrivate: Boolean = false
)
