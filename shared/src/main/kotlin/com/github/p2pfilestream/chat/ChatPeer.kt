package com.github.p2pfilestream.chat

interface ChatPeer {
    fun binary(binaryMessage: BinaryMessage)
    fun chunk(binaryMessageChunk: BinaryMessageChunk)
    fun text(textMessage: TextMessage)
}