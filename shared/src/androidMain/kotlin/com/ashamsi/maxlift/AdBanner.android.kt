package com.ashamsi.maxlift

import android.content.pm.ApplicationInfo
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView

@Composable
actual fun AdBanner(modifier: Modifier) {
    AndroidView(
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp),
        factory = { context ->
            // Only debug builds are flagged debuggable; release AABs (including the
            // Play Internal track) are not, so they correctly resolve to production ads.
            val isDebug = (context.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0
            val adUnitId = AdConfig.getBannerAdUnitId(PlatformType.Android, isDebug)

            AdView(context).apply {
                this.adUnitId = adUnitId
                setAdSize(AdSize.FULL_BANNER)
                loadAd(AdRequest.Builder().build())
            }
        },
        update = { },
        onRelease = { adView ->
            adView.destroy()
        }
    )
}
