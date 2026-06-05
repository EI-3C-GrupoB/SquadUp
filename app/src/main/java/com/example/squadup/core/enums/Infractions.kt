package com.example.squadup.core.enums

import androidx.annotation.StringRes
import com.example.squadup.R

// ─── Futebol & Futsal ─────────────────────────────────────────────────────────

enum class FootballInfraction(val code: Int, @StringRes val labelRes: Int) {
    YELLOW_CARD(1, R.string.infraction_yellow_card),
    RED_CARD(2, R.string.infraction_red_card),
    SECOND_YELLOW(3, R.string.infraction_second_yellow);   // implica vermelho automático

    companion object {
        fun fromCode(code: Int) = entries.firstOrNull { it.code == code }
    }
}

// ─── Basquete ─────────────────────────────────────────────────────────────────

enum class BasketballInfraction(val code: Int, @StringRes val labelRes: Int) {
    PERSONAL_FOUL(1, R.string.infraction_personal_foul),
    TECHNICAL_FOUL(2, R.string.infraction_technical_foul),
    FLAGRANT_FOUL(3, R.string.infraction_flagrant_foul);

    companion object {
        fun fromCode(code: Int) = entries.firstOrNull { it.code == code }
    }
}

// ─── Voleibol ─────────────────────────────────────────────────────────────────

enum class VolleyballInfraction(val code: Int, @StringRes val labelRes: Int) {
    WARNING(1, R.string.infraction_warning),
    PENALTY(2, R.string.infraction_penalty),
    EXPULSION(3, R.string.infraction_expulsion);   // jogador sai do jogo

    companion object {
        fun fromCode(code: Int) = entries.firstOrNull { it.code == code }
    }
}

// ─── Padel ────────────────────────────────────────────────────────────────────

enum class PaddleInfraction(val code: Int, @StringRes val labelRes: Int) {
    WARNING(1, R.string.infraction_warning),
    POINT_PENALTY(2, R.string.infraction_point_penalty),
    GAME_PENALTY(3, R.string.infraction_game_penalty);  // perde um jogo inteiro

    companion object {
        fun fromCode(code: Int) = entries.firstOrNull { it.code == code }
    }
}

// ─── Helper: SportType → label da infração por código ─────────────────────────

fun SportType.infractionLabelRes(code: Int): Int? = when (this) {
    SportType.SOCCER,
    SportType.FUTSAL     -> FootballInfraction.fromCode(code)?.labelRes
    SportType.BASKETBALL -> BasketballInfraction.fromCode(code)?.labelRes
    SportType.VOLLEYBALL -> VolleyballInfraction.fromCode(code)?.labelRes
    SportType.PADDLE     -> PaddleInfraction.fromCode(code)?.labelRes
}
