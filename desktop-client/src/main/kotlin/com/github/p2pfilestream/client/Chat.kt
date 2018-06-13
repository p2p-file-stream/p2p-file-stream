package com.github.p2pfilestream.client

import com.github.p2pfilestream.Device
import com.github.p2pfilestream.chat.BinaryMessage
import com.github.p2pfilestream.chat.BinaryMessageChunk
import com.github.p2pfilestream.chat.ChatPeer
import com.github.p2pfilestream.chat.TextMessage
import javafx.application.Platform
import javafx.collections.FXCollections
import mu.KLogging
import tornadofx.ItemViewModel
import tornadofx.property
import java.io.File
import java.time.LocalDateTime
import kotlin.concurrent.thread

class Chat(
    val device: Device,
    private val chatPeer: ChatPeer
) : ChatController {
    val startProperty = property(LocalDateTime.now())
    var start by startProperty
    val chatMessages = FXCollections.observableArrayList<String>()

    private companion object : KLogging()

    private val fileSender = FileSender(chatPeer::chunk)
    private var messageCounter: Int = 1
    private fun nextMessageIndex() = messageCounter++

    override fun sendFile(file: File) {
        val messageIndex = nextMessageIndex()
        val fileName = file.name
        val inputStream = file.inputStream()
        val fileSize = inputStream.channel.size()
        val message = BinaryMessage(messageIndex, fileName, fileSize)
        chatPeer.binary(message)
        thread(name = "Reading binary $fileName") {
            fileSender.read(messageIndex, inputStream)
        }
    }

    override fun sendText(text: String) {
        logger.info { "Send test $text" }
        chatPeer.text(TextMessage(nextMessageIndex(), text))
    }

    val receiver = object : ChatController.Receiver {
        override fun text(textMessage: TextMessage) {
            Platform.runLater {
                chatMessages.add(textMessage.payload)
            }
        }

        override fun binary(binaryMessage: BinaryMessage) {
            Platform.runLater {
                chatMessages.add(binaryMessage.toString())
            }
        }

        override fun chunk(binaryMessageChunk: BinaryMessageChunk) {

        }

        override fun close(messageIndex: Int) {
            TODO("not implemented")
        }

        override fun cancel(messageIndex: Int) {
            TODO("not implemented")
        }
    }
}

class ChatModel : ItemViewModel<Chat>() {
    val deviceProperty = bind(Chat::device)
    val chatMessages = bind(Chat::chatMessages)
}