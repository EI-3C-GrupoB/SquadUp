package com.example.squadup.core.enums

enum class RegistrationType(
    val dbValue: String,
    val label: String
) {
    TEAM_GLOBAL(
        dbValue = "equipa_global",
        label = "Membro de equipa"
    ),
    EVENT_INDIVIDUAL(
        dbValue = "evento_individual",
        label = "Participação individual"
    ),
    EVENT_TEAM(
        dbValue = "evento_equipa",
        label = "Participação por equipa"
    ),
    EVENT_TEAM_REQUEST(
        dbValue = "pedido_evento_equipa",
        label = "Pedido para equipa do evento"
    );

    companion object {
        fun fromDbValue(value: String?): RegistrationType {
            return entries.firstOrNull { it.dbValue == value }
                ?: TEAM_GLOBAL
        }
    }
}