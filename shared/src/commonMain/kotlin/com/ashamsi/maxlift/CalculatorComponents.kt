package com.ashamsi.maxlift

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ashamsi.maxlift.domain.model.FormulaType
import com.ashamsi.maxlift.presentation.calculator.CalculatorEvent
import com.ashamsi.maxlift.presentation.calculator.CalculatorUiState

private const val ONE_RM_MAX_LB = 1500.0
private const val ONE_RM_MAX_KG = 999.0
private const val CONVERTER_MAX_LB = 9999.0
private const val CONVERTER_MAX_KG = 999.0

/**
 * Composable component for weight conversion between pounds and kilograms.
 */
@Composable
fun WeightConverter(
    modifier: Modifier = Modifier,
    state: CalculatorUiState,
    onEvent: (CalculatorEvent) -> Unit,
    keyboardController: SoftwareKeyboardController?,
    focusManager: FocusManager,
    onInputFocused: () -> Unit = {}
) {
    val lbText = state.calculatorState.converterLbText
    val kgText = state.calculatorState.converterKgText

    Column(modifier = modifier.padding(16.dp)) {
        Text(
            text = "Converter:",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            CustomNumericInput(
                value = lbText,
                onValueChange = { onEvent(CalculatorEvent.UpdateConverterLb(it)) },
                placeholder = "0",
                maxValue = CONVERTER_MAX_LB,
                modifier = Modifier.weight(1f),
                keyboardController = keyboardController,
                focusManager = focusManager,
                onFocused = onInputFocused
            )
            Text(" lb", color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.padding(horizontal = 4.dp))
            Text(" = ", color = MaterialTheme.colorScheme.onSurface)
            CustomNumericInput(
                value = kgText,
                onValueChange = { onEvent(CalculatorEvent.UpdateConverterKg(it)) },
                placeholder = "0",
                maxValue = CONVERTER_MAX_KG,
                modifier = Modifier.weight(1f),
                keyboardController = keyboardController,
                focusManager = focusManager,
                onFocused = onInputFocused
            )
            Text(" kg", color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.padding(horizontal = 4.dp))
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { onEvent(CalculatorEvent.ResetConverter) },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
            shape = RoundedCornerShape(4.dp)
        ) {
            Text("Reset", color = MaterialTheme.colorScheme.onSurface)
        }
    }
}

/**
 * Composable component for calculating 1RM based on weight and reps.
 */
@Composable
fun OneRepMaxCalculator(
    modifier: Modifier = Modifier,
    state: CalculatorUiState,
    onEvent: (CalculatorEvent) -> Unit,
    keyboardController: SoftwareKeyboardController?,
    focusManager: FocusManager,
    onInputFocused: () -> Unit = {}
) {
    val weightText = state.calculatorState.oneRmWeightText
    val reps = state.calculatorState.oneRmReps
    val isLb = state.calculatorState.oneRmIsLb
    val oneRM = state.oneRmResult

    Column(
        modifier = modifier
            .padding(16.dp)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                focusManager.clearFocus()
            }
    ) {
        Text(
            text = "1RM calculator",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            CustomNumericInput(
                value = weightText,
                onValueChange = { onEvent(CalculatorEvent.UpdateOneRmWeight(it)) },
                placeholder = "Eqpt. weight, ${if (isLb) "lb" else "kg"}",
                maxValue = if (isLb) ONE_RM_MAX_LB else ONE_RM_MAX_KG,
                modifier = Modifier.weight(1f),
                keyboardController = keyboardController,
                focusManager = focusManager,
                onFocused = onInputFocused
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text("lb", color = MaterialTheme.colorScheme.onSurface)
            Switch(
                checked = !isLb,
                onCheckedChange = { checked ->
                    onEvent(CalculatorEvent.ToggleOneRmUnit(!checked))
                },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = MaterialTheme.colorScheme.onSurface,
                    checkedTrackColor = Color.Gray,
                    uncheckedThumbColor = MaterialTheme.colorScheme.onSurface,
                    uncheckedTrackColor = Color.Gray
                )
            )
            Text("kg", color = MaterialTheme.colorScheme.onSurface)
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CalculatorButton("-", modifier = Modifier.weight(1f)) {
                if (reps > 1) onEvent(CalculatorEvent.UpdateOneRmReps(reps - 1))
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(4.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = reps.toString(), color = MaterialTheme.colorScheme.onSurface)
            }
            CalculatorButton("+", modifier = Modifier.weight(1f)) {
                if (reps < 10) onEvent(CalculatorEvent.UpdateOneRmReps(reps + 1))
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Record weight:",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (oneRM > 0) "${Formulas.roundToTwoDecimals(oneRM)} ${if (isLb) "lb" else "kg"}" else "0",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { onEvent(CalculatorEvent.ResetOneRm) },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
            shape = RoundedCornerShape(4.dp)
        ) {
            Text("Reset", color = MaterialTheme.colorScheme.onSurface)
        }
    }
}


/**
 * Custom numeric input field with a maximum value constraint.
 */
@Composable
fun CustomNumericInput(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    maxValue: Double? = null,
    keyboardController: SoftwareKeyboardController?,
    focusManager: FocusManager,
    onFocused: () -> Unit = {}
) {
    var textFieldValue by remember { mutableStateOf(TextFieldValue(value)) }

    LaunchedEffect(value) {
        if (value != textFieldValue.text) {
            textFieldValue = TextFieldValue(
                text = value,
                selection = TextRange(value.length)
            )
        }
    }

    Column(modifier) {
        BasicTextField(
            value = textFieldValue,
            onValueChange = { updated ->
                val text = updated.text
                if (text.isEmpty()) {
                    textFieldValue = updated
                    onValueChange(text)
                    return@BasicTextField
                }
                val numericValue = text.toDoubleOrNull() ?: return@BasicTextField
                if (maxValue == null || numericValue <= maxValue) {
                    textFieldValue = updated
                    onValueChange(text)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
                .background(Color.Transparent)
                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(4.dp))
                .padding(horizontal = 8.dp)
                .onFocusChanged { focusState ->
                    if (focusState.isFocused) {
                        onFocused()
                    }
                },
            textStyle = TextStyle(color = MaterialTheme.colorScheme.onSurface, fontSize = 16.sp),
            cursorBrush = SolidColor(MaterialTheme.colorScheme.onSurface),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal,
                imeAction = ImeAction.Done
            ),
            decorationBox = { innerTextField ->
                Box(contentAlignment = Alignment.CenterStart) {
                    if (textFieldValue.text.isEmpty()) {
                        Text(placeholder, color = Color.Gray, fontSize = 14.sp)
                    }
                    innerTextField()
                }
            },
            keyboardActions = KeyboardActions(
                onDone = {
                    keyboardController?.hide()
                    focusManager.clearFocus()
                }
            )
        )
    }
}

/**
 * Standardized button for calculator actions.
 */
@Composable
fun CalculatorButton(
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(48.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        shape = RoundedCornerShape(4.dp),
        contentPadding = PaddingValues(0.dp)
    ) {
        Text(text, color = MaterialTheme.colorScheme.onSurface, fontSize = 20.sp)
    }
}
