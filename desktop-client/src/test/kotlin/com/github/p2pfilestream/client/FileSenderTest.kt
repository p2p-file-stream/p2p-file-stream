package com.github.p2pfilestream.client

import com.github.p2pfilestream.chat.BinaryMessageChunk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.io.File

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FileSenderTest {
    private val fileSender = FileSender { chunks.add(it) }
    private val chunks = ArrayList<BinaryMessageChunk>()

    @Test
    fun `Split file in chunks`() {
        val file = File.createTempFile("HelloWorld", null)
        file.writeText("Hello World, this is a test file!")
        fileSender.read(0, file.inputStream())
        val result = chunks.joinToString("") { String(it.payload) }
        assertThat(result).isEqualTo("Hello World, this is a test file!")
    }
}