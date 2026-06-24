package com.ashamsi.maxlift

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * A platform-specific banner ad view.
 * 
 * @param modifier Modifier to be applied to the banner.
 */
@Composable
expect fun AdBanner(modifier: Modifier = Modifier)
