package com.ashamsi.maxlift.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity representing the persisted state of the calculators.
 */
@Entity(tableName = "calculator_state")
data class CalculatorStateEntity(
    @PrimaryKey val id: Int = 1,
    val converterLbText: String,
    val converterKgText: String,
    val oneRmWeightText: String,
    val oneRmReps: Int,
    val oneRmIsLb: Boolean
)
