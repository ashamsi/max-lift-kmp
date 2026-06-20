package com.ashamsi.maxlift.domain.repository

import com.ashamsi.maxlift.domain.model.FormulaSelection
import com.ashamsi.maxlift.domain.model.FormulaType
import kotlinx.coroutines.flow.Flow

/**
 * Repository for managing formula selections and availability.
 */
interface FormulaRepository {
    /**
     * Retrieves the list of currently selected formulas as a flow.
     */
    fun getSelectedFormulas(): Flow<List<FormulaType>>

    /**
     * Retrieves all available formulas with their current selection status.
     */
    fun getAllFormulasWithSelection(): Flow<List<FormulaSelection>>

    /**
     * Toggles the selection status of a specific formula.
     *
     * @param type The formula type to toggle.
     * @param isSelected The new selection status.
     */
    suspend fun toggleFormulaSelection(type: FormulaType, isSelected: Boolean)
}
