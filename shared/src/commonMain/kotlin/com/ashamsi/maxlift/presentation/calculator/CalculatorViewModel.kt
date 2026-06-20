package com.ashamsi.maxlift.presentation.calculator

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ashamsi.maxlift.Formulas
import com.ashamsi.maxlift.domain.model.CalculatorState
import com.ashamsi.maxlift.domain.model.FormulaType
import com.ashamsi.maxlift.domain.usecase.GetCalculatorStateUseCase
import com.ashamsi.maxlift.domain.usecase.GetSelectedFormulasUseCase
import com.ashamsi.maxlift.domain.usecase.SaveCalculatorStateUseCase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * UI State for the calculator screen.
 *
 * @property calculatorState The persisted state of the calculator inputs.
 * @property selectedFormulas The list of formulas currently selected by the user.
 */
data class CalculatorUiState(
    val calculatorState: CalculatorState = CalculatorState(),
    val selectedFormulas: List<FormulaType> = listOf(FormulaType.Brzycki)
) {
    /**
     * Calculates the mean 1RM result based on the selected formulas.
     */
    val oneRmResult: Double
        get() {
            val weight = calculatorState.oneRmWeightText.toDoubleOrNull() ?: return 0.0
            return Formulas.calculateMeanOneRM(selectedFormulas, weight, calculatorState.oneRmReps)
        }
}

/**
 * UI events for the calculator screen.
 */
sealed interface CalculatorEvent {
    /** Update the weight in pounds in the converter. */
    data class UpdateConverterLb(val text: String) : CalculatorEvent
    /** Update the weight in kilograms in the converter. */
    data class UpdateConverterKg(val text: String) : CalculatorEvent
    /** Update the weight in the 1RM calculator. */
    data class UpdateOneRmWeight(val text: String) : CalculatorEvent
    /** Update the number of reps in the 1RM calculator. */
    data class UpdateOneRmReps(val reps: Int) : CalculatorEvent
    /** Toggle between pounds and kilograms in the 1RM calculator. */
    data class ToggleOneRmUnit(val isLb: Boolean) : CalculatorEvent
    /** Reset the converter inputs. */
    object ResetConverter : CalculatorEvent
    /** Reset the 1RM calculator inputs. */
    object ResetOneRm : CalculatorEvent
}

/**
 * ViewModel for managing the state and events of the calculators.
 */
class CalculatorViewModel(
    private val getCalculatorStateUseCase: GetCalculatorStateUseCase,
    private val saveCalculatorStateUseCase: SaveCalculatorStateUseCase,
    private val getSelectedFormulasUseCase: GetSelectedFormulasUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(CalculatorUiState())
    val state: StateFlow<CalculatorUiState> = _state.asStateFlow()

    init {
        combine(
            getCalculatorStateUseCase(),
            getSelectedFormulasUseCase()
        ) { calcState, formulas ->
            CalculatorUiState(
                calculatorState = calcState,
                selectedFormulas = formulas.ifEmpty { listOf(FormulaType.Brzycki) }
            )
        }.onEach { newState ->
            _state.value = newState
        }.launchIn(viewModelScope)
    }

    fun onEvent(event: CalculatorEvent) {
        viewModelScope.launch {
            val currentState = _state.value.calculatorState
            val newState = when (event) {
                is CalculatorEvent.UpdateConverterLb -> {
                    val kg = if (event.text.isEmpty()) "" else {
                        event.text.toDoubleOrNull()?.let { Formulas.convertLbToKg(it).toString() } ?: ""
                    }
                    currentState.copy(converterLbText = event.text, converterKgText = kg)
                }
                is CalculatorEvent.UpdateConverterKg -> {
                    val lb = if (event.text.isEmpty()) "" else {
                        event.text.toDoubleOrNull()?.let { Formulas.convertKgToLb(it).toString() } ?: ""
                    }
                    currentState.copy(converterKgText = event.text, converterLbText = lb)
                }
                is CalculatorEvent.UpdateOneRmWeight -> currentState.copy(oneRmWeightText = event.text)
                is CalculatorEvent.UpdateOneRmReps -> currentState.copy(oneRmReps = event.reps)
                is CalculatorEvent.ToggleOneRmUnit -> {
                    val nextIsLb = event.isLb
                    val currentWeight = currentState.oneRmWeightText.toDoubleOrNull()
                    val nextWeight = if (currentWeight != null) {
                        if (nextIsLb) {
                            Formulas.convertKgToLb(currentWeight).toString()
                        } else {
                            Formulas.convertLbToKg(currentWeight).toString()
                        }
                    } else {
                        ""
                    }
                    currentState.copy(oneRmIsLb = nextIsLb, oneRmWeightText = nextWeight)
                }
                CalculatorEvent.ResetConverter -> currentState.copy(converterLbText = "", converterKgText = "")
                CalculatorEvent.ResetOneRm -> currentState.copy(oneRmWeightText = "", oneRmReps = 1)
            }
            saveCalculatorStateUseCase(newState)
        }
    }
}
