package com.ashamsi.maxlift

import android.content.pm.ApplicationInfo
import android.util.Log
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError

private const val AD_LOG_TAG = "MaxLiftAds"

@Composable
actual fun AdBanner(modifier: Modifier) {
    // Drives visibility from real SDK load/fail callbacks (see AdBannerController).
    val controller = remember { AdBannerController() }

    // Collapse to zero space once the ad fails so we never show an empty banner slot.
    if (!controller.shouldReserveSpace) return

    AndroidView(
        modifier = modifier
            .fillMaxWidth()
            .height(controller.heightDp.dp),
        factory = { context ->
            // Only debug builds are flagged debuggable; release AABs (including the
            // Play Internal track) are not, so they correctly resolve to production ads.
            val isDebug = (context.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0
            val adUnitId = AdConfig.getBannerAdUnitId(PlatformType.Android, isDebug)

            AdView(context).apply {
                this.adUnitId = adUnitId
                setAdSize(AdSize.BANNER)
                // Mirrors the iOS BannerViewDelegate: log the outcome and update state so
                // the banner collapses on failure.
                adListener = object : AdListener() {
                    override fun onAdLoaded() {
                        Log.i(AD_LOG_TAG, "Banner loaded. unit=$adUnitId")
                        controller.onAdLoaded()
                    }

                    override fun onAdFailedToLoad(error: LoadAdError) {
                        Log.e(AD_LOG_TAG, "Banner failed. unit=$adUnitId, error=${error.message}")
                        controller.onAdFailedToLoad()
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
