package com.github.p2pfilestream.views

import javafx.collections.FXCollections
import tornadofx.*

class ChatView : View() {
    private val currentChat: ChatModel by inject()
    private val chatController: ChatController by inject()
    private val chatMessages = FXCollections.observableArrayList<String>()

    override val root = vbox {
        label(currentChat.deviceProperty)
        listview(chatMessages)
        val messageTextArea = textarea {
            promptText = "Message"
        }
        button("Send text") {
            shortcut("Ctrl+Enter")
            action { sendMessage(messageTextArea.text) }
        }
        button("Upload file") {
            action(::openFile)
        }
    }


    private fun openFile() {
        val file = chooseFile("Upload file", emptyArray())
            .firstOrNull()
        if (file != null) {
            println("You choosed: ${file.name}")
            chatController.sendFile(file)
        } else {
            println("File picker cancelled")
        }
    }

    private fun sendMessage(text: String) {
        if (!text.isBlank()) {
            chatMessages.add(text)
        }
    }
}