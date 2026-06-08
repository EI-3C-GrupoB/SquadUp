package com.example.squadup.features.events.moredetails

import com.example.squadup.core.SupabaseClientProvider
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.realtime.PostgresAction
import io.github.jan.supabase.realtime.channel
import io.github.jan.supabase.realtime.postgresChangeFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onStart
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class MoreDetailsRepository(
    private val supabaseClient: SupabaseClient = SupabaseClientProvider.client
) {
    fun observeEventDetails(eventId: String): Flow<MoreDetailsUiState> = flow {
        val parsedEventId = eventId.toIntOrNull()

        if (parsedEventId == null) {
            emit(
                MoreDetailsUiState(
                    isLoading = false,
                    errorMessage = "Evento inválido."
                )
            )
            return@flow
        }

        emitAll(
            observeEventTables(parsedEventId)
                .map {
                    getEventDetails(parsedEventId).getOrElse { exception ->
                        MoreDetailsUiState(
                            isLoading = false,
                            errorMessage = exception.message ?: "Não foi possível carregar o evento."
                        )
                    }
                }
        )
    }

    suspend fun getEventDetails(eventId: Int): Result<MoreDetailsUiState> {
        return try {
            val event = supabaseClient
                .from("evento")
                .select {
                    filter {
                        eq("id", eventId)
                    }
                }
                .decodeSingle<MoreDetailsEventRow>()

            val modality = event.modalityId?.let { modalityId ->
                getModalityName(modalityId)
            }.orEmpty()

            val format = event.formatId?.let { formatId ->
                getFormatName(formatId)
            }.orEmpty()

            val acceptedRegistrations = getAcceptedRegistrationsCount(eventId)

            Result.success(
                event.toUiState(
                    modalityName = modality,
                    formatName = format,
                    acceptedRegistrations = acceptedRegistrations
                )
            )
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }

    private fun observeEventTables(eventId: Int): Flow<Unit> = flow {
        val channelSuffix = "${eventId}_${System.currentTimeMillis()}"

        val eventChannel = supabaseClient.channel("more_details_evento_$channelSuffix")
        val registrationsChannel = supabaseClient.channel("more_details_inscricao_$channelSuffix")

        val eventChanges = eventChannel
            .postgresChangeFlow<PostgresAction>(schema = "public") {
                table = "evento"
            }
            .map { Unit }

        val registrationChanges = registrationsChannel
            .postgresChangeFlow<PostgresAction>(schema = "public") {
                table = "inscricao"
            }
            .map { Unit }

        eventChannel.subscribe()
        registrationsChannel.subscribe()

        emitAll(
            merge(
                eventChanges,
                registrationChanges
            ).onStart {
                emit(Unit)
            }
        )
    }

    private suspend fun getModalityName(modalityId: Int): String {
        return runCatching {
            supabaseClient
                .from("modalidade")
                .select {
                    filter {
                        eq("id", modalityId)
                    }
                }
                .decodeSingle<MoreDetailsModalityRow>()
                .name
        }.getOrDefault("")
    }

    private suspend fun getFormatName(formatId: Int): String {
        return runCatching {
            supabaseClient
                .from("formato")
                .select {
                    filter {
                        eq("id", formatId)
                    }
                }
                .decodeSingle<MoreDetailsFormatRow>()
                .name
        }.getOrDefault("")
    }

    private suspend fun getAcceptedRegistrationsCount(eventId: Int): Int {
        return runCatching {
            supabaseClient
                .from("inscricao")
                .select {
                    filter {
                        eq("evento_id", eventId)
                        eq("estado_inscricao", "aceite")
                    }
                }
                .decodeList<MoreDetailsRegistrationRow>()
                .size
        }.getOrDefault(0)
    }

    private fun MoreDetailsEventRow.toUiState(
        modalityName: String,
        formatName: String,
        acceptedRegistrations: Int
    ): MoreDetailsUiState {
        val start = startDate.toLocalDateTimeOrNull()
        val end = endDate.toLocalDateTimeOrNull()

        val registrationStart = registrationStartDate.toLocalDateTimeOrNull()
        val registrationEnd = registrationEndDate.toLocalDateTimeOrNull()

        val maxSlots = maxTeams ?: participationLimit ?: 0
        val spotsLeft = if (maxSlots > 0) {
            (maxSlots - acceptedRegistrations).coerceAtLeast(0)
        } else {
            0
        }

        val hasTeamRequirement = (maxTeams ?: 0) > 0

        return MoreDetailsUiState(
            isLoading = false,
            errorMessage = null,

            eventId = id,
            title = title,
            description = description.orEmpty(),
            imageUrl = imageUrl,

            date = start.toDateLabel(),
            time = toTimeRangeLabel(start, end),
            registrationPeriod = toRegistrationPeriodLabel(registrationStart, registrationEnd),

            entryType = eventType.toEntryType(),
            eventStatus = eventStatus.toEventStatusLabel(),
            modalityName = modalityName.ifBlank { "Modalidade não definida" },
            formatName = formatName.ifBlank { "Formato não definido" },

            teamRequirementTitle = if (hasTeamRequirement) {
                "Equipa necessária"
            } else {
                "Inscrição individual"
            },
            teamRequirementDescription = if (hasTeamRequirement) {
                "Este evento aceita inscrições por equipa."
            } else {
                "Este evento aceita inscrições individuais."
            },

            priceLabel = price.toMoneyLabel(currency),
            entryFeeLabel = entryFee.toMoneyLabel(currency),

            registeredTeams = acceptedRegistrations,
            maxTeams = maxSlots,
            spotsLeft = spotsLeft,

            rules = rules
                ?.lines()
                ?.map { it.trim() }
                ?.filter { it.isNotBlank() }
                .orEmpty(),

            venueName = address.orEmpty(),
            latitude = latitude,
            longitude = longitude,

            creatorId = creatorId,
            participationType = participationType ?: "individual"
        )
    }

    private fun String?.toEntryType(): String {
        return when (this?.lowercase()) {
            "torneio" -> "TORNEIO"
            "liga" -> "LIGA"
            "jogo_amigavel" -> "JOGO ABERTO"
            "treino" -> "TREINO"
            else -> "EVENTO"
        }
    }

    private fun String?.toEventStatusLabel(): String {
        return when (this?.lowercase()) {
            "rascunho" -> "Rascunho"
            "publicado" -> "Publicado"
            "ativo" -> "Activo"
            "em_curso" -> "Em curso"
            "terminado" -> "Terminado"
            "cancelado" -> "Cancelado"
            else -> "Estado não definido"
        }
    }

    private fun Double?.toMoneyLabel(currency: String?): String {
        val value = this ?: 0.0

        if (value == 0.0) return "Grátis"

        val symbol = when (currency?.uppercase()) {
            "EUR", null -> "€"
            "USD" -> "$"
            "GBP" -> "£"
            else -> "${currency.uppercase()} "
        }

        return if (value % 1.0 == 0.0) {
            "$symbol${value.toInt()}"
        } else {
            "$symbol${"%.2f".format(Locale.US, value)}"
        }
    }

    private fun LocalDateTime?.toDateLabel(): String {
        return this
            ?.format(DateTimeFormatter.ofPattern("dd MMM yyyy", Locale("pt", "PT")))
            .orEmpty()
    }

    private fun toTimeRangeLabel(
        start: LocalDateTime?,
        end: LocalDateTime?
    ): String {
        val startLabel = start?.format(DateTimeFormatter.ofPattern("HH:mm")).orEmpty()
        val endLabel = end?.format(DateTimeFormatter.ofPattern("HH:mm")).orEmpty()

        return when {
            startLabel.isNotBlank() && endLabel.isNotBlank() -> "$startLabel - $endLabel"
            startLabel.isNotBlank() -> startLabel
            else -> ""
        }
    }

    private fun toRegistrationPeriodLabel(
        start: LocalDateTime?,
        end: LocalDateTime?
    ): String {
        val startLabel = start?.format(DateTimeFormatter.ofPattern("dd MMM", Locale("pt", "PT"))).orEmpty()
        val endLabel = end?.format(DateTimeFormatter.ofPattern("dd MMM", Locale("pt", "PT"))).orEmpty()

        return when {
            startLabel.isNotBlank() && endLabel.isNotBlank() -> "$startLabel - $endLabel"
            startLabel.isNotBlank() -> "Desde $startLabel"
            endLabel.isNotBlank() -> "Até $endLabel"
            else -> "Sem período definido"
        }
    }

    private fun String?.toLocalDateTimeOrNull(): LocalDateTime? {
        if (isNullOrBlank()) return null

        val normalized = replace(" ", "T").take(19)

        return runCatching {
            LocalDateTime.parse(normalized)
        }.getOrNull()
    }
}
