package com.example.squadup.features.payment

import com.example.squadup.core.SupabaseClientProvider
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.UUID

class PaymentRepository(
    private val supabaseClient: SupabaseClient = SupabaseClientProvider.client
) {
    suspend fun loadPaymentDetails(
        inscricaoId: Int,
        eventId: Int
    ): Result<PaymentUiState> {
        return try {
            val userId = getCurrentUserId()
                ?: throw Exception("Utilizador não encontrado.")

            val event = supabaseClient.from("evento")
                .select { filter { eq("id", eventId) } }
                .decodeSingle<PaymentEventRow>()

            val payment = supabaseClient.from("pagamento")
                .select {
                    filter {
                        eq("evento_id", eventId)
                        eq("user_id", userId)
                    }
                }
                .decodeList<PaymentRow>()
                .firstOrNull()

            val start = event.startDate.toLocalDateTimeOrNull()
            val dateLabel = start?.format(
                DateTimeFormatter.ofPattern("dd MMM yyyy • HH:mm", Locale("pt", "PT"))
            ).orEmpty()

            Result.success(
                PaymentUiState(
                    isLoading = false,
                    eventTitle = event.title,
                    eventDate = dateLabel,
                    eventVenue = event.address.orEmpty(),
                    price = event.price ?: 0.0,
                    currency = event.currency ?: "EUR",
                    inscricaoId = inscricaoId,
                    eventId = eventId,
                    paymentId = payment?.id
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun confirmPayment(
        paymentId: Int?,
        eventId: Int
    ): Result<Int> {
        return try {
            val userId = getCurrentUserId()
                ?: throw Exception("Utilizador não encontrado.")

            if (paymentId != null) {
                supabaseClient.from("pagamento")
                    .update(PaymentStatusUpdateRow(status = "pago")) {
                        filter { eq("id", paymentId) }
                    }
            } else {
                supabaseClient.from("pagamento")
                    .update(PaymentStatusUpdateRow(status = "pago")) {
                        filter {
                            eq("evento_id", eventId)
                            eq("user_id", userId)
                        }
                    }
            }

            val ticket = supabaseClient.from("bilhete")
                .insert(PaymentTicketInsertRow(
                    userId = userId,
                    eventId = eventId,
                    codigoQr = UUID.randomUUID().toString()
                )) {
                    select()
                }
                .decodeSingle<PaymentTicketCreatedRow>()

            notifyOrganizer(eventId = eventId, payerUserId = userId)

            Result.success(ticket.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun notifyOrganizer(eventId: Int, payerUserId: Int) {
        runCatching {
            val event = supabaseClient.from("evento")
                .select { filter { eq("id", eventId) } }
                .decodeSingle<PaymentEventOrganizerRow>()

            val organizerId = event.creatorId ?: return

            val playerName = supabaseClient.from("utilizador")
                .select { filter { eq("id", payerUserId) } }
                .decodeSingle<PaymentUserRow>()
                .name ?: "Um jogador"

            supabaseClient.from("notificacao").insert(
                PaymentNotificationInsertRow(
                    userId = organizerId,
                    title = "Pagamento recebido",
                    description = "$playerName pagou a inscrição no evento \"${event.title}\".",
                    type = "informacao",
                    referenceId = eventId,
                    referenceType = "evento"
                )
            )
        }
    }

    private suspend fun getCurrentUserId(): Int? {
        val authUserId = supabaseClient.auth.currentUserOrNull()?.id ?: return null
        return runCatching {
            supabaseClient.from("utilizador")
                .select { filter { eq("auth_user_id", authUserId) } }
                .decodeSingle<PaymentUserRow>()
                .id
        }.getOrNull()
    }

    private fun String?.toLocalDateTimeOrNull(): LocalDateTime? {
        if (isNullOrBlank()) return null
        return runCatching { LocalDateTime.parse(replace(" ", "T").take(19)) }.getOrNull()
    }
}
