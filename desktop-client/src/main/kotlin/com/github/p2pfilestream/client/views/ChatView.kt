package com.github.p2pfilestream.client.views

import com.github.p2pfilestream.chat.BinaryMessage
import com.github.p2pfilestream.chat.TextMessage
import com.github.p2pfilestream.client.chat.ChatController
import com.github.p2pfilestream.client.chat.ChatModel
import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Pos
import javafx.scene.layout.Priority
import mu.KLogging
import tornadofx.*
import java.time.format.DateTimeFormatter

class ChatView : View() {
    private val currentChat: ChatModel by inject()
    private val textMessageInput = SimpleStringProperty()
    private val controller: ChatController?
        get() = currentChat.itemProperty.get()

    private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

    private companion object : KLogging()

    override val root = vbox {
        listview(currentChat.chatMessages) {
            vgrow = Priority.ALWAYS
            cellFormat {
                val chatMessage = it.chatMessage
                graphic = borderpane {
                    // Message header
                    top = hbox {
                        label(it.author.nickname)
                        region { hgrow = Priority.ALWAYS }
                        label(it.time.format(timeFormatter))
                    }
                    // Message body
                    center = when (chatMessage) {
                        is TextMessage -> textarea(chatMessage.payload) {
                            addClass(Styles.textMessage)
                            prefHeight = 100.0
                            isEditable = false
                        }
                        is BinaryMessage -> vbox {
                            label(chatMessage.name)
                            label(chatMessage.size.humanReadableByteCount())
                        }
                        else -> throw IllegalStateException("Type not expected")
                    }
                    // File progressProperty
                    val progress = it.fileStreamProgress
                    if (progress != null) {
                        bottom = hbox {
                            progressbar(progress.progressPercentage)
                            button("Cancel") {
                                action {
                                    progress.cancel()
                                }
                                visibleProperty().bind(progress.finishedProperty.not())
                            }
                        }
                    }
                    // Align it
                    maxWidth = 500.0
                    if (it.userAuthored) {
                        alignment = Pos.CENTER_RIGHT
                    }
                }
            }
        }
        form {
            fieldset {
                textarea(textMessageInput) {
                    addClass(Styles.textMessage)
                    promptText = "Message"
                }
                button("Send text") {
                    disableProperty().bind(currentChat.closed.or(textMessageInput.isBlank()))
                    shortcut("Ctrl+Enter")
                    action { sendMessage() }
                }
                button("Upload file") {
                    disableProperty().bind(currentChat.closed)
                    action(::openFile)
                }
            }
        }
    }


    private fun openFile() {
        val file = chooseFile("Upload file", emptyArray())
            .firstOrNull()
        if (file != null) {
            logger.info { "You chose ${file.name}" }
            controller?.sendFile(file)
        } else {
            logger.info { "File picker cancelled" }
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

/**
 * Formats byte size to human readable format
 *
 * https://programming.guide/java/formatting-byte-size-to-human-readable-format.html
 */
private fun humanReadableByteCount(bytes: Long, si: Boolean = true): String {
    val unit = if (si) 1000 else 1024
    if (bytes < unit) return bytes.toString() + " B"
    val exp = (Math.log(bytes.toDouble()) / Math.log(unit.toDouble())).toInt()
    val pre = (if (si) "kMGTPE" else "KMGTPE")[exp - 1] + if (si) "" else "i"
    return String.format("%.1f %sB", bytes / Math.pow(unit.toDouble(), exp.toDouble()), pre)
}

private fun Long.humanReadableByteCount() = humanReadableByteCount(this)