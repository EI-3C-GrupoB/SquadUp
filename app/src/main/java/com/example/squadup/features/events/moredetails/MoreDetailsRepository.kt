package com.example.squadup.features.events.moredetails

import android.util.Log
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
    fun observeEventDetails(
        eventId: String,
        currentUserId: Int?
    ): Flow<MoreDetailsUiState> = flow {
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
                    getEventDetails(
                        eventId = parsedEventId,
                        currentUserId = currentUserId
                    ).getOrElse { exception ->
                        MoreDetailsUiState(
                            isLoading = false,
                            errorMessage = exception.message ?: "Não foi possível carregar o evento."
                        )
                    }
                }
        )
    }

    suspend fun getEventDetails(
        eventId: Int,
        currentUserId: Int?
    ): Result<MoreDetailsUiState> {
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

            val currentUserRegistration = getCurrentUserEventRegistration(
                eventId = eventId,
                currentUserId = currentUserId
            )

            Result.success(
                event.toUiState(
                    modalityName = modality,
                    formatName = format,
                    acceptedRegistrations = acceptedRegistrations,
                    currentUserRegistration = currentUserRegistration
                )
            )
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }

    private suspend fun getCurrentUserEventRegistration(
        eventId: Int,
        currentUserId: Int?
    ): MoreDetailsRegistrationRow? {
        if (currentUserId == null) {
            return null
        }

        val registrations = supabaseClient
            .from("inscricao")
            .select {
                filter {
                    eq("evento_id", eventId)
                    eq("user_id", currentUserId)
                }
            }
            .decodeList<MoreDetailsRegistrationRow>()

        val activeRegistration = registrations.firstOrNull { registration ->
            registration.registrationStatus.isActiveRegistrationStatus() &&
                    (
                            registration.registrationType == "evento_individual" ||
                                    registration.registrationType == "evento_equipa" ||
                                    registration.registrationType == "pedido_evento_equipa"
                            )
        }

        if (activeRegistration != null) {
            return activeRegistration
        }

        return getCurrentUserEventTeamRegistration(
            eventId = eventId,
            currentUserId = currentUserId
        )
    }

    private suspend fun getCurrentUserEventTeamRegistration(
        eventId: Int,
        currentUserId: Int
    ): MoreDetailsRegistrationRow? {
        val eventTeam = supabaseClient
            .from("evento_equipa")
            .select {
                filter {
                    eq("evento_id", eventId)
                    eq("capitao_user_id", currentUserId)
                }
            }
            .decodeList<MoreDetailsEventTeamRow>()
            .firstOrNull { it.status.isActiveEventTeamStatus() }
            ?: return null

        val status = when (eventTeam.status) {
            "confirmada" -> "aceite"
            "recusada", "cancelada" -> "recusada"
            else -> "pendente"
        }

        return MoreDetailsRegistrationRow(
            id = eventTeam.id.toInt(),
            eventId = eventId,
            teamId = eventTeam.teamId,
            userId = currentUserId,
            registrationStatus = status,
            registrationType = if (status == "aceite") "evento_equipa" else "pedido_evento_equipa",
            role = "capitao",
            isCaptain = true
        )
    }
    private fun observeEventTables(eventId: Int): Flow<Unit> = flow {
        val channelSuffix = "${eventId}_${System.currentTimeMillis()}"

        val eventChannel = supabaseClient.channel("more_details_evento_$channelSuffix")
        val registrationsChannel = supabaseClient.channel("more_details_inscricao_$channelSuffix")
        val eventTeamsChannel = supabaseClient.channel("more_details_evento_equipa_$channelSuffix")

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

        val eventTeamChanges = eventTeamsChannel
            .postgresChangeFlow<PostgresAction>(schema = "public") {
                table = "evento_equipa"
            }
            .map { Unit }

        eventChannel.subscribe()
        registrationsChannel.subscribe()
        eventTeamsChannel.subscribe()

        emitAll(
            merge(
                eventChanges,
                registrationChanges,
                eventTeamChanges
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
        acceptedRegistrations: Int,
        currentUserRegistration: MoreDetailsRegistrationRow?
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
            participationType = participationType ?: "individual",

            userEventRegistrationStatus = currentUserRegistration?.registrationStatus,
            userEventRegistrationType = currentUserRegistration?.registrationType
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

    suspend fun joinEventIndividually(
        eventId: Int,
        currentUserId: Int?
    ): Result<Unit> {
        return try {
            if (currentUserId == null) {
                throw Exception("Utilizador não encontrado.")
            }

            val existingRegistration = supabaseClient
                .from("inscricao")
                .select {
                    filter {
                        eq("evento_id", eventId)
                        eq("user_id", currentUserId)
                    }
                }
                .decodeList<MoreDetailsRegistrationRow>()

            if (existingRegistration.any { it.isActiveEventParticipation() }) {
                throw Exception("Já tens uma inscrição ou pedido activo neste evento.")
            }

            supabaseClient
                .from("inscricao")
                .insert(
                    MoreDetailsIndividualRegistrationInsertRow(
                        eventId = eventId,
                        userId = currentUserId,
                        teamId = null,
                        registrationStatus = "pendente",
                        registrationType = "evento_individual",
                        isCaptain = false,
                        role = "membro"
                    )
                )

            notifyEventCreatorAboutIndividualRequest(
                eventId = eventId,
                currentUserId = currentUserId
            )

            Result.success(Unit)
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }

    suspend fun getAvailableTeamsForEvent(
        eventId: Int,
        currentUserId: Int?
    ): Result<List<MoreDetailsAvailableTeam>> {
        return try {
            if (currentUserId == null) {
                throw Exception("Utilizador não encontrado.")
            }

            val teams = supabaseClient
                .from("equipa")
                .select()
                .decodeList<MoreDetailsTeamRow>()

            val registrations = supabaseClient
                .from("inscricao")
                .select()
                .decodeList<MoreDetailsRegistrationRow>()

            val eventTeams = supabaseClient
                .from("evento_equipa")
                .select {
                    filter {
                        eq("evento_id", eventId)
                    }
                }
                .decodeList<MoreDetailsEventTeamRow>()

            val teamsWithActiveEventRegistration = registrations
                .filter { registration ->
                    registration.eventId == eventId &&
                            registration.teamId != null &&
                            registration.registrationType in setOf("pedido_evento_equipa", "evento_equipa") &&
                            registration.registrationStatus.isActiveRegistrationStatus()
                }
                .mapNotNull { it.teamId }
                .toSet()

            val unavailableTeamIds = eventTeams
                .filter { eventTeam ->
                    eventTeam.status.isActiveEventTeamStatus() &&
                            eventTeam.teamId in teamsWithActiveEventRegistration
                }
                .map { it.teamId }
                .toSet() + teamsWithActiveEventRegistration

            val globalRegistrationsByTeam = registrations
                .filter { registration ->
                    registration.eventId == null &&
                            registration.teamId != null &&
                            registration.userId == currentUserId &&
                            registration.registrationStatus.isActiveRegistrationStatus()
                }
                .associateBy { it.teamId }

            val availableTeams = teams
                .filter { team ->
                    val currentUserTeamRegistration = globalRegistrationsByTeam[team.id]
                    val canRegisterTeam = team.ownerId == currentUserId ||
                            currentUserTeamRegistration?.role == "capitao" ||
                            currentUserTeamRegistration?.isCaptain == true

                    canRegisterTeam && team.id !in unavailableTeamIds
                }
                .sortedBy { it.name }
                .map { team ->
                    MoreDetailsAvailableTeam(
                        id = team.id,
                        name = team.name
                    )
                }

            Result.success(availableTeams)
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }

    suspend fun joinEventWithTeam(
        eventId: Int,
        currentUserId: Int?,
        teamId: Int
    ): Result<Unit> {
        return try {
            if (currentUserId == null) {
                throw Exception("Utilizador não encontrado.")
            }

            val currentUserRegistrations = supabaseClient
                .from("inscricao")
                .select {
                    filter {
                        eq("evento_id", eventId)
                        eq("user_id", currentUserId)
                    }
                }
                .decodeList<MoreDetailsRegistrationRow>()

            if (currentUserRegistrations.any { it.isActiveEventParticipation() }) {
                throw Exception("Já tens uma inscrição ou pedido activo neste evento.")
            }

            val availableTeams = getAvailableTeamsForEvent(
                eventId = eventId,
                currentUserId = currentUserId
            ).getOrThrow()

            if (availableTeams.none { it.id == teamId }) {
                throw Exception("Esta equipa já está inscrita ou não pode ser usada neste evento.")
            }

            val existingEventTeam = supabaseClient
                .from("evento_equipa")
                .select {
                    filter {
                        eq("evento_id", eventId)
                        eq("equipa_id", teamId)
                        eq("capitao_user_id", currentUserId)
                    }
                }
                .decodeList<MoreDetailsEventTeamRow>()
                .firstOrNull { it.status.isActiveEventTeamStatus() }

            val createdEventTeam = existingEventTeam ?: supabaseClient
                .from("evento_equipa")
                .insert(
                    MoreDetailsEventTeamInsertRow(
                        eventId = eventId,
                        teamId = teamId,
                        status = "pendente",
                        captainUserId = currentUserId
                    )
                ) {
                    select()
                }
                .decodeSingle<MoreDetailsEventTeamRow>()

            try {
                supabaseClient
                    .from("inscricao")
                    .insert(
                        MoreDetailsIndividualRegistrationInsertRow(
                            eventId = eventId,
                            userId = currentUserId,
                            teamId = teamId,
                            registrationStatus = "pendente",
                            registrationType = "pedido_evento_equipa",
                            isCaptain = true,
                            role = "capitao"
                        )
                    )
            } catch (exception: Exception) {
                if (exception.isDuplicateTeamUserRegistration()) {
                    notifyEventCreatorAboutTeamRequest(
                        eventId = eventId,
                        currentUserId = currentUserId,
                        teamId = teamId
                    )

                    return Result.success(Unit)
                }

                if (existingEventTeam == null) {
                    deleteEventTeamRegistration(createdEventTeam.id)
                }

                throw exception
            }

            notifyEventCreatorAboutTeamRequest(
                eventId = eventId,
                currentUserId = currentUserId,
                teamId = teamId
            )

            Result.success(Unit)
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }

    private suspend fun notifyEventCreatorAboutIndividualRequest(
        eventId: Int,
        currentUserId: Int
    ) {
        runCatching {
            val event = supabaseClient
                .from("evento")
                .select {
                    filter {
                        eq("id", eventId)
                    }
                }
                .decodeSingle<MoreDetailsEventRow>()

            val creatorId = event.creatorId ?: return

            if (creatorId == currentUserId) {
                return
            }

            val user = getUserForNotification(currentUserId)

            supabaseClient
                .from("notificacao")
                .insert(
                    MoreDetailsEventNotificationInsertRow(
                        userId = creatorId,
                        title = "Novo pedido de participação",
                        description = "${user.name} pediu para participar no evento ${event.title}.",
                        type = "evento",
                        referenceId = event.id,
                        referenceType = "evento"
                    )
                )
        }.onFailure { exception ->
            Log.e(
                "MoreDetailsRepository",
                "Erro ao criar notificação de pedido individual: ${exception.message}",
                exception
            )
        }
    }

    private suspend fun notifyEventCreatorAboutTeamRequest(
        eventId: Int,
        currentUserId: Int,
        teamId: Int
    ) {
        runCatching {
            val event = supabaseClient
                .from("evento")
                .select {
                    filter {
                        eq("id", eventId)
                    }
                }
                .decodeSingle<MoreDetailsEventRow>()

            val creatorId = event.creatorId ?: return

            if (creatorId == currentUserId) {
                return
            }

            val team = supabaseClient
                .from("equipa")
                .select {
                    filter {
                        eq("id", teamId)
                    }
                }
                .decodeSingle<MoreDetailsTeamRow>()

            supabaseClient
                .from("notificacao")
                .insert(
                    MoreDetailsEventNotificationInsertRow(
                        userId = creatorId,
                        title = "Novo pedido de equipa",
                        description = "A equipa ${team.name} pediu para participar no evento ${event.title}.",
                        type = "evento",
                        referenceId = event.id,
                        referenceType = "evento"
                    )
                )
        }.onFailure { exception ->
            Log.e(
                "MoreDetailsRepository",
                "Erro ao criar notificação de pedido com equipa: ${exception.message}",
                exception
            )
        }
    }

    private suspend fun getUserForNotification(userId: Int): MoreDetailsUserRow {
        return supabaseClient
            .from("utilizador")
            .select {
                filter {
                    eq("id", userId)
                }
            }
            .decodeSingle<MoreDetailsUserRow>()
    }
    private fun MoreDetailsRegistrationRow.isActiveEventParticipation(): Boolean {
        return registrationStatus.isActiveRegistrationStatus() &&
                registrationType in setOf(
                    "evento_individual",
                    "evento_equipa",
                    "pedido_evento_equipa"
                )
    }

    private suspend fun deleteEventTeamRegistration(eventTeamRegistrationId: Long) {
        runCatching {
            supabaseClient
                .from("evento_equipa")
                .delete {
                    filter {
                        eq("id", eventTeamRegistrationId)
                    }
                }
        }
    }

    private fun String?.isActiveRegistrationStatus(): Boolean {
        return this != "recusada" && this != "recusado" && this != "banido"
    }

    private fun String?.isActiveEventTeamStatus(): Boolean {
        return this != "recusada" &&
                this != "recusado" &&
                this != "cancelada" &&
                this != "cancelado"
    }

    private fun Exception.isDuplicateTeamUserRegistration(): Boolean {
        val message = message.orEmpty()
        return "duplicate key value" in message &&
                "unique_team_user_registration" in message
    }
}
