package com.ashamsi.maxlift

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

abstract class SecureStorageTest {
    abstract fun createStorage(): SecureStorage

    @Test
    fun testStringStorage() = runTest {
        val storage = createStorage()
        val key = "testString"
        val value = "hello world"
        
        storage.putString(key, value)
        assertEquals(value, storage.getString(key))
        assertEquals(value, storage.getStringFlow(key).first())
        
        storage.remove(key)
        assertNull(storage.getString(key))
    }

    @Test
    fun testIntStorage() = runTest {
        val storage = createStorage()
        val key = "testInt"
        val value = 42
        
        storage.putInt(key, value)
        assertEquals(value, storage.getInt(key))
        assertEquals(value, storage.getIntFlow(key).first())
    }

    @Test
    fun testBooleanStorage() = runTest {
        val storage = createStorage()
        val key = "testBool"
        
        storage.putBoolean(key, true)
        assertEquals(true, storage.getBoolean(key))
        
        storage.putBoolean(key, false)
        assertEquals(false, storage.getBoolean(key))
    }

    @Test
    fun testDoubleStorage() = runTest {
        val storage = createStorage()
        val key = "testDouble"
        val value = 3.14159
        
        storage.putDouble(key, value)
        assertEquals(value, storage.getDouble(key))
    }

    @Test
    fun testClear() = runTest {
        val storage = createStorage()
        storage.putString("key1", "val1")
        storage.putInt("key2", 2)
        
        storage.clear()
        
        assertNull(storage.getString("key1"))
        assertNull(storage.getInt("key2"))
    }
}
