package com.example.squadup.features.events.manageevent.qrscanner

import com.example.squadup.core.SupabaseClientProvider
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDateTime

class QrScannerRepository(
    private val supabaseClient: SupabaseClient = SupabaseClientProvider.client
) {
    suspend fun verifyTicket(codigoQr: String, eventId: Int): QrVerificationResult {
        return try {
            val tickets = supabaseClient.from("bilhete")
                .select { filter { eq("codigo_qr", codigoQr) } }
                .decodeList<QrTicketRow>()

            val ticket = tickets.firstOrNull() ?: return QrVerificationResult.NotFound

            val event = supabaseClient.from("evento")
                .select { filter { eq("id", ticket.eventId) } }
                .decodeSingle<QrEventRow>()

            if (ticket.eventId != eventId) {
                return QrVerificationResult.WrongEvent(event.title)
            }

            val cutoff = (event.endDate ?: event.startDate).toLocalDateTimeOrNull()
            if (cutoff != null && cutoff.isBefore(LocalDateTime.now())) {
                return QrVerificationResult.Expired(event.title)
            }

            val playerName = runCatching {
                supabaseClient.from("utilizador")
                    .select { filter { eq("id", ticket.userId) } }
                    .decodeSingle<QrUserRow>()
                    .name
            }.getOrDefault("Jogador desconhecido")

            QrVerificationResult.Valid(playerName, event.title, ticket.id)
        } catch (e: Exception) {
            QrVerificationResult.Error(e.message ?: "Erro ao verificar bilhete")
        }
    }

    private fun String?.toLocalDateTimeOrNull(): LocalDateTime? {
        if (isNullOrBlank()) return null
        return runCatching { LocalDateTime.parse(replace(" ", "T").take(19)) }.getOrNull()
    }
}

@Serializable
private data class QrTicketRow(
    val id: Int,
    @SerialName("evento_id") val eventId: Int,
    @SerialName("user_id") val userId: Int,
    @SerialName("estado") val status: String? = null
)

@Serializable
private data class QrEventRow(
    val id: Int,
    @SerialName("titulo") val title: String,
    @SerialName("data_inicio") val startDate: String? = null,
    @SerialName("data_fim") val endDate: String? = null
)

@Serializable
private data class QrUserRow(
    @SerialName("nome") val name: String
)
