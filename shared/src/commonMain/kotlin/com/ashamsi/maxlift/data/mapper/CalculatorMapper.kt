package com.ashamsi.maxlift.data.mapper

import com.ashamsi.maxlift.data.local.CalculatorStateEntity
import com.ashamsi.maxlift.domain.model.CalculatorState

/**
 * Maps [CalculatorStateEntity] to [CalculatorState].
 */
fun CalculatorStateEntity.toDomain(): CalculatorState {
    return CalculatorState(
        converterLbText = converterLbText,
        converterKgText = converterKgText,
        oneRmWeightText = oneRmWeightText,
        oneRmReps = oneRmReps,
        oneRmIsLb = oneRmIsLb
    )
}

/**
 * Maps [CalculatorState] to [CalculatorStateEntity].
 */
fun CalculatorState.toEntity(): CalculatorStateEntity {
    return CalculatorStateEntity(
        converterLbText = converterLbText,
        converterKgText = converterKgText,
        oneRmWeightText = oneRmWeightText,
        oneRmReps = oneRmReps,
        oneRmIsLb = oneRmIsLb
    )
}
