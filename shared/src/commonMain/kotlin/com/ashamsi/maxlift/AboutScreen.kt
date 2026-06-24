package com.ashamsi.maxlift

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Screen providing information about the Max Lift application.
 *
 * @param onBack Callback when the user clicks the back button.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("About Max Lift") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        bottomBar = {
            AdBanner(modifier = Modifier.navigationBarsPadding())
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Max Lift is a practical toolkit for weightlifters and strength athletes who train in pounds, kilograms, or both.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )

            AboutSection(
                title = "Convert weights instantly",
                description = "Switch between lb and kg with live conversion — no mental math at the rack or on the platform."
            )

            AboutSection(
                title = "Estimate your 1RM accurately",
                description = "Enter the weight you lifted and the reps you completed to calculate your one-rep max. Results work in lb or kg, so you can train and program in whichever unit you prefer."
            )

            AboutSection(
                title = "Choose from trusted formulas",
                description = "Max Lift supports several widely used 1RM estimation formulas, including Brzycki, Epley, Lander, Lombardi, Mayhew, O’Conner, and Wathan — pick the one that fits your training style."
            )

            AboutSection(
                title = "Built for lifters",
                description = "Clean, focused design with the tools you need and nothing you don’t. Whether you’re warming up, working sets, or planning your next cycle, Max Lift keeps the numbers straight."
            )

            Text(
                text = "Download Max Lift and spend less time calculating — more time lifting.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun AboutSection(title: String, description: String) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
        )
    }
}
