package com.github.p2pfilestream.encoding

import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test

class MessageDecoderTest {
    @Test
    fun `Decode to private class`() {
        val mock = mockk<MyInterface>(relaxed = true)
        val decode = MessageDecoder(mock)
        val encoder = MessageEncoder.create<MyInterface> { decode(it) }
        encoder.doStuff()
        verify {
            mock.doStuff()
        }
    }

    private interface MyInterface {
        fun doStuff()
    }

    private abstract class Private : MyInterface {
        abstract override fun doStuff()
    }
}