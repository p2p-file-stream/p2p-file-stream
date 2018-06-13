package com.github.p2pfilestream.chat

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
    fun chunk(binaryMessageChunk: BinaryMessageChunk)

    /** Close a file-stream after all the chunks are sent */
    fun close(messageIndex: Int)

    /**
     * Cancel a file-stream.
     * Downloader must remove the partial file
     */
    fun cancel(messageIndex: Int)
}