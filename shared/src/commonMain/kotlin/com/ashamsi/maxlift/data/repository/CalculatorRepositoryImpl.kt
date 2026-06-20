package com.ashamsi.maxlift.data.repository

import com.ashamsi.maxlift.data.local.CalculatorStateDao
import com.ashamsi.maxlift.data.mapper.toDomain
import com.ashamsi.maxlift.data.mapper.toEntity
import com.ashamsi.maxlift.domain.model.CalculatorState
import com.ashamsi.maxlift.domain.repository.CalculatorRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Implementation of [CalculatorRepository] using [CalculatorStateDao].
 */
class CalculatorRepositoryImpl(private val dao: CalculatorStateDao) : CalculatorRepository {

    override fun getCalculatorState(): Flow<CalculatorState> {
        return dao.getCalculatorState().map { entity ->
            entity?.toDomain() ?: CalculatorState()
        }
    }

    override suspend fun saveCalculatorState(state: CalculatorState) {
        dao.insertOrUpdate(state.toEntity())
    }
}
