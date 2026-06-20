package com.ashamsi.maxlift.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for the calculator state.
 */
@Dao
interface CalculatorStateDao {
    /**
     * Retrieves the single calculator state record.
     */
    @Query("SELECT * FROM calculator_state WHERE id = 1")
    fun getCalculatorState(): Flow<CalculatorStateEntity?>

    /**
     * Inserts or updates the calculator state.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(state: CalculatorStateEntity)
}
