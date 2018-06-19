package com.github.p2pfilestream.client.files

import com.github.p2pfilestream.chat.BinaryMessageChunk
import com.github.p2pfilestream.chat.FileDownloader
import io.mockk.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.io.File
import kotlin.test.assertEquals

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FileSenderTest {
    private val downloader: FileDownloader = mockk(relaxed = true)
    private val text = "Hello World, this is a test file!"
    private val file = File.createTempFile("HelloWorld", null)

    @BeforeEach
    fun setUp() {
        clearMocks(downloader)
        file.createNewFile()
        file.writeText(text)
    }

    @Test
    fun `Split file in chunks`() {
        // Start sender
        val chunkSize = 8
        val fileSender = FileSender(file, downloader, chunkSize, cloneBytes = true)
        fileSender.start()
        fileSender.joinReaderThread()
        verifySequence {
            // Verify chunks
            text.chunkedSequence(chunkSize).forEachIndexed { i, chunk ->
                downloader.chunk(BinaryMessageChunk(i, chunk.toByteArray()))
            }
            // Closing
            downloader.close()
        }
    }

    @Test
    fun `Pausing file upload`() {
        val chunkSize = 8
        val fileSender =
            FileSender(file, downloader, chunkSize, cloneBytes = true)
        // Start sender
        fileSender.start()
        // Pause
        fileSender.pause()
        // Verify pausing
        verify(inverse = true) {
            downloader.close(any())
        }
    }

    @Test
    fun `Resume after pausing`() {
        val chunkSize = 8
        val fileSender =
            FileSender(file, downloader, chunkSize, cloneBytes = true)
        // Start sender
        fileSender.start()
        // Pause
        fileSender.pause()
        fileSender.joinReaderThread()
        // Start it again
        clearMocks(downloader)
        fileSender.start()
        fileSender.joinReaderThread()
        verify {
            downloader.chunk(any())
        }
        verify(exactly = 1) {
            downloader.close()
        }
    }

    @Test
    fun `Cancel fileSender`() {
        val chunkSize = 8
        val fileSender =
            FileSender(file, downloader, chunkSize, cloneBytes = true)
        // Cancel after the 2rd chunk
        every {
            downloader.chunk(match { it.index == 1 })
        } answers {
            fileSender.cancel()
        }
        // Start sender
        fileSender.start()
        fileSender.joinReaderThread()
        // Verify canceling
        verifySequence {
            downloader.chunk(BinaryMessageChunk(0, "Hello Wo".toByteArray()))
            downloader.chunk(BinaryMessageChunk(1, "rld, thi".toByteArray()))
        }
    }

    @Test
    fun `File should be closed after reading`() {
        val fileSender = FileSender(file, downloader)
        fileSender.start()
        fileSender.joinReaderThread()
        verify { downloader.close() }
        // Test if the file is closed by trying to delete it
        file.delete()
    }

    @Test
    fun `Starting twice shouldn't throw exception, but only log a warning`() {
        val chunkSize = 5
        val fileSender =
            FileSender(file, downloader, chunkSize, cloneBytes = true)
        fileSender.start()
        fileSender.start()
        fileSender.joinReaderThread()
        verifySequence {
            // Verify chunks
            text.chunkedSequence(chunkSize).forEachIndexed { i, chunk ->
                downloader.chunk(BinaryMessageChunk(i, chunk.toByteArray()))
            }
            // Closing
            downloader.close()
        }
    }

    @Test
    fun `Progress percentage`() {
        val chunkSize = 8
        val fileSender =
            FileSender(file, downloader, chunkSize, cloneBytes = true)
        // Percentage should be 0%
        assertEquals(0.0, fileSender.progressPercentage.get())
        fileSender.start()
        fileSender.joinReaderThread()
        // Should be 100%
        assertEquals(1.0, fileSender.progressPercentage.get())
    }
}