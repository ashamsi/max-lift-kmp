package com.ashamsi.maxlift

/**
 * Configuration for AdMob IDs.
 */
object AdConfig {
    // Replace these with your actual production IDs from AdMob dashboard
    const val ANDROID_PROD_ID = "ca-app-pub-9953397470667764/4013720012"
    const val IOS_PROD_ID = "ca-app-pub-9953397470667764/3431532017"

    // Official Google Test IDs
    const val ANDROID_TEST_ID = "ca-app-pub-3940256099942544/6300978111"
    const val IOS_TEST_ID = "ca-app-pub-3940256099942544/2934735716"

    /**
     * Returns the appropriate Ad Unit ID based on the platform and build environment.
     */
    fun getBannerAdUnitId(platformType: PlatformType, isDebug: Boolean): String {
        return when (platformType) {
            PlatformType.Android -> if (isDebug) ANDROID_TEST_ID else ANDROID_PROD_ID
            PlatformType.IOS -> if (isDebug) IOS_TEST_ID else IOS_PROD_ID
        }
    }
}
