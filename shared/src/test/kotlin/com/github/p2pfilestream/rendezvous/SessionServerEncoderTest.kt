package com.github.p2pfilestream.rendezvous

import com.github.p2pfilestream.encoding.MessageDecoder
import com.github.p2pfilestream.encoding.MessageEncoder
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test

class SessionServerEncoderTest {
    private val encoder = MessageEncoder.of<SessionServer> { decoder.decode(it) }
    private val mock = mockk<SessionServer>(relaxed = true)
    private val decoder = MessageDecoder(mock)

    @Test
    fun request() {
        encoder.request("Henk")
        verify { mock.request("Henk") }
    }

    @Test
    fun response() {
        encoder.response("Henk", true)
        verify { mock.response("Henk", true) }
    }
}