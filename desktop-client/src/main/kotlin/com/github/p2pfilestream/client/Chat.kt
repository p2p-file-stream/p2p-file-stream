package com.github.p2pfilestream.client

import com.github.p2pfilestream.Device
import com.github.p2pfilestream.chat.BinaryMessage
import com.github.p2pfilestream.chat.BinaryMessageChunk
import com.github.p2pfilestream.chat.ChatPeer
import com.github.p2pfilestream.chat.TextMessage
import javafx.collections.FXCollections
import tornadofx.ItemViewModel
import tornadofx.property
import java.io.File
import java.time.LocalDateTime

class Chat(
    val device: Device,
    private val chatPeer: ChatPeer
) : ChatController {
    val startProperty = property(LocalDateTime.now())
    var start by startProperty
    val chatMessages = FXCollections.observableArrayList<String>()

    private val fileProcessor = FileSender(chatPeer::chunk)
    private var messageCounter: Int = 1
    private fun nextMessageIndex() = messageCounter++

    override fun sendFile(file: File) {
        val messageIndex = nextMessageIndex()
        val inputStream = file.inputStream()
        val fileSize = inputStream.channel.size()
        BinaryMessage(messageIndex, file.name, fileSize)
        fileProcessor.read(inputStream)
    }

    override fun sendText(text: String) {
        chatPeer.text(TextMessage(nextMessageIndex(), text))

    }

    val receiver = object : ChatController.Receiver {
        override fun text(textMessage: TextMessage) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun binary(binaryMessage: BinaryMessage) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun chunk(binaryMessageChunk: BinaryMessageChunk) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

    }
}

class ChatModel : ItemViewModel<Chat>() {
    val deviceProperty = bind(Chat::device)
    val chatMessages = bind(Chat::chatMessages)
    val controller: ChatController? = item
}