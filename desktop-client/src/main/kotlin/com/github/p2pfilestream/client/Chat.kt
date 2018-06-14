package com.github.p2pfilestream.client

import com.github.p2pfilestream.Device
import com.github.p2pfilestream.chat.*
import javafx.application.Platform
import javafx.collections.FXCollections
import mu.KLogging
import tornadofx.ItemViewModel
import tornadofx.property
import java.io.File
import java.io.IOException
import java.time.LocalDateTime

class Chat(
    val device: Device,
    private val chatPeer: ChatPeer
) : ChatController {
    val startProperty = property(LocalDateTime.now())
    var start by startProperty
    val chatMessages = FXCollections.observableArrayList<String>()

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
        // todo: Show progress of uploading
    }

    override fun sendText(text: String) {
        logger.info { "Send test $text" }
        chatPeer.text(TextMessage(nextMessageIndex(), text))
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
        /** Receive a text-message */
        override fun text(textMessage: TextMessage) {
            Platform.runLater {
                chatMessages.add(textMessage.payload)
            }
        }

        /** Receive a binary */
        override fun binary(binaryMessage: BinaryMessage) {
            Platform.runLater {
                chatMessages.add(binaryMessage.toString())
                createFile(binaryMessage.name)?.let { file ->
                    val index = binaryMessage.index
                    fileReceivers[index] = FileReceiver(file, chatPeer.uploader(index))
                }
            }
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

class ChatModel : ItemViewModel<Chat>() {
    val deviceProperty = bind(Chat::device)
    val chatMessages = bind(Chat::chatMessages)
}