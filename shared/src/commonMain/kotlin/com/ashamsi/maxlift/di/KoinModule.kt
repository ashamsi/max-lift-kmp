package com.ashamsi.maxlift.di

import com.ashamsi.maxlift.data.local.AppDatabase
import com.ashamsi.maxlift.data.local.getDatabaseBuilder
import com.ashamsi.maxlift.data.local.getRoomDatabase
import com.ashamsi.maxlift.data.repository.CalculatorRepositoryImpl
import com.ashamsi.maxlift.data.repository.FormulaRepositoryImpl
import com.ashamsi.maxlift.domain.repository.CalculatorRepository
import com.ashamsi.maxlift.domain.repository.FormulaRepository
import com.ashamsi.maxlift.domain.usecase.*
import com.ashamsi.maxlift.presentation.calculator.CalculatorViewModel
import com.ashamsi.maxlift.presentation.formula.FormulaSelectionViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module

/**
 * Koin module for dependency injection.
 *
 * @param ctx Platform-specific context.
 */
fun appModule(ctx: Any? = null) = module {
    // Database
    single<AppDatabase> {
        getRoomDatabase(getDatabaseBuilder(ctx))
    }
    single { get<AppDatabase>().formulaDao() }
    single { get<AppDatabase>().calculatorStateDao() }

    // Repository
    singleOf(::FormulaRepositoryImpl) bind FormulaRepository::class
    singleOf(::CalculatorRepositoryImpl) bind CalculatorRepository::class

    // Use Cases
    factoryOf(::GetAllFormulasUseCase)
    factoryOf(::GetSelectedFormulasUseCase)
    factoryOf(::ToggleFormulaSelectionUseCase)
    factoryOf(::GetCalculatorStateUseCase)
    factoryOf(::SaveCalculatorStateUseCase)

    // ViewModels
    factoryOf(::FormulaSelectionViewModel)
    factoryOf(::CalculatorViewModel)
}
