package com.ashamsi.maxlift.domain.usecase

import com.ashamsi.maxlift.domain.model.FormulaType
import com.ashamsi.maxlift.domain.repository.FormulaRepository

/**
 * Use case to toggle the selection of a formula.
 */
class ToggleFormulaSelectionUseCase(private val repository: FormulaRepository) {
    /**
     * Executes the use case.
     *
     * @param type The formula type to toggle.
     * @param isSelected The new selection status.
     */
    suspend operator fun invoke(type: FormulaType, isSelected: Boolean) {
        repository.toggleFormulaSelection(type, isSelected)
    }
}
