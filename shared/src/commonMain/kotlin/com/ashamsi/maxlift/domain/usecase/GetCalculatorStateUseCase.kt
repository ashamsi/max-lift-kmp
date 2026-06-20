package com.ashamsi.maxlift.domain.usecase

import com.ashamsi.maxlift.domain.model.CalculatorState
import com.ashamsi.maxlift.domain.repository.CalculatorRepository
import kotlinx.coroutines.flow.Flow

/**
 * Use case to retrieve the current state of the calculators.
 */
class GetCalculatorStateUseCase(private val repository: CalculatorRepository) {
    /**
     * Executes the use case.
     *
     * @return A flow emitting the current calculator state.
     */
    operator fun invoke(): Flow<CalculatorState> = repository.getCalculatorState()
}
