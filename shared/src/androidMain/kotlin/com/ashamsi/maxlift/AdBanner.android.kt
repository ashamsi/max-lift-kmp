package com.ashamsi.maxlift

import android.content.pm.ApplicationInfo
import android.util.Log
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError

private const val AD_LOG_TAG = "MaxLiftAds"

@Composable
actual fun AdBanner(modifier: Modifier, controller: AdBannerController?) {
    // Drives visibility/height from real SDK callbacks (see AdBannerController).
    val resolvedController = controller ?: remember { AdBannerController() }

    // Collapse to zero space once the ad fails so we never show an empty banner slot.
    if (!resolvedController.shouldReserveSpace) return

    val context = LocalContext.current
    val configuration = LocalConfiguration.current

    // ROTATION DOOR: derive the ad width from the *current* configuration rather than
    // assuming portrait. While the app is locked to portrait this never changes, but if
    // rotation is enabled later, screenWidthDp changes -> the adaptive size is recomputed
    // and (because of the key() below) the banner is re-requested automatically.
    val adWidthDp = configuration.screenWidthDp

    // Large anchored adaptive banner: full-width, taller format (up to 20% of screen
    // height, 50-150 dp) optimized for video. See AdMob docs:
    // https://developers.google.com/admob/android/banner#kotlin
    val adSize = remember(adWidthDp) {
        AdSize.getLargeAnchoredAdaptiveBannerAdSize(context, adWidthDp)
    }

    // Report the adaptive height so the container (and any hoisted consumer) sizes to match.
    LaunchedEffect(adSize) {
        resolvedController.onAdSizeResolved(adSize.height)
    }

    // Re-create the AdView when the adaptive width changes (see ROTATION DOOR above).
    key(adWidthDp) {
        AndroidView(
            modifier = modifier
                .fillMaxWidth()
                .height(resolvedController.heightDp.dp),
            factory = { ctx ->
                // Only debug builds are flagged debuggable; release AABs (including the
                // Play Internal track) are not, so they correctly resolve to production ads.
                val isDebug = (ctx.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0
                val adUnitId = AdConfig.getBannerAdUnitId(PlatformType.Android, isDebug)

                AdView(ctx).apply {
                    this.adUnitId = adUnitId
                    setAdSize(adSize)
                    // Mirrors the iOS BannerViewDelegate: log the outcome and update state
                    // so the banner collapses on failure.
                    adListener = object : AdListener() {
                        override fun onAdLoaded() {
                            Log.i(AD_LOG_TAG, "Banner loaded. unit=$adUnitId")
                            resolvedController.onAdLoaded()
                        }

                        override fun onAdFailedToLoad(error: LoadAdError) {
                            Log.e(AD_LOG_TAG, "Banner failed. unit=$adUnitId, error=${error.message}")
                            resolvedController.onAdFailedToLoad()
                        }
                    }
                    loadAd(AdRequest.Builder().build())
                }
            },
            update = { },
            onRelease = { adView ->
                adView.destroy()
            }
        )
    }
}
