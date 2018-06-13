package com.github.p2pfilestream.chat

interface FileDownloader {
    /** Chunk of a binary */
    fun chunk(chunk: BinaryMessageChunk)

    /**
     * Ends a file-stream, so the file can be closed.
     * @param cancel Exception occurred.
     *  Downloader must remove the partial file after closing.
     */
    fun close(cancel: Boolean = false)
}

fun ChatPeer.downloader(messageIndex: Int) = object : FileDownloader {
    override fun chunk(chunk: BinaryMessageChunk) = chunk(messageIndex, chunk)
    override fun close(cancel: Boolean) = close(messageIndex, cancel)
}
