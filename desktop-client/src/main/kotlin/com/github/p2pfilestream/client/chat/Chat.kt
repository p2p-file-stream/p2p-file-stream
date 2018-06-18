package com.github.p2pfilestream.client.chat

import com.github.p2pfilestream.Device
import com.github.p2pfilestream.chat.*
import com.github.p2pfilestream.client.files.FileReceiver
import com.github.p2pfilestream.client.files.FileSender
import javafx.application.Platform
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleObjectProperty
import mu.KLogging
import tornadofx.getValue
import tornadofx.observable
import tornadofx.setValue
import java.io.File
import java.io.IOException
import java.time.LocalDateTime
import kotlin.collections.set

/**
 * Represents a chat between two devices: "user" and "peer".
 *
 * Can be initialized before cat-request is confirmed.
 */
class Chat(
    val peerNickname: String,
    private val userDevice: Device
) : ChatController {
    /** Device is only available if the chat is started */
    val peerDeviceProperty = SimpleObjectProperty<Device>()
    private var peerDevice: Device? by peerDeviceProperty
    private lateinit var chatPeer: DisconnectableChatPeer

    val start: LocalDateTime = LocalDateTime.now()

    val chatMessages = ArrayList<ReceivedChatMessage>().observable()

    val closedProperty = SimpleBooleanProperty()
    var closed by closedProperty

    /** Maps MessageIndex to FileReceivers */
    private val fileReceivers = HashMap<Int, FileReceiver>()
    /** Maps MessageIndex to FileSenders */
    private val fileSenders = HashMap<Int, FileSender>()


    private var messageCounter: Int = 1
    private fun nextMessageIndex() = messageCounter++

    private var directory: File? = null

    private companion object : KLogging()

    override fun sendFile(file: File) {
        val messageIndex = nextMessageIndex()
        val fileName = file.name
        val fileSize = file.length()
        val message = BinaryMessage(messageIndex, fileName, fileSize)
        chatPeer.binary(message)
        val sender = FileSender(file, chatPeer.downloader(messageIndex))
        fileSenders[messageIndex] = sender
        displayUserMessage(message, sender)
    }

    override fun sendText(text: String) {
        logger.info { "Send text $text" }
        val message = TextMessage(text)
        chatPeer.text(message)
        displayUserMessage(message)
    }

    private fun displayUserMessage(message: ChatMessage, fileSender: FileSender? = null) {
        chatMessages.add(
            ReceivedChatMessage(
                message,
                userDevice,
                true,
                fileSender
            )
        )
    }

    fun close() {
        chatPeer.disconnect()
    }

    /** Choose download dir */
    private fun chooseDirectory() {
        directory = tornadofx.chooseDirectory("Choose download directory")
    }

    private fun createFile(name: String): File? {
        if (directory == null) {
            chooseDirectory()
        }
        val dir = this.directory ?: return null
        try {
            if (dir.isDirectory && dir.exists()) {
                var counter = 0
                var file: File?
                // Prepend counter to filename until it doesn't exist
                do {
                    val prefix = if (counter == 0) "" else counter.toString()
                    file = dir.toPath().resolve("$prefix$name").toFile()
                    counter++
                } while (file!!.exists())
                file.createNewFile()
                return file
            }
        } catch (e: IOException) {
            logger.warn(e) { "Exception while creating file" }
        }
        return null
    }

    private fun downloader(messageIndex: Int, block: FileDownloader.() -> Unit) {
        val fileReceiver = fileReceivers[messageIndex]
                ?: return logger.warn { "Downloader for index $messageIndex not found" }
        fileReceiver.block()
    }

    private fun uploader(messageIndex: Int, block: FileUploader.() -> Unit) {
        val fileSender = fileSenders[messageIndex]
                ?: return logger.warn { "Uploader for index $messageIndex not found" }
        fileSender.block()
    }


    val receiver = object : ChatController.Receiver {
        override fun connectionEstablished(chatPeer: DisconnectableChatPeer, device: Device) {
            Platform.runLater {
                this@Chat.chatPeer = chatPeer
                this@Chat.peerDevice = device
            }
        }

        override fun disconnect(reason: String?) {
            Platform.runLater {
                logger.info { "Chat disconnected" }
                closed = true
            }
        }

        /** Receive a text-message */
        override fun text(textMessage: TextMessage) {
            Platform.runLater {
                displayRemoteMessage(textMessage)
            }
        }

        /** Receive a binary */
        override fun binary(binaryMessage: BinaryMessage) {
            Platform.runLater {
                createFile(binaryMessage.name)?.let { file ->
                    val index = binaryMessage.index
                    val fileReceiver = FileReceiver(
                        file,
                        chatPeer.uploader(index),
                        binaryMessage.size
                    )
                    fileReceivers[index] = fileReceiver
                    displayRemoteMessage(binaryMessage, fileReceiver)
                }
            }
        }

        private fun displayRemoteMessage(chatMessage: ChatMessage, fileReceiver: FileReceiver? = null) {
            chatMessages.add(
                ReceivedChatMessage(
                    chatMessage,
                    peerDevice!!,
                    false,
                    fileReceiver
                )
            )
        }

        override fun chunk(messageIndex: Int, chunk: BinaryMessageChunk) =
            downloader(messageIndex) { chunk(chunk) }

        override fun close(messageIndex: Int, cancel: Boolean) =
            downloader(messageIndex) { close(cancel) }

        override fun start(messageIndex: Int) =
            uploader(messageIndex) { start() }

        override fun pause(messageIndex: Int) =
            uploader(messageIndex) { pause() }

        override fun cancel(messageIndex: Int) =
            uploader(messageIndex) { cancel() }
    }
}
