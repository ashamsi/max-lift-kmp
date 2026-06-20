package com.ashamsi.maxlift.presentation.formula

import com.ashamsi.maxlift.domain.model.FormulaSelection

/**
 * UI State for the formula selection screen.
 *
 * @property formulas List of available formulas and their selection status.
 * @property isLoading Whether the data is currently being loaded.
 */
data class FormulaSelectionState(
    val formulas: List<FormulaSelection> = emptyList(),
    val isLoading: Boolean = false
)

/**
 * UI events for the formula selection screen.
 */
sealed interface FormulaSelectionEvent {
    /** Toggle the selection status of a formula. */
    data class ToggleFormula(val formula: FormulaSelection) : FormulaSelectionEvent
}
