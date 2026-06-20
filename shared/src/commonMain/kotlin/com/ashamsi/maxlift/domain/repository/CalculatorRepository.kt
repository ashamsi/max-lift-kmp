package com.ashamsi.maxlift.domain.repository

import com.ashamsi.maxlift.domain.model.CalculatorState
import kotlinx.coroutines.flow.Flow

/**
 * Repository for managing and persisting the state of the calculators.
 */
interface CalculatorRepository {
    /**
     * Retrieves the current calculator state as a flow.
     */
    fun getCalculatorState(): Flow<CalculatorState>

    /**
     * Saves the provided calculator state.
     *
     * @param state The state to persist.
     */
    suspend fun saveCalculatorState(state: CalculatorState)
}
