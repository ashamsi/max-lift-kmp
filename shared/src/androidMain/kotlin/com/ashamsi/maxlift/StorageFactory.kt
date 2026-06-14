package com.ashamsi.maxlift

import android.content.Context
import android.util.Base64
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStoreFile
import com.google.crypto.tink.Aead
import com.google.crypto.tink.KeyTemplates
import com.google.crypto.tink.aead.AeadConfig
import com.google.crypto.tink.integration.android.AndroidKeysetManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class AndroidSecureStorage(
    private val context: Context,
    private val aead: Aead
) : SecureStorage {
    private val dataStore = PreferenceDataStoreFactory.create {
        context.preferencesDataStoreFile("secure_storage")
    }

    private fun encrypt(value: String): String {
        val encrypted = aead.encrypt(value.toByteArray(), null)
        return Base64.encodeToString(encrypted, Base64.DEFAULT)
    }

    private fun decrypt(value: String): String {
        val decoded = Base64.decode(value, Base64.DEFAULT)
        return String(aead.decrypt(decoded, null))
    }

    private suspend fun putEncrypted(key: String, value: String) {
        dataStore.edit { preferences ->
            preferences[stringPreferencesKey(key)] = encrypt(value)
        }
    }

    private suspend fun getDecrypted(key: String): String? {
        return try {
            dataStore.data.map { preferences ->
                preferences[stringPreferencesKey(key)]?.let { decrypt(it) }
            }.first()
        } catch (_: Exception) {
            null
        }
    }

    private fun getDecryptedFlow(key: String): Flow<String?> {
        return dataStore.data.map { preferences ->
            preferences[stringPreferencesKey(key)]?.let {
                try { decrypt(it) } catch (_: Exception) { null }
            }
        }
    }

    override suspend fun putString(key: String, value: String) = putEncrypted(key, value)
    override suspend fun getString(key: String): String? = getDecrypted(key)
    override fun getStringFlow(key: String): Flow<String?> = getDecryptedFlow(key)

    override suspend fun putInt(key: String, value: Int) = putEncrypted(key, value.toString())
    override suspend fun getInt(key: String): Int? = getDecrypted(key)?.toIntOrNull()
    override fun getIntFlow(key: String): Flow<Int?> = getDecryptedFlow(key).map { it?.toIntOrNull() }

    override suspend fun putLong(key: String, value: Long) = putEncrypted(key, value.toString())
    override suspend fun getLong(key: String): Long? = getDecrypted(key)?.toLongOrNull()
    override fun getLongFlow(key: String): Flow<Long?> = getDecryptedFlow(key).map { it?.toLongOrNull() }

    override suspend fun putBoolean(key: String, value: Boolean) = putEncrypted(key, value.toString())
    override suspend fun getBoolean(key: String): Boolean? = getDecrypted(key)?.toBooleanStrictOrNull()
    override fun getBooleanFlow(key: String): Flow<Boolean?> = getDecryptedFlow(key).map { it?.toBooleanStrictOrNull() }

    override suspend fun putDouble(key: String, value: Double) = putEncrypted(key, value.toString())
    override suspend fun getDouble(key: String): Double? = getDecrypted(key)?.toDoubleOrNull()
    override fun getDoubleFlow(key: String): Flow<Double?> = getDecryptedFlow(key).map { it?.toDoubleOrNull() }

    override suspend fun remove(key: String) {
        dataStore.edit { it.remove(stringPreferencesKey(key)) }
    }

    override suspend fun clear() {
        dataStore.edit { it.clear() }
    }
}

//The specific "key" inside maxlift_crypto_prefs preference file where the keyset data is stored.
private const val KEYSET_NAME = "maxlift_keyset"

//The name of the internal Android SharedPreferences file where Tink will store your encrypted key.
private const val PREF_FILE_NAME = "maxlift_crypto_prefs"

// The most critical part. The android-keystore:// prefix tells Tink to use the Android Keystore
// System (a hardware-backed vault) to protect your master key. tink_master_key is the alias for
// that key.
private const val MASTER_KEY_URI = "android-keystore://maxlift_master_key"

/**
 * 1. Vault Access: Tink looks into the Android Keystore for a master key named tink_master_key. If
 * it doesn't exist, the hardware creates a new one that cannot be extracted from the phone.
 * 2. Keyset Management: Tink generates a "working key" (the keyset) used to encrypt your actual
 * data. It encrypts this keyset with the master key and saves the result in tink_pref.
 * 3. Encryption: When you call storage.putString(), Tink uses the aead primitive to turn your text \
 * into garbage data using that keyset.
 */
actual class SecureStorageFactory actual constructor(context: Any?) {
    private val appContext = context as Context

    actual fun createStorage(): SecureStorage {
        // This initializes the library and registers AEAD (Authenticated Encryption with Associated
        // Data). AEAD is the "gold standard" for mobile apps because it ensures your data is not
        // only secret (confidentiality) but also hasn't been tampered with (integrity).
        AeadConfig.register()
        val keysetHandle = AndroidKeysetManager.Builder()
            .withSharedPref(appContext, KEYSET_NAME, PREF_FILE_NAME)
            .withKeyTemplate(KeyTemplates.get("AES256_GCM"))
            .withMasterKeyUri(MASTER_KEY_URI)
            .build()
            .keysetHandle

        val aead = keysetHandle.getPrimitive(Aead::class.java)
        return AndroidSecureStorage(appContext, aead)
    }
}
