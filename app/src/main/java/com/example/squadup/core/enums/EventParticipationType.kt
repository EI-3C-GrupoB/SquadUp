package com.example.squadup.core.enums

enum class EventParticipationType(
    val dbValue: String,
    val label: String
) {
    INDIVIDUAL(
        dbValue = "individual",
        label = "Individual"
    ),
    TEAM(
        dbValue = "equipa",
        label = "Por equipas"
    ),
    INDIVIDUAL_AND_TEAM(
        dbValue = "individual_e_equipa",
        label = "Individual e equipas"
    );

    companion object {
        fun fromDbValue(value: String?): EventParticipationType {
            return entries.firstOrNull { it.dbValue == value }
                ?: INDIVIDUAL
        }
    }
}