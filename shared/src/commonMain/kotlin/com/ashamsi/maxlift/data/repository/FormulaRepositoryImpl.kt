package com.ashamsi.maxlift.data.repository

import com.ashamsi.maxlift.data.local.FormulaDao
import com.ashamsi.maxlift.data.local.FormulaEntity
import com.ashamsi.maxlift.data.mapper.toDomain
import com.ashamsi.maxlift.domain.model.FormulaSelection
import com.ashamsi.maxlift.domain.model.FormulaType
import com.ashamsi.maxlift.domain.repository.FormulaRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Implementation of [FormulaRepository] using [FormulaDao].
 */
class FormulaRepositoryImpl(private val dao: FormulaDao) : FormulaRepository {

    override fun getSelectedFormulas(): Flow<List<FormulaType>> {
        return dao.getAllFormulas().map { entities ->
            entities.filter { it.isSelected }.map { entity ->
                FormulaType.entries.find { it.id == entity.id } ?: FormulaType.Brzycki
            }
        }
    }

    override fun getAllFormulasWithSelection(): Flow<List<FormulaSelection>> {
        return dao.getAllFormulas().map { entities ->
            FormulaType.entries.map { type ->
                val entity = entities.find { it.id == type.id }
                FormulaSelection(
                    type = type,
                    isSelected = entity?.isSelected ?: (type == FormulaType.Brzycki)
                )
            }
        }
    }

    override suspend fun toggleFormulaSelection(type: FormulaType, isSelected: Boolean) {
        dao.insertOrUpdate(FormulaEntity(id = type.id, isSelected = isSelected))
    }
}
