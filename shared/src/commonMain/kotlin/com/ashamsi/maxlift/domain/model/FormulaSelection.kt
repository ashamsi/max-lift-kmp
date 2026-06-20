package com.ashamsi.maxlift.domain.model

/**
 * Represents a formula and its current selection status.
 *
 * @property type The type of the 1RM formula.
 * @property isSelected Whether this formula is currently selected for calculations.
 */
data class FormulaSelection(
    val type: FormulaType,
    val isSelected: Boolean
)
