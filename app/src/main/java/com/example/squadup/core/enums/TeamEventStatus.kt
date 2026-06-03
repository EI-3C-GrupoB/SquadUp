package com.example.squadup.core.enums

enum class TeamEventStatus {
    PENDING,        // Inscrita, aguarda validação do organizador
    INCOMPLETE,     // Não tem jogadores suficientes para participar
    CONFIRMED,      // Validada e pronta a jogar
    WITHDRAWN,      // Retirou-se do evento
    ELIMINATED,     // Eliminada (mata-mata / fase de grupos)
    DISQUALIFIED    // Desqualificada pelo organizador
}
