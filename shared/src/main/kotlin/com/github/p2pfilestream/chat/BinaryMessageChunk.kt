package com.github.p2pfilestream.chat

import java.nio.ByteBuffer
import java.util.*

data class BinaryMessageChunk(
    /** Chunk counter, first chunk is 0 */
    val index: Int,
    val payload: ByteArray
) {
    fun encode(messageIndex: Int): ByteBuffer {
        return ByteBuffer.allocate(Integer.BYTES * 2 + payload.size)
            .putInt(messageIndex)
            .putInt(index)
            .put(payload)
    }

    companion object {
        inline fun decode(
            byteBuffer: ByteBuffer,
            consumer: (messageIndex: Int, chunk: BinaryMessageChunk) -> Unit
        ) {
            val messageIndex = byteBuffer.getInt()
            val index = byteBuffer.getInt()
            val payloadSize = byteBuffer.remaining()
            val payload = ByteArray(payloadSize)
            byteBuffer.get(payload)
            consumer(messageIndex, BinaryMessageChunk(index, payload))
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is BinaryMessageChunk) return false

        if (index != other.index) return false
        if (!Arrays.equals(payload, other.payload)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = index
        result = 31 * result + Arrays.hashCode(payload)
        return result
    }

    override fun toString(): String {
        return "BinaryMessageChunk(index=$index, payload=${String(payload)})"
    }
}