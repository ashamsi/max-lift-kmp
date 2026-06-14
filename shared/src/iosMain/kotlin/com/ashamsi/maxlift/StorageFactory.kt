package com.ashamsi.maxlift

import com.russhwolf.settings.ExperimentalSettingsImplementation
import com.russhwolf.settings.KeychainSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

@OptIn(ExperimentalSettingsImplementation::class)
class IosSecureStorage : SecureStorage {
    // KeychainSettings (part of the multiplatform-settings library) is a wrapper around the iOS
    // Keychain Services API. This is the most secure way to store small pieces of sensitive data
    // on Apple devices.
    private val settings = KeychainSettings(service = "com.ashamsi.maxlift.MaxLift")

    override suspend fun putString(key: String, value: String) = settings.putString(key, value)
    override suspend fun getString(key: String): String? = settings.getStringOrNull(key)
    override fun getStringFlow(key: String): Flow<String?> = flow { emit(settings.getStringOrNull(key)) }

    override suspend fun putInt(key: String, value: Int) = settings.putInt(key, value)
    override suspend fun getInt(key: String): Int? = settings.getIntOrNull(key)
    override fun getIntFlow(key: String): Flow<Int?> = flow { emit(settings.getIntOrNull(key)) }

    override suspend fun putLong(key: String, value: Long) = settings.putLong(key, value)
    override suspend fun getLong(key: String): Long? = settings.getLongOrNull(key)
    override fun getLongFlow(key: String): Flow<Long?> = flow { emit(settings.getLongOrNull(key)) }

    override suspend fun putBoolean(key: String, value: Boolean) = settings.putBoolean(key, value)
    override suspend fun getBoolean(key: String): Boolean? = settings.getBooleanOrNull(key)
    override fun getBooleanFlow(key: String): Flow<Boolean?> = flow { emit(settings.getBooleanOrNull(key)) }

    override suspend fun putDouble(key: String, value: Double) = settings.putDouble(key, value)
    override suspend fun getDouble(key: String): Double? = settings.getDoubleOrNull(key)
    override fun getDoubleFlow(key: String): Flow<Double?> = flow { emit(settings.getDoubleOrNull(key)) }

    override suspend fun remove(key: String) = settings.remove(key)
    override suspend fun clear() = settings.clear()
}

actual class SecureStorageFactory actual constructor(context: Any?) {
    actual fun createStorage(): SecureStorage {
        return IosSecureStorage()
    }
}
