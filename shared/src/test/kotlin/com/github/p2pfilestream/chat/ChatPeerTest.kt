package com.github.p2pfilestream.chat

import com.github.p2pfilestream.encoding.MessageEncoder
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test

class ChatPeerTest {
    private val encoder = MessageEncoder.create<ChatPeer> { mock(String(it)) }
    private val mock: (String) -> Unit = mockk(relaxed = true)

    @Test
    fun `Encode binary chunk`() {
        // Verify if MessageEncoder encodes an ByteArray using Base64Url encoding
        encoder.chunk(1, BinaryMessageChunk(2, "Hello World".toByteArray()))
        verify {
            mock("""{"type":"chunk","arguments":[1,{"index":2,"payload":"SGVsbG8gV29ybGQ="}]}""")
        }
    }
}