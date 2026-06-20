package com.ashamsi.maxlift.domain.model

/**
 * Supported 1RM calculation formulas.
 *
 * @property id Unique identifier for the formula.
 */
enum class FormulaType(val id: String) {
    Brzycki("brzycki"),
    Epley("epley"),
    Lander("lander"),
    Lombardi("lombardi"),
    Mayhew("mayhew"),
    OConner("oconner"),
    Wathan("wathan"),
}
