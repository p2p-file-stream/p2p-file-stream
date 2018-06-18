package com.github.p2pfilestream.chat

import com.github.p2pfilestream.encoding.Disconnectable

interface ChatPeer {
    /** Send a text-message */
    fun text(textMessage: TextMessage)

    /**
     * Send a binary file (only file-name and size)
     * After this message, a number of chunks are send.
     * Finally, the file must either be closed or canceled.
     */
    fun binary(binaryMessage: BinaryMessage)

    /** Chunk of a binary */
    fun chunk(messageIndex: Int, chunk: BinaryMessageChunk)

    /**
     * Ends a file-stream, so the file can be closed.
     * @param cancel Exception occurred.
     *  Downloader must remove the partial file after closing.
     */
    fun close(messageIndex: Int, cancel: Boolean = false)

    fun start(messageIndex: Int)

    fun pause(messageIndex: Int)

    fun cancel(messageIndex: Int)
}

interface DisconnectableChatPeer : ChatPeer, Disconnectable

fun ChatPeer.onDisconnect(block: () -> Unit): DisconnectableChatPeer =
    object : DisconnectableChatPeer, ChatPeer by this, Disconnectable {
        override fun disconnect(reason: String?) {
            block()
        }
    }

fun ChatPeer.onChunk(block: (messageIndex: Int, chunk: BinaryMessageChunk) -> Unit) =
    object : ChatPeer by this {
        override fun chunk(messageIndex: Int, chunk: BinaryMessageChunk) = block(messageIndex, chunk)
    }