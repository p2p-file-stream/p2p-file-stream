package com.github.p2pfilestream.client

import com.github.p2pfilestream.chat.BinaryMessageChunk
import java.io.InputStream

private const val CHUNK_SIZE = 8

class FileSender(
    private val chunkConsumer: (BinaryMessageChunk) -> Unit
) {
    /** Read file and parse into message-chunk
     */
    fun read(messageIndex: Int, inputStream: InputStream) {
        val data = ByteArray(CHUNK_SIZE)
        var bytesRead = inputStream.read(data)
        while (bytesRead != -1) {
            val chunkBytes = if (bytesRead == CHUNK_SIZE) {
                data
            } else {
                data.take(bytesRead).toByteArray()
            }
            chunkConsumer(BinaryMessageChunk(messageIndex, chunkBytes.clone()))
            bytesRead = inputStream.read(data)
        }
        inputStream.close()
    }
}