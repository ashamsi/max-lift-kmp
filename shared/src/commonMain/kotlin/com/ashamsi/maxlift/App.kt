package com.ashamsi.maxlift

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
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

    MaterialTheme(colorScheme = colorScheme) {
        ParallaxScreen()
    }
}
