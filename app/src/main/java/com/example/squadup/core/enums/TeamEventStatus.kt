package com.example.squadup.core.enums

enum class TeamEventStatus(
    val dbValue: String,
    val label: String
) {
    PENDING(
        dbValue = "pendente",
        label = "Pendente"
    ),
    INCOMPLETE(
        dbValue = "incompleta",
        label = "Incompleta"
    ),
    CONFIRMED(
        dbValue = "confirmada",
        label = "Confirmada"
    ),
    WITHDRAWN(
        dbValue = "cancelada",
        label = "Cancelada"
    ),
    ELIMINATED(
        dbValue = "eliminada",
        label = "Eliminada"
    ),
    DISQUALIFIED(
        dbValue = "desqualificada",
        label = "Desqualificada"
    );

    companion object {
        fun fromDbValue(value: String?): TeamEventStatus {
            return entries.firstOrNull { it.dbValue == value }
                ?: PENDING
        }
    }
}
