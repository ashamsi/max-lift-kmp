package com.ashamsi.maxlift

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
    val platform = getPlatform()
    val adUnitId = AdConfig.getBannerAdUnitId(platform.type, platform.isDebug)

    AndroidView(
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp),
        factory = { context ->
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
