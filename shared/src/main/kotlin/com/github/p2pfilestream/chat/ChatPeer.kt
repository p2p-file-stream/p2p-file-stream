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