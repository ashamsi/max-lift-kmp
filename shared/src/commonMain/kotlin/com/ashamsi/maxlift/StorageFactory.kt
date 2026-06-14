package com.ashamsi.maxlift

import kotlinx.coroutines.flow.Flow

interface SecureStorage {
    suspend fun putString(key: String, value: String)
    suspend fun getString(key: String): String?
    fun getStringFlow(key: String): Flow<String?>

    suspend fun putInt(key: String, value: Int)
    suspend fun getInt(key: String): Int?
    fun getIntFlow(key: String): Flow<Int?>

    suspend fun putLong(key: String, value: Long)
    suspend fun getLong(key: String): Long?
    fun getLongFlow(key: String): Flow<Long?>

    suspend fun putBoolean(key: String, value: Boolean)
    suspend fun getBoolean(key: String): Boolean?
    fun getBooleanFlow(key: String): Flow<Boolean?>

    suspend fun putDouble(key: String, value: Double)
    suspend fun getDouble(key: String): Double?
    fun getDoubleFlow(key: String): Flow<Double?>

    suspend fun remove(key: String)
    suspend fun clear()
}

expect class SecureStorageFactory(context: Any? = null) {
    fun createStorage(): SecureStorage
}
