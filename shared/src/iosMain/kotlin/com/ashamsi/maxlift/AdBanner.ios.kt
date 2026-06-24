package com.ashamsi.maxlift

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
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
 * Global registry for the AdView factory.
 * The iOS application should set this during startup.
 */
object AdViewFactory {
    var createAdView: (() -> UIView)? = null
}

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun AdBanner(modifier: Modifier) {
    val factory = AdViewFactory.createAdView
    if (factory != null) {
        UIKitView(
            factory = {
                val view = factory()
                // If it's a BannerView, we could dynamically set the ID here if the factory allowed it,
                // but since the ID is set in Swift's factory block, we trust the Swift side for now.
                // However, let's keep it consistent by logging or providing a fallback.
                view
            },
            modifier = modifier.fillMaxWidth().height(50.dp)
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
            modifier = modifier.fillMaxWidth().height(50.dp)
        )
    }
}
