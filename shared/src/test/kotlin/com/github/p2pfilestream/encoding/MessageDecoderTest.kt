package com.github.p2pfilestream.encoding

import com.github.p2pfilestream.Account
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test

class MessageDecoderTest {
    @Test
    fun `Encode and decode some method call`() {
        val (mock, encoder) = getMock()
        encoder.doStuff()
        verify {
            mock.doStuff()
        }
    }

    @Test
    fun `Call method with generic argument`() {
        val (mock, encoder) = getMock()
        val accounts = listOf(Account("Hello", "World"))
        encoder.genericStuff(accounts)
        verify {
            mock.genericStuff(accounts)
        }
    }
}

private interface MyInterface {
    fun doStuff()
    fun genericStuff(accounts: List<Account>)
}

private fun getMock(): MyMock {
    val mock: MyInterface = mockk(relaxed = true)
    val decode: MessageDecoder<MyInterface> = MessageDecoder(mock)
    val encoder: MyInterface = MessageEncoder.create { decode(it) }
    return MyMock(mock, encoder)
}

private data class MyMock(
    val mock: MyInterface,
    val encoder: MyInterface
)
