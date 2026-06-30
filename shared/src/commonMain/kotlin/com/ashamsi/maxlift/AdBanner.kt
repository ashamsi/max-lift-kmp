package com.ashamsi.maxlift

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

/**
 * Lifecycle status of a banner ad request.
 *
 * - [Loading]: the request is in flight. Space is reserved so the layout doesn't jump
 *   once the ad arrives.
 * - [Loaded]: an ad was returned and is being displayed.
 * - [Failed]: the request failed (no fill, network error, etc.). The banner should be
 *   collapsed so it doesn't occupy empty UI space.
 */
enum class AdBannerStatus { Loading, Loaded, Failed }

/**
 * Pure, platform-agnostic policy that decides how a banner should affect layout for a
 * given [AdBannerStatus]. Kept free of Compose/SDK types so it is trivially unit-testable.
 */
object AdBannerPolicy {
    /** Standard AdMob banner height in dp. */
    const val BANNER_HEIGHT_DP = 50

    /** Whether the banner should occupy layout space. Failed ads collapse to nothing. */
    fun shouldReserveSpace(status: AdBannerStatus): Boolean = status != AdBannerStatus.Failed

    /** Height the banner should take for the given [status], in dp. */
    fun heightDp(status: AdBannerStatus): Int =
        if (shouldReserveSpace(status)) BANNER_HEIGHT_DP else 0
}

/**
 * Observable holder for a single banner's [AdBannerStatus]. Platform [AdBanner]
 * implementations feed real SDK load/fail callbacks into this controller, and Compose
 * reads [status] to decide whether to render or collapse the banner.
 *
 * The transition logic lives here (rather than inside the platform UI code) so it can be
 * verified without an emulator, device, or live AdMob request.
 */
class AdBannerController {
    var status by mutableStateOf(AdBannerStatus.Loading)
        private set

    /** Called when the SDK reports a successful ad load. */
    fun onAdLoaded() {
        status = AdBannerStatus.Loaded
    }

    /** Called when the SDK reports a failed ad load. */
    fun onAdFailedToLoad() {
        status = AdBannerStatus.Failed
    }

    /** Whether the banner should currently occupy layout space. */
    val shouldReserveSpace: Boolean get() = AdBannerPolicy.shouldReserveSpace(status)

    /** Height the banner should currently take, in dp. */
    val heightDp: Int get() = AdBannerPolicy.heightDp(status)
}

/**
 * A platform-specific banner ad view.
 *
 * Implementations must collapse the banner (occupy no space) when the underlying ad
 * request fails, so an empty placeholder is never shown to the user.
 *
 * @param modifier Modifier to be applied to the banner.
 */
@Composable
expect fun AdBanner(modifier: Modifier = Modifier)
