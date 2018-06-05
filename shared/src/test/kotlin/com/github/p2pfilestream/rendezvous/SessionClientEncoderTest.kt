package com.github.p2pfilestream.rendezvous

import com.github.p2pfilestream.Account
import com.github.p2pfilestream.Device
import com.github.p2pfilestream.encoding.MessageDecoder
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test

class SessionClientEncoderTest {
    private val encoder = SessionClientEncoder { decoder.decode(it) }
    private val mock = mockk<SessionClient>(relaxed = true)
    private val decoder = MessageDecoder(mock)

    @Test
    fun deleteRequest() {
        encoder.deleteRequest("Henk")
        verify { mock.deleteRequest("Henk") }
    }

    @Test
    fun request() {
        val device = Device("Henk", Account("Piet", 3), 4)
        encoder.request(device)
        verify { mock.request(device) }
    }

    @Test
    fun response() {
        val device = Device("Henk", Account("Piet", 3), 4)
        encoder.confirmed(device, false, SessionClient.ResponseError.DISCONNECTED)
        verify { mock.confirmed(device, false, SessionClient.ResponseError.DISCONNECTED) }
    }
}