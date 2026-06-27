package com.ashamsi.maxlift

import android.os.Build

class AndroidPlatform : Platform {
    override val name: String = "Android ${Build.VERSION.SDK_INT}"
    override val type: PlatformType = PlatformType.Android
    // No Context here, so we can't read FLAG_DEBUGGABLE. Ad-unit selection does NOT
    // rely on this; AdBanner.android.kt derives debug state from the app's debuggable
    // flag at runtime. Treat this as "not debug" so nothing else defaults to test mode.
    override val isDebug: Boolean = false
}

actual fun getPlatform(): Platform = AndroidPlatform()