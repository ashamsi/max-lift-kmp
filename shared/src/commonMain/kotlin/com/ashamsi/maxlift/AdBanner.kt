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
    /**
     * Default/fallback banner height in dp, used to reserve space before the SDK reports
     * the real (adaptive) height. Adaptive anchored banners are usually 50-90 dp.
     */
    const val BANNER_HEIGHT_DP = 50

    /** Whether the banner should occupy layout space. Failed ads collapse to nothing. */
    fun shouldReserveSpace(status: AdBannerStatus): Boolean = status != AdBannerStatus.Failed

    /** Height the banner should take for the given [status], in dp, using the default height. */
    fun heightDp(status: AdBannerStatus): Int =
        if (shouldReserveSpace(status)) BANNER_HEIGHT_DP else 0
}

/**
 * Observable holder for a single banner's [AdBannerStatus] and resolved height. Platform
 * [AdBanner] implementations feed real SDK load/fail/size callbacks into this controller,
 * and Compose reads [heightDp]/[shouldReserveSpace] to render, resize, or collapse the
 * banner.
 *
 * The transition logic lives here (rather than inside the platform UI code) so it can be
 * verified without an emulator, device, or live AdMob request.
 */
class AdBannerController {
    var status by mutableStateOf(AdBannerStatus.Loading)
        private set

    /**
     * Height (dp) reserved for the banner while visible. Seeded with the default and
     * updated once the platform resolves an adaptive size via [onAdSizeResolved]. This is
     * the seam that makes variable/adaptive heights (and future orientation changes) work
     * without touching the layout code.
     */
    var reservedHeightDp by mutableStateOf(AdBannerPolicy.BANNER_HEIGHT_DP)
        private set

    /** Called when the SDK reports a successful ad load. */
    fun onAdLoaded() {
        status = AdBannerStatus.Loaded
    }

    /** Called when the SDK reports a failed ad load. */
    fun onAdFailedToLoad() {
        status = AdBannerStatus.Failed
    }

    /**
     * Called when the platform resolves the (adaptive) banner height, e.g. on first layout
     * or after an orientation change. Non-positive values are ignored.
     */
    fun onAdSizeResolved(heightDp: Int) {
        if (heightDp > 0) reservedHeightDp = heightDp
    }

    /** Whether the banner should currently occupy layout space. */
    val shouldReserveSpace: Boolean get() = AdBannerPolicy.shouldReserveSpace(status)

    /** Height the banner should currently take, in dp (0 when collapsed). */
    val heightDp: Int get() = if (shouldReserveSpace) reservedHeightDp else 0
}

/**
 * A platform-specific banner ad view.
 *
 * Implementations must collapse the banner (occupy no space) when the underlying ad
 * request fails, so an empty placeholder is never shown to the user.
 *
 * @param modifier Modifier to be applied to the banner.
 * @param controller Optional hoisted [AdBannerController]. Pass a remembered instance when
 *   the caller needs to observe the banner's state/height (e.g. to reserve matching space
 *   elsewhere in the layout). When null, the implementation remembers its own.
 */
@Composable
expect fun AdBanner(modifier: Modifier = Modifier, controller: AdBannerController? = null)
