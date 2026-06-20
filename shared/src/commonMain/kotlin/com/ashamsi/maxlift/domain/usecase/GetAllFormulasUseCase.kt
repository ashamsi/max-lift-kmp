package com.ashamsi.maxlift.domain.usecase

import com.ashamsi.maxlift.domain.model.FormulaSelection
import com.ashamsi.maxlift.domain.repository.FormulaRepository
import kotlinx.coroutines.flow.Flow

/**
 * Use case to retrieve all formulas and their current selection status.
 */
class GetAllFormulasUseCase(private val repository: FormulaRepository) {
    /**
     * Executes the use case.
     *
     * @return A flow emitting the list of formulas with their selection status.
     */
    operator fun invoke(): Flow<List<FormulaSelection>> = repository.getAllFormulasWithSelection()
}
