package com.github.p2pfilestream.client.dal

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * Integration test for DeviceStore
 */
class PreferencesDeviceStoreTestIt {
    private val deviceStore: DeviceStore = PreferencesDeviceStore()

    @BeforeEach
    fun setUp() {
        val value = deviceStore.get()
        println("Value before: $value")
    }

    @Test
    fun `Save and get token`() {
        val token = "myToken"
        deviceStore.save(token)
        val result = deviceStore.get()
        assertEquals(token, result)
    }

    @Test
    fun `Remove token`() {
        deviceStore.save("This should be removed")
        deviceStore.remove()
        assertNull(deviceStore.get())
    }
}