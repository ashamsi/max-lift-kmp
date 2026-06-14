package com.ashamsi.maxlift

/**
 * Note: These tests may fail with "Keychain error -25291" when run on certain 
 * simulator environments where the Keychain is not accessible to unit tests.
 */
class IosSecureStorageTest : SecureStorageTest() {
    override fun createStorage(): SecureStorage {
        return SecureStorageFactory().createStorage()
    }
}
