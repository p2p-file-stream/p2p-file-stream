package com.github.p2pfilestream.chat

interface FileUploader {
    /** Start or resume sending chunks */
    fun start()

    /** Pause sending chunks because of full buffer (backpressure). */
    fun pause()

    /** Cancel the upload */
    fun cancel()
}

fun ChatPeer.uploader(messageIndex: Int) = object : FileUploader {
    override fun start() = start(messageIndex)
    override fun pause() = pause(messageIndex)
    override fun cancel() = cancel(messageIndex)
}