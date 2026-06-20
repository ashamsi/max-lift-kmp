package com.ashamsi.maxlift.domain.usecase

import com.ashamsi.maxlift.domain.model.FormulaType
import com.ashamsi.maxlift.domain.repository.FormulaRepository
import kotlinx.coroutines.flow.Flow

/**
 * Use case to retrieve the list of currently selected formulas.
 */
class GetSelectedFormulasUseCase(private val repository: FormulaRepository) {
    /**
     * Executes the use case.
     *
     * @return A flow emitting the list of selected formula types.
     */
    operator fun invoke(): Flow<List<FormulaType>> = repository.getSelectedFormulas()
}
