package com.github.p2pfilestream.rendezvous

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.github.p2pfilestream.Account
import com.github.p2pfilestream.Device
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class SessionClientEncoderTest {
    private val encoder = SessionClientEncoder { messages.add(it) }
    private val messages = ArrayList<ByteArray>()
    private val mapper = jacksonObjectMapper()

    @Test
    fun deleteRequest() {
        val device = Device("Henk", Account("Piet", 3), 4)
        encoder.request(device)
        assertThat(messages).hasSize(1)
        println(String(messages[0]))
        val client = mockk<SessionClient>(relaxed = true)
        val decoder = MessageDecoder(client)
        decoder.decode(messages[0])
        verify { client.request(device) }
    }

    @Test
    fun request() {
    }

    @Test
    fun response() {
    }
}