package com.github.p2pfilestream.client

import com.github.p2pfilestream.chat.BinaryMessageChunk
import com.github.p2pfilestream.chat.FileDownloader
import com.github.p2pfilestream.chat.FileUploader
import mu.KLogging
import java.io.File
import java.io.IOException
import java.util.concurrent.LinkedBlockingQueue
import kotlin.concurrent.thread

private const val MIN_BUFFER = 5
private const val MAX_BUFFER = 90

/** Receives chunks for a certain file */
class FileReceiver(
    private val file: File,
    private val uploader: FileUploader
) : FileDownloader {
    private val buffer = LinkedBlockingQueue<ByteArray>()
    private var paused = true
    private var chunkCount = 0
    private val writer: Thread

    private companion object : KLogging()

    init {
        writer = thread(name = "Writer for ${file.name}") { write() }
    }

    /**
     * Receive chunk of a binary
     */
    override fun chunk(chunk: BinaryMessageChunk) {
        if (chunk.index != chunkCount++) {
            logger.error { "Invalid chunk-index ${chunk.index}" }
            uploader.cancel()
            return
        }
        // put into buffer
        buffer.put(chunk.payload)
    }

    /**
     * Ends a file-stream, so the file can be closed.
     * @param cancel Exception occurred.
     *  Downloader must remove the partial file after closing.
     */
    override fun close(cancel: Boolean) {
        logger.info { "Closing file" }
        // Stop the writing thread
        writer.interrupt()
        writer.join()
        if (cancel) {
            logger.info { "Delete canceled file" }
            file.delete()
        }
    }

    private fun write() {
        val outputStream = file.outputStream()
        try {
            while (true) {
                // Start or pause based on buffer size
                val size = buffer.size
                if (paused && size < MIN_BUFFER) {
                    // Empty buffer
                    start()
                } else if (!paused && size > MAX_BUFFER) {
                    // Full buffer
                    pause()
                }
                // Write chunk to file
                outputStream.write(buffer.take())
            }
        } catch (e: IOException) {
            logger.warn(e) { "IOException while writing file" }
            uploader.cancel()
        } catch (e: InterruptedException) {
            logger.info { "Writing interrupted" }
            Thread.currentThread().interrupt()
        } finally {
            logger.info { "Closing outputStream" }
            outputStream.close()
        }
    }

    private fun pause() {
        logger.warn { "Send pause message" }
        paused = true
        uploader.pause()
    }

    private fun start() {
        logger.warn { "Send start message" }
        paused = false
        uploader.start()
    }
}