package com.ashamsi.maxlift

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormulaSelectionScreen(onBack: () -> Unit) {
    val storage = LocalSecureStorage.current
    var selectedFormulas by remember { mutableStateOf(setOf(FormulaType.Brzycki)) }

    LaunchedEffect(Unit) {
        val saved = storage.getString("selected_formulas")
        if (saved != null) {
            val loaded = saved.split(",").mapNotNull { id ->
                FormulaType.entries.find { it.id == id }
            }.toSet()
            if (loaded.isNotEmpty()) {
                selectedFormulas = loaded
            }
        }
    }

    LaunchedEffect(selectedFormulas) {
        storage.putString("selected_formulas", selectedFormulas.joinToString(",") { it.id })
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("1RM Formulas", color = MaterialTheme.colorScheme.onSurface) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            items(FormulaType.entries) { formula ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = selectedFormulas.contains(formula),
                        onCheckedChange = { checked ->
                            val newSet = if (checked) {
                                selectedFormulas + formula
                            } else {
                                // Keep at least one selected
                                if (selectedFormulas.size > 1) selectedFormulas - formula else selectedFormulas
                            }
                            selectedFormulas = newSet
                        },
                        colors = CheckboxDefaults.colors(
                            checkedColor = MaterialTheme.colorScheme.primary,
                            uncheckedColor = MaterialTheme.colorScheme.outline
                        )
                    )
                    Text(
                        text = formula.name,
                        modifier = Modifier.padding(start = 8.dp),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}
