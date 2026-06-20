package com.ashamsi.maxlift.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for formula selections.
 */
@Dao
interface FormulaDao {
    /**
     * Retrieves all formula selection entries.
     */
    @Query("SELECT * FROM formula_selection")
    fun getAllFormulas(): Flow<List<FormulaEntity>>

    /**
     * Inserts or updates a formula selection entry.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(formula: FormulaEntity)

    /**
     * Clears all formula selection entries.
     */
    @Query("DELETE FROM formula_selection")
    suspend fun clearAll()
}
