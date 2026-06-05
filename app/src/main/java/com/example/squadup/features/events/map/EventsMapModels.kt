package com.example.squadup.features.events.map

import com.example.squadup.core.enums.SportType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class EventsMapPinStatus {
    AVAILABLE,
    FULL,
    PRIVATE,
    CANCELLED,
    FINISHED
}

data class EventsMapPin(
    val eventId: String,
    val title: String,
    val venue: String,
    val latitude: Double,
    val longitude: Double,
    val sportType: SportType,
    val status: EventsMapPinStatus,
    val registeredCount: Int,
    val totalSpots: Int,
    val startsAt: String
)

data class EventsMapCameraBounds(
    val minLatitude: Double,
    val maxLatitude: Double,
    val minLongitude: Double,
    val maxLongitude: Double
)

@Serializable
data class EventsMapEventRow(
    val id: Int,
    @SerialName("titulo")
    val title: String,
    @SerialName("morada")
    val address: String? = null,
    @SerialName("latitude")
    val latitude: Double? = null,
    @SerialName("longitude")
    val longitude: Double? = null,
    @SerialName("is_private")
    val isPrivate: Boolean? = null,
    @SerialName("estado_evento")
    val eventStatus: String? = null,
    @SerialName("limite_participacoes")
    val participationLimit: Int? = null,
    @SerialName("max_equipas")
    val maxTeams: Int? = null,
    @SerialName("data_inicio")
    val startsAt: String? = null,
    @SerialName("modalidade_id")
    val modalityId: Int? = null
)

@Serializable
data class EventsMapRegistrationRow(
    val id: Int,
    @SerialName("evento_id")
    val eventId: Int? = null
)

@Serializable
data class EventsMapModalityRow(
    val id: Int,
    @SerialName("nome")
    val name: String
)
