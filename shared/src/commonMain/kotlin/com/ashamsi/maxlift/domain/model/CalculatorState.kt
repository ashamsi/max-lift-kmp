package com.ashamsi.maxlift.domain.model

/**
 * Represents the current state of the calculator inputs.
 *
 * @property converterLbText The text input for the weight in pounds in the converter.
 * @property converterKgText The text input for the weight in kilograms in the converter.
 * @property oneRmWeightText The text input for the weight in the 1RM calculator.
 * @property oneRmReps The number of repetitions in the 1RM calculator.
 * @property oneRmIsLb Whether the 1RM calculator is currently using pounds as unit.
 */
data class CalculatorState(
    val converterLbText: String = "",
    val converterKgText: String = "",
    val oneRmWeightText: String = "",
    val oneRmReps: Int = 1,
    val oneRmIsLb: Boolean = true
)
