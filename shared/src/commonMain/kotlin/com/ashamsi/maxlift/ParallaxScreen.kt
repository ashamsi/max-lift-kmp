package com.ashamsi.maxlift

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ashamsi.maxlift.presentation.calculator.CalculatorViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

// Use your generated Res object for multiplatform images
import maxlift.shared.generated.resources.Res
import maxlift.shared.generated.resources.header_light
import maxlift.shared.generated.resources.header_dark
import kotlin.time.Duration.Companion.milliseconds

/**
 * Main screen of the application with a parallax header effect.
 *
 * @param onNavigateToFormulas Callback to navigate to the formula selection screen.
 * @param onNavigateToAbout Callback to navigate to the about screen.
 * @param viewModel ViewModel for calculator state management.
 */
@Composable
fun ParallaxScreen(
    onNavigateToFormulas: () -> Unit,
    onNavigateToAbout: () -> Unit,
    viewModel: CalculatorViewModel = koinViewModel()
) {
    val scrollState = rememberScrollState()
    var showMenu by remember { mutableStateOf(false) }
    val state by viewModel.state.collectAsStateWithLifecycle()

    // Determine the header image based on the system theme
    val headerImage = if (isSystemInDarkTheme()) {
        Res.drawable.header_dark
    } else {
        Res.drawable.header_light
    }

    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val density = LocalDensity.current
    val bottomScrollPadding = with(density) { 16.dp.roundToPx() }
    val topScrollPadding = with(density) { 16.dp.roundToPx() }
    val imeBottom = WindowInsets.ime.getBottom(density)

    var scrollContainerCoords by remember { mutableStateOf<LayoutCoordinates?>(null) }
    var rootCoords by remember { mutableStateOf<LayoutCoordinates?>(null) }
    var converterCardCoords by remember { mutableStateOf<LayoutCoordinates?>(null) }
    var calculatorCardCoords by remember { mutableStateOf<LayoutCoordinates?>(null) }
    var pendingScrollCard by remember { mutableStateOf<LayoutCoordinates?>(null) }
    var scrollGeneration by remember { mutableIntStateOf(0) }
    val viewportHeight = scrollContainerCoords?.size?.height ?: 0

    fun effectiveViewportHeight(container: LayoutCoordinates): Int {
        val rootHeight = rootCoords?.size?.height ?: container.size.height
        return if (imeBottom > 0) {
            minOf(container.size.height, rootHeight - imeBottom)
        } else {
            container.size.height
        }
    }

    fun cardScrollTarget(cardCoords: LayoutCoordinates?): Int {
        val container = scrollContainerCoords ?: return 0
        if (cardCoords == null) return 0

        val cardTopInContent = cardCoords.positionInRoot().y - container.positionInRoot().y + scrollState.value
        val cardBottomInContent = cardTopInContent + cardCoords.size.height
        val cardHeight = cardCoords.size.height
        val visibleHeight = effectiveViewportHeight(container)

        val alignBottom = (cardBottomInContent - visibleHeight + bottomScrollPadding).toInt()
        val alignTop = (cardTopInContent - topScrollPadding).toInt()

        // When the card fits, scroll the minimum amount needed to reveal it fully.
        // When it is taller than the viewport, bottom-align so the Reset button stays visible.
        return if (cardHeight <= visibleHeight) {
            minOf(alignTop, alignBottom).coerceAtLeast(0)
        } else {
            alignBottom.coerceAtLeast(0)
        }.coerceIn(0, scrollState.maxValue)
    }

    fun requestScrollToCard(cardCoords: LayoutCoordinates?) {
        pendingScrollCard = cardCoords
        scrollGeneration++
    }

//    LaunchedEffect(scrollGeneration, viewportHeight, imeBottom) {
//        if (pendingScrollCard == null) return@LaunchedEffect
//        delay(150)
//        scrollState.animateScrollTo(cardScrollTarget(pendingScrollCard))
//        if (imeBottom > 0) {
//            delay(250)
//            scrollState.animateScrollTo(cardScrollTarget(pendingScrollCard))
//        }
//    }

    LaunchedEffect(scrollGeneration, viewportHeight, imeBottom) {
        if (pendingScrollCard == null) return@LaunchedEffect
        delay(100.milliseconds)
        scrollState.animateScrollTo(cardScrollTarget(pendingScrollCard))
    }

    val scrollToConverter = { requestScrollToCard(converterCardCoords) }
    val scrollToCalculator = { requestScrollToCard(calculatorCardCoords) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .onGloballyPositioned { rootCoords = it }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .onGloballyPositioned { scrollContainerCoords = it }
                .background(MaterialTheme.colorScheme.background)
                .statusBarsPadding()
                .navigationBarsPadding()
                .imePadding()
                .verticalScroll(scrollState)
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
                        .onGloballyPositioned { converterCardCoords = it }
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
                        state = state,
                        onEvent = viewModel::onEvent,
                        keyboardController = keyboardController,
                        focusManager = focusManager,
                        onInputFocused = scrollToConverter
                    )
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .onGloballyPositioned { calculatorCardCoords = it }
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
                        state = state,
                        onEvent = viewModel::onEvent,
                        keyboardController = keyboardController,
                        focusManager = focusManager,
                        onInputFocused = scrollToCalculator
                    )
                }
            }
            Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.ime))
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
