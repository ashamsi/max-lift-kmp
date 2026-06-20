package com.ashamsi.maxlift.presentation.formula

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ashamsi.maxlift.domain.usecase.GetAllFormulasUseCase
import com.ashamsi.maxlift.domain.usecase.ToggleFormulaSelectionUseCase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * ViewModel for managing formula selections.
 */
class FormulaSelectionViewModel(
    private val getAllFormulasUseCase: GetAllFormulasUseCase,
    private val toggleFormulaSelectionUseCase: ToggleFormulaSelectionUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(FormulaSelectionState(isLoading = true))
    val state: StateFlow<FormulaSelectionState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            getAllFormulasUseCase()
                .onEach { formulas ->
                    _state.update { it.copy(formulas = formulas, isLoading = false) }
                }
                .launchIn(viewModelScope)
        }
    }

    fun onEvent(event: FormulaSelectionEvent) {
        when (event) {
            is FormulaSelectionEvent.ToggleFormula -> {
                viewModelScope.launch {
                    toggleFormulaSelectionUseCase(
                        type = event.formula.type,
                        isSelected = !event.formula.isSelected
                    )
                }
            }
        }
    }
}
