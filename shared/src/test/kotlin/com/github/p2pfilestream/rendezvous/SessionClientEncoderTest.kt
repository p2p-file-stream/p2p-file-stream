package com.github.p2pfilestream.rendezvous

import com.github.p2pfilestream.Account
import com.github.p2pfilestream.Device
import com.github.p2pfilestream.encoding.MessageDecoder
import com.github.p2pfilestream.encoding.MessageEncoder
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class SessionClientEncoderTest {
    private val encoder = MessageEncoder.create<SessionClient> { decode(it) }
    private val mock = mockk<SessionClient>(relaxed = true)
    private val decode = MessageDecoder(mock)

    @Test
    fun deleteRequest() {
        encoder.deleteRequest("Henk")
        verify { mock.deleteRequest("Henk") }
    }

    @Test
    fun request() {
        val device = Device("Henk", Account("Piet", "abcd"), 4)
        encoder.request(device)
        verify { mock.request(device) }
    }

    @Test
    fun response() {
        val device = Device("Henk", Account("Piet", "abcd"), 4)
        encoder.startChat(device, 1234)
        verify { mock.startChat(device, 1234) }
    }

    @Test
    fun `Call hashCode() on proxy`() {
        val a = MessageEncoder.create<SessionClient> { }
        val b = MessageEncoder.create<SessionClient> { }
        assertNotEquals(a.hashCode(), b.hashCode())
        assertEquals(a.hashCode(), a.hashCode())
    }

    @Test
    internal fun `Call toString() on proxy`() {
        println(encoder.toString())
    }
}