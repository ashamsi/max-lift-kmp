package com.ashamsi.maxlift.domain.usecase

import com.ashamsi.maxlift.domain.model.CalculatorState
import com.ashamsi.maxlift.domain.repository.CalculatorRepository

/**
 * Use case to save/persist the state of the calculators.
 */
class SaveCalculatorStateUseCase(private val repository: CalculatorRepository) {
    /**
     * Executes the use case.
     *
     * @param state The calculator state to save.
     */
    suspend operator fun invoke(state: CalculatorState) = repository.saveCalculatorState(state)
}
