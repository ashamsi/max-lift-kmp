package com.ashamsi.maxlift

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun WeightConverter(modifier: Modifier = Modifier) {
    var lbText by remember { mutableStateOf("") }
    var kgText by remember { mutableStateOf("") }

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
                onValueChange = {
                    lbText = it
                    if (it.isEmpty()) {
                        kgText = ""
                    } else {
                        it.toDoubleOrNull()?.let { lb ->
                            kgText = Formulas.convertLbToKg(lb).toString()
                        }
                    }
                },
                placeholder = "0",
                modifier = Modifier.weight(1f)
            )
            Text(" lb", color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.padding(horizontal = 4.dp))
            Text(" = ", color = MaterialTheme.colorScheme.onSurface)
            CustomNumericInput(
                value = kgText,
                onValueChange = {
                    kgText = it
                    if (it.isEmpty()) {
                        lbText = ""
                    } else {
                        it.toDoubleOrNull()?.let { kg ->
                            lbText = Formulas.convertKgToLb(kg).toString()
                        }
                    }
                },
                placeholder = "0",
                modifier = Modifier.weight(1f)
            )
            Text(" kg", color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.padding(horizontal = 4.dp))
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                lbText = ""
                kgText = ""
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
            shape = RoundedCornerShape(4.dp)
        ) {
            Text("Reset", color = MaterialTheme.colorScheme.onSurface)
        }
    }
}

@Composable
fun OneRepMaxCalculator(modifier: Modifier = Modifier) {
    val storage = LocalSecureStorage.current
    var weightText by remember { mutableStateOf("") }
    var reps by remember { mutableStateOf(1) }
    var isLb by remember { mutableStateOf(true) }
    
    val selectedFormulasIds by storage.getStringFlow("selected_formulas").collectAsState(initial = FormulaType.Brzycki.id)
    val selectedFormulas = remember(selectedFormulasIds) {
        selectedFormulasIds?.split(",")?.mapNotNull { id ->
            FormulaType.entries.find { it.id == id }
        }?.ifEmpty { listOf(FormulaType.Brzycki) } ?: listOf(FormulaType.Brzycki)
    }

    val oneRM = remember(weightText, reps, selectedFormulas) {
        weightText.toDoubleOrNull()?.let { weight ->
            Formulas.calculateMeanOneRM(selectedFormulas, weight, reps)
        } ?: 0.0
    }

    Column(modifier = modifier.padding(16.dp)) {
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
                onValueChange = { weightText = it },
                placeholder = "Eqpt. weight, ${if (isLb) "lb" else "kg"}",
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text("lb", color = MaterialTheme.colorScheme.onSurface)
            Switch(
                checked = !isLb,
                onCheckedChange = { checked ->
                    val nextIsLb = !checked
                    weightText.toDoubleOrNull()?.let { currentWeight ->
                        weightText = if (nextIsLb) {
                            Formulas.convertKgToLb(currentWeight).toString()
                        } else {
                            Formulas.convertLbToKg(currentWeight).toString()
                        }
                    }
                    isLb = nextIsLb
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
                if (reps > 1) reps--
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
                if (reps < 10) reps++
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
                text = if (oneRM > 0) "$oneRM ${if (isLb) "lb" else "kg"}" else "0",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                weightText = ""
                reps = 1
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
            shape = RoundedCornerShape(4.dp)
        ) {
            Text("Reset", color = MaterialTheme.colorScheme.onSurface)
        }
    }
}

@Composable
fun CustomNumericInput(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = ""
) {
    BasicTextField(
        value = value,
        onValueChange = { text ->
            if (text.isEmpty() || text.toDoubleOrNull() != null) {
                onValueChange(text)
            }
        },
        modifier = modifier
            .height(40.dp)
            .background(Color.Transparent)
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(4.dp))
            .padding(horizontal = 8.dp),
        textStyle = TextStyle(color = MaterialTheme.colorScheme.onSurface, fontSize = 16.sp),
        cursorBrush = SolidColor(MaterialTheme.colorScheme.onSurface),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        decorationBox = { innerTextField ->
            Box(contentAlignment = Alignment.CenterStart) {
                if (value.isEmpty()) {
                    Text(placeholder, color = Color.Gray, fontSize = 14.sp)
                }
                innerTextField()
            }
        }
    )
}

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
