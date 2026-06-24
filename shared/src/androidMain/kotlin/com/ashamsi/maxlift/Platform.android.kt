package com.ashamsi.maxlift

import android.os.Build

class AndroidPlatform : Platform {
    override val name: String = "Android ${Build.VERSION.SDK_INT}"
    override val type: PlatformType = PlatformType.Android
    override val isDebug: Boolean = true // TODO: Hook into a real BuildConfig if available
}

actual fun getPlatform(): Platform = AndroidPlatform()