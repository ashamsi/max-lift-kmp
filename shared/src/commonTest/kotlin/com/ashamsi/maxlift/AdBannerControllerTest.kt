package com.ashamsi.maxlift

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class AdBannerControllerTest {

    @Test
    fun initialStatusIsLoadingAndReservesDefaultHeight() {
        val controller = AdBannerController()

        assertEquals(AdBannerStatus.Loading, controller.status)
        assertTrue(controller.shouldReserveSpace, "Loading banner should reserve space")
        assertEquals(AdBannerPolicy.BANNER_HEIGHT_DP, controller.heightDp)
    }

    @Test
    fun loadedStatusKeepsBannerVisible() {
        val controller = AdBannerController()

        controller.onAdLoaded()

        assertEquals(AdBannerStatus.Loaded, controller.status)
        assertTrue(controller.shouldReserveSpace)
        assertEquals(AdBannerPolicy.BANNER_HEIGHT_DP, controller.heightDp)
    }

    @Test
    fun failedStatusCollapsesBanner() {
        val controller = AdBannerController()

        controller.onAdFailedToLoad()

        assertEquals(AdBannerStatus.Failed, controller.status)
        assertFalse(controller.shouldReserveSpace, "Failed banner must not occupy space")
        assertEquals(0, controller.heightDp)
    }

    @Test
    fun failureAfterSuccessfulLoadCollapsesBanner() {
        val controller = AdBannerController()

        controller.onAdLoaded()
        controller.onAdFailedToLoad()

        assertEquals(AdBannerStatus.Failed, controller.status)
        assertFalse(controller.shouldReserveSpace)
        assertEquals(0, controller.heightDp)
    }

    @Test
    fun resolvedAdaptiveHeightIsReservedWhileVisible() {
        val controller = AdBannerController()

        controller.onAdSizeResolved(62)

        assertEquals(62, controller.reservedHeightDp)
        assertEquals(62, controller.heightDp)

        controller.onAdLoaded()
        assertEquals(62, controller.heightDp)
    }

    @Test
    fun resolvedHeightStillCollapsesOnFailure() {
        val controller = AdBannerController()

        controller.onAdSizeResolved(90)
        controller.onAdFailedToLoad()

        assertEquals(90, controller.reservedHeightDp)
        assertEquals(0, controller.heightDp, "Collapsed banner has no height regardless of resolved size")
    }

    @Test
    fun nonPositiveResolvedHeightIsIgnored() {
        val controller = AdBannerController()

        controller.onAdSizeResolved(0)
        controller.onAdSizeResolved(-10)

        assertEquals(AdBannerPolicy.BANNER_HEIGHT_DP, controller.reservedHeightDp)
        assertEquals(AdBannerPolicy.BANNER_HEIGHT_DP, controller.heightDp)
    }
}

class AdBannerPolicyTest {

    @Test
    fun loadingAndLoadedReserveSpace() {
        assertTrue(AdBannerPolicy.shouldReserveSpace(AdBannerStatus.Loading))
        assertTrue(AdBannerPolicy.shouldReserveSpace(AdBannerStatus.Loaded))
    }

    @Test
    fun failedDoesNotReserveSpace() {
        assertFalse(AdBannerPolicy.shouldReserveSpace(AdBannerStatus.Failed))
    }

    @Test
    fun heightMatchesVisibility() {
        assertEquals(AdBannerPolicy.BANNER_HEIGHT_DP, AdBannerPolicy.heightDp(AdBannerStatus.Loading))
        assertEquals(AdBannerPolicy.BANNER_HEIGHT_DP, AdBannerPolicy.heightDp(AdBannerStatus.Loaded))
        assertEquals(0, AdBannerPolicy.heightDp(AdBannerStatus.Failed))
    }
}
