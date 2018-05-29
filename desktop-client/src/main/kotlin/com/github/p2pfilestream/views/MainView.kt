package com.github.p2pfilestream.views

import com.github.p2pfilestream.FileProcessor
import javafx.collections.FXCollections
import tornadofx.*

class MainView : View("P2P File Stream") {
    val sessionController: SessionController by inject()
    val chatMessages = FXCollections.observableArrayList<String>()

    override val root = borderpane {
        top {
            hbox {
                label(sessionController.nickname)
                label(sessionController.username ?: "Anonymous")
            }
        }
        center {
            vbox {
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
        }
    }

    private fun openFile() {
        val file = chooseFile("Upload file", emptyArray())
            .firstOrNull()
        if (file != null) {
            println("You choosed: ${file.name}")
            // InputStreams are used to read binaries
            val fileProcessor = FileProcessor()
            fileProcessor.readFile(file)
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

class SessionController : Controller() {
    val nickname: String = "Jan2000"
    val username: String? = "Jan Jansen"
}
