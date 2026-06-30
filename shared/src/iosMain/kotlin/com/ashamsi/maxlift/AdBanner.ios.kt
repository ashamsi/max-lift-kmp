package com.ashamsi.maxlift

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.UIKitView
import kotlinx.cinterop.ExperimentalForeignApi
import platform.UIKit.UIView
import platform.UIKit.UIColor
import platform.UIKit.UILabel
import platform.UIKit.NSTextAlignmentCenter
import platform.CoreGraphics.CGRectMake

/**
 * Callbacks the Swift `BannerViewDelegate` invokes so the shared Compose layer can react
 * to ad load/failure events (e.g. collapse the banner when an ad fails).
 *
 * This is intentionally an interface rather than a pair of `() -> Unit` lambdas: when a
 * Kotlin function type is *nested* inside another function type, Kotlin/Native bridges it
 * to Swift as `() -> KotlinUnit` instead of `() -> Void`, which is awkward to call from
 * Swift. Interface methods returning [Unit] bridge cleanly to Swift `Void`.
 */
interface AdBannerListener {
    fun onAdLoaded()
    fun onAdFailedToLoad()
}

/**
 * Global registry for the AdView factory.
 *
 * The iOS application sets [createAdView] during startup. The factory receives an
 * [AdBannerListener] that the Swift `BannerViewDelegate` must notify on load/failure.
 *
 * @see com.ashamsi.maxlift.AdBannerController
 */
object AdViewFactory {
    var createAdView: ((listener: AdBannerListener) -> UIView)? = null
}

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun AdBanner(modifier: Modifier) {
    val controller = remember { AdBannerController() }
    val listener = remember {
        object : AdBannerListener {
            override fun onAdLoaded() = controller.onAdLoaded()
            override fun onAdFailedToLoad() = controller.onAdFailedToLoad()
        }
    }
    val factory = AdViewFactory.createAdView
    if (factory != null) {
        // Collapse to zero space once the ad fails so we never show an empty banner slot.
        if (!controller.shouldReserveSpace) return

        UIKitView(
            factory = {
                factory(listener)
            },
            modifier = modifier.fillMaxWidth().height(controller.heightDp.dp)
        )
    } else {
        // Placeholder for development if factory is not set
        UIKitView(
            factory = {
                UIView().apply {
                    backgroundColor = UIColor.lightGrayColor
                    val label = UILabel(frame = CGRectMake(0.0, 0.0, 320.0, 50.0))
                    label.text = "Ad Banner Placeholder"
                    label.textAlignment = NSTextAlignmentCenter
                    addSubview(label)
                }
            },
            modifier = modifier.fillMaxWidth().height(AdBannerPolicy.BANNER_HEIGHT_DP.dp)
        )
    }
}
