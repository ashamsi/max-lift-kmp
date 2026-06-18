package com.ashamsi.maxlift

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource

// Use your generated Res object for multiplatform images
import maxlift.shared.generated.resources.Res
import maxlift.shared.generated.resources.header_light
import maxlift.shared.generated.resources.header_dark

@Composable
fun ParallaxScreen(
    onNavigateToFormulas: () -> Unit,
    onNavigateToAbout: () -> Unit
) {
    val scrollState = rememberScrollState()
    var showMenu by remember { mutableStateOf(false) }

    // Determine the header image based on the system theme
    val headerImage = if (isSystemInDarkTheme()) {
        Res.drawable.header_dark
    } else {
        Res.drawable.header_light
    }

    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    Box(modifier = Modifier
        .fillMaxSize()
        .imePadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .statusBarsPadding()
                .navigationBarsPadding()
                .verticalScroll(scrollState)
//                .imePadding()
                .clickable(
                    indication = null,
                    interactionSource = remember {MutableInteractionSource()}
                ) {
                    focusManager.clearFocus()
                },
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .graphicsLayer {
                        // Parallax effect: background moves at 50% scroll speed
                        translationY = scrollState.value * 0.5f
                    }
            ) {
                Image(
                    painter = painterResource(headerImage),
                    contentDescription = "Max Lift Header",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                )
            }

            // Main content area
            Column(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.background)
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = MaterialTheme.colorScheme.surface,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.outline,
                            shape = RoundedCornerShape(12.dp)
                        )
                ) {
                    WeightConverter(
                        keyboardController = keyboardController,
                        focusManager = focusManager)
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = MaterialTheme.colorScheme.surface,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.outline,
                            shape = RoundedCornerShape(12.dp)
                        )
                ) {
                    OneRepMaxCalculator(
                        keyboardController = keyboardController,
                        focusManager = focusManager,
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .statusBarsPadding()
                .padding(8.dp)
        ) {
            IconButton(onClick = { showMenu = true }) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
            DropdownMenu(
                expanded = showMenu,
                onDismissRequest = { showMenu = false },
                containerColor = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(8.dp)
            ) {
                DropdownMenuItem(
                    text = { Text("Formulas") },
                    leadingIcon = { Icon(Icons.Default.Info, contentDescription = null) },
                    onClick = {
                        showMenu = false
                        onNavigateToFormulas()
                    }
                )
                DropdownMenuItem(
                    text = { Text("About") },
                    leadingIcon = { Icon(Icons.Default.Build, contentDescription = null) },
                    onClick = {
                        showMenu = false
                        onNavigateToAbout()
                    }
                )
            }
        }
    }
}

@Preview
@Composable
fun ParallaxScreenPreview() {
    MaterialTheme {
        ParallaxScreen(onNavigateToFormulas = {}, onNavigateToAbout = {})
    }
}

@Preview(name = "Small Screen", widthDp = 320, heightDp = 480)
@Composable
fun ParallaxScreenSmallPreview() {
    MaterialTheme {
        ParallaxScreen(onNavigateToFormulas = {}, onNavigateToAbout = {})
    }
}

@Preview(name = "Dark Theme", showBackground = true, backgroundColor = 0xFF000000)
@Composable
fun ParallaxScreenDarkPreview() {
    MaterialTheme(colorScheme = MaterialTheme.colorScheme.copy(surface = Color.Black)) {
        // Force dark theme if your App implementation doesn't automatically switch
        ParallaxScreen(onNavigateToFormulas = {}, onNavigateToAbout = {})
    }
}
