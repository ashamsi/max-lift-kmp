package com.ashamsi.maxlift

import androidx.test.core.app.ApplicationProvider
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class AndroidSecureStorageTest : SecureStorageTest() {
    override fun createStorage(): SecureStorage {
        return SecureStorageFactory(ApplicationProvider.getApplicationContext()).createStorage()
    }
}
