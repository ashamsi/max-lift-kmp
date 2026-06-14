package com.ashamsi.maxlift

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview

val LocalSecureStorage = staticCompositionLocalOf<SecureStorage> {
    error("No SecureStorage provided")
}

enum class Screen {
    Main,
    Formulas,
    About
}

@Composable
fun App(factory: SecureStorageFactory? = null) {
    val darkTheme = isSystemInDarkTheme()
    val colorScheme = if (darkTheme) {
        darkColorScheme(
            primary = Color(0xFF5383EC),
            background = Color(0xFF151718),
            surface = Color(0xFF1E2123), // Card Background
            onBackground = Color.White,
            onSurface = Color.White,
            outline = Color(0xFF3F4346)  // Subtle Dark Border
        )
    } else {
        lightColorScheme(
            primary = Color(0xFF5383EC),
            background = Color(0xFFF2F4F7), // Off-white Background
            surface = Color.White,          // Card Background
            onBackground = Color.Black,
            onSurface = Color.Black,
            outline = Color(0xFFD0D5DD)    // Subtle Light Border
        )
    }

    var currentScreen by remember { mutableStateOf(Screen.Main) }
    val storage = remember { factory?.createStorage() }

    MaterialTheme(colorScheme = colorScheme) {
        if (storage != null) {
            CompositionLocalProvider(LocalSecureStorage provides storage) {
                when (currentScreen) {
                    Screen.Main -> ParallaxScreen(
                        onNavigateToFormulas = { currentScreen = Screen.Formulas },
                        onNavigateToAbout = { currentScreen = Screen.About }
                    )
                    Screen.Formulas -> FormulaSelectionScreen(
                        onBack = { currentScreen = Screen.Main }
                    )
                    Screen.About -> { /* TODO */ }
                }
            }
        } else {
            // Fallback for previews
            when (currentScreen) {
                Screen.Main -> ParallaxScreen(
                    onNavigateToFormulas = { currentScreen = Screen.Formulas },
                    onNavigateToAbout = { currentScreen = Screen.About }
                )
                Screen.Formulas -> { /* Empty for preview */ }
                Screen.About -> { /* TODO */ }
            }
        }
    }
}
