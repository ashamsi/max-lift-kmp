package com.ashamsi.maxlift.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity representing a formula selection.
 */
@Entity(tableName = "formula_selection")
data class FormulaEntity(
    @PrimaryKey val id: String,
    val isSelected: Boolean
)
