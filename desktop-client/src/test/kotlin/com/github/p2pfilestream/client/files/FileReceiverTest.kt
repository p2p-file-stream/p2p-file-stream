package com.github.p2pfilestream.client.files

import com.github.p2pfilestream.chat.BinaryMessageChunk
import com.github.p2pfilestream.chat.FileUploader
import io.mockk.clearMocks
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.io.File

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FileReceiverTest {
    private val uploader: FileUploader = mockk(relaxed = true)
    private val file = File.createTempFile("HelloWorld", null)

    @BeforeEach
    fun setUp() {
        clearMocks(uploader)
        // Empty the file
        file.createNewFile()
        file.writeBytes(byteArrayOf())
    }

    @Test
    fun `Should emit start`() {
        val fileReceiver = FileReceiver(file, uploader, 0)
        Thread.sleep(500)
        verify {
            uploader.start()
        }
        fileReceiver.close(true)
    }

    @Test
    fun `Write chunks to file`() {
        val bytes = "Hello World, this is a test file!".toByteArray()
        val fileReceiver = FileReceiver(file, uploader, bytes.size.toLong())
        // Send chunks
        bytes.asList().chunked(8).forEachIndexed { index, chunkBytes ->
            fileReceiver.chunk(BinaryMessageChunk(index, chunkBytes.toByteArray()))
        }
        // Let fileReceiver process them
        Thread.sleep(500)
        fileReceiver.close()
        // Check contents of file
        assertThat(bytes).isEqualTo(bytes)
    }
}