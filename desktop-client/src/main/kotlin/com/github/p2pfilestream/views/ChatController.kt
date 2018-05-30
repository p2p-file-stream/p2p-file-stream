package com.github.p2pfilestream.views

import com.github.p2pfilestream.FileSender
import com.github.p2pfilestream.chat.BinaryMessage
import com.github.p2pfilestream.chat.ChatPeer
import com.github.p2pfilestream.chat.TextMessage
import tornadofx.Controller
import java.io.File

/**
 * Sends messages
 */
class ChatController(
    private val chatPeer: ChatPeer
) : Controller() {
    private val fileProcessor = FileSender(chatPeer::chunk)
    private var messageCounter: Int = 1
    private fun nextMessageIndex() = messageCounter++

    fun sendFile(file: File) {
        val messageIndex = nextMessageIndex()
        val inputStream = file.inputStream()
        val fileSize = inputStream.channel.size()
        BinaryMessage(messageIndex, file.name, fileSize)
        fileProcessor.read(inputStream)
    }

    fun sendText(text: String) {
        chatPeer.text(TextMessage(nextMessageIndex(), text))
    }
}