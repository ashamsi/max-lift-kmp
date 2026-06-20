package com.ashamsi.maxlift.data.mapper

import com.ashamsi.maxlift.data.local.FormulaEntity
import com.ashamsi.maxlift.domain.model.FormulaSelection
import com.ashamsi.maxlift.domain.model.FormulaType

/**
 * Maps [FormulaEntity] to [FormulaSelection].
 */
fun FormulaEntity.toDomain(): FormulaSelection {
    return FormulaSelection(
        type = FormulaType.entries.find { it.id == id } ?: FormulaType.Brzycki,
        isSelected = isSelected
    )
}

/**
 * Maps [FormulaSelection] to [FormulaEntity].
 */
fun FormulaSelection.toEntity(): FormulaEntity {
    return FormulaEntity(
        id = type.id,
        isSelected = isSelected
    )
}
