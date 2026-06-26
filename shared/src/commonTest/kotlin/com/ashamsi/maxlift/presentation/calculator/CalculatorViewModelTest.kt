package com.ashamsi.maxlift.presentation.calculator

import com.ashamsi.maxlift.domain.model.CalculatorState
import com.ashamsi.maxlift.domain.model.FormulaSelection
import com.ashamsi.maxlift.domain.model.FormulaType
import com.ashamsi.maxlift.domain.repository.CalculatorRepository
import com.ashamsi.maxlift.domain.repository.FormulaRepository
import com.ashamsi.maxlift.domain.usecase.GetCalculatorStateUseCase
import com.ashamsi.maxlift.domain.usecase.GetSelectedFormulasUseCase
import com.ashamsi.maxlift.domain.usecase.SaveCalculatorStateUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.*

@OptIn(ExperimentalCoroutinesApi::class)
class CalculatorViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var viewModel: CalculatorViewModel
    private lateinit var mockCalculatorRepository: MockCalculatorRepository
    private lateinit var mockFormulaRepository: MockFormulaRepository

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        mockCalculatorRepository = MockCalculatorRepository()
        mockFormulaRepository = MockFormulaRepository()
        
        viewModel = CalculatorViewModel(
            GetCalculatorStateUseCase(mockCalculatorRepository),
            SaveCalculatorStateUseCase(mockCalculatorRepository),
            GetSelectedFormulasUseCase(mockFormulaRepository)
        )
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testInitialState() = runTest {
        val state = viewModel.state.first()
        assertEquals("", state.calculatorState.oneRmWeightText)
        assertEquals(1, state.calculatorState.oneRmReps)
        assertEquals(listOf(FormulaType.Brzycki), state.selectedFormulas)
    }

    @Test
    fun testUpdateOneRmWeight() = runTest {
        viewModel.onEvent(CalculatorEvent.UpdateOneRmWeight("100"))

        val state = viewModel.state.value
        assertEquals("100", state.calculatorState.oneRmWeightText)
        assertEquals(100.0, state.oneRmResult) // Brzycki: 100 * (36/(37-1)) = 100

        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals("100", mockCalculatorRepository.savedState.value.oneRmWeightText)
    }

    @Test
    fun testRapidSequentialWeightUpdates() = runTest {
        viewModel.onEvent(CalculatorEvent.UpdateOneRmWeight("1"))
        viewModel.onEvent(CalculatorEvent.UpdateOneRmWeight("12"))
        viewModel.onEvent(CalculatorEvent.UpdateOneRmWeight("123"))
        viewModel.onEvent(CalculatorEvent.UpdateOneRmWeight("1234"))

        assertEquals("1234", viewModel.state.value.calculatorState.oneRmWeightText)

        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals("1234", mockCalculatorRepository.savedState.value.oneRmWeightText)
    }

    @Test
    fun testUpdateReps() = runTest {
        viewModel.onEvent(CalculatorEvent.UpdateOneRmWeight("100"))
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.onEvent(CalculatorEvent.UpdateOneRmReps(10))
        testDispatcher.scheduler.advanceUntilIdle()
        
        val state = viewModel.state.value
        assertEquals(10, state.calculatorState.oneRmReps)
        assertEquals(133.33, state.oneRmResult, 0.01)
    }

    @Test
    fun testReset() = runTest {
        viewModel.onEvent(CalculatorEvent.UpdateOneRmWeight("100"))
        viewModel.onEvent(CalculatorEvent.ResetOneRm)
        testDispatcher.scheduler.advanceUntilIdle()
        
        val state = viewModel.state.value
        assertEquals("", state.calculatorState.oneRmWeightText)
        assertEquals(1, state.calculatorState.oneRmReps)
    }
}

class MockCalculatorRepository : CalculatorRepository {
    val savedState = MutableStateFlow(CalculatorState())
    override fun getCalculatorState(): Flow<CalculatorState> = savedState
    override suspend fun saveCalculatorState(state: CalculatorState) {
        savedState.value = state
    }
}

class MockFormulaRepository : FormulaRepository {
    private val selectedFlow = MutableStateFlow(listOf(FormulaType.Brzycki))
    override fun getSelectedFormulas(): Flow<List<FormulaType>> = selectedFlow
    override fun getAllFormulasWithSelection(): Flow<List<FormulaSelection>> = TODO()
    override suspend fun toggleFormulaSelection(type: FormulaType, isSelected: Boolean) {
        val current = selectedFlow.value.toMutableList()
        if (isSelected) {
            if (!current.contains(type)) current.add(type)
        } else {
            current.remove(type)
        }
        selectedFlow.value = current
    }
}
