package com.github.p2pfilestream.client.views

import com.github.p2pfilestream.client.ChatModel
import javafx.beans.property.SimpleStringProperty
import tornadofx.*

class ChatView : View() {
    private val currentChat: ChatModel by inject()
    private val textMessageInput = SimpleStringProperty()
    private val controller = currentChat.controller

    override val root = vbox {
        listview(currentChat.chatMessages)
        textarea(textMessageInput) {
            promptText = "Message"
        }
        button("Send text") {
            disableProperty().bind(currentChat.empty.or(textMessageInput.isBlank()))
            shortcut("Ctrl+Enter")
            action { sendMessage() }
        }
        button("Upload file") {
            disableProperty().bind(currentChat.empty)
            action(::openFile)
        }
    }


    private fun openFile() {
        val file = chooseFile("Upload file", emptyArray())
            .firstOrNull()
        if (file != null) {
            println("You choosed: ${file.name}")
            controller?.sendFile(file)
        } else {
            println("File picker cancelled")
        }
    }

    private fun sendMessage() {
        val text = textMessageInput.value
        if (!text.isNullOrBlank()) {
            controller?.sendText(text)
            textMessageInput.value = ""
        }
    }
}