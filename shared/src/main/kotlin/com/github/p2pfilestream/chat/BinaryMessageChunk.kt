package com.github.p2pfilestream.chat

import java.util.*

data class BinaryMessageChunk(
    /** Identifies the message it belongs to */
    val index: Int,
    val payload: ByteArray
) {
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
}