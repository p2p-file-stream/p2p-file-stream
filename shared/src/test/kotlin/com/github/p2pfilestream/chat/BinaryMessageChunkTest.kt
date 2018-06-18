package com.github.p2pfilestream.chat

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.nio.ByteBuffer

class BinaryMessageChunkTest {
    @Test
    fun `Encode chunk to buffer`() {
        // Encode
        val chunk = BinaryMessageChunk(4, "Hello World".toByteArray())
        val buffer = chunk.encode(5)
            .flip() as ByteBuffer
        // Decode
        BinaryMessageChunk.decode(buffer) { messageIndex, result ->
            assertEquals(5, messageIndex)
            assertEquals(chunk, result)
        }
    }
}