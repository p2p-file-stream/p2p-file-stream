package com.github.p2pfilestream.client

import com.github.p2pfilestream.Device
import com.github.p2pfilestream.client.websocket.RelayWebSocket
import com.github.p2pfilestream.client.websocket.RendezvousServer
import com.github.p2pfilestream.client.websocket.SessionWebSocket
import com.github.p2pfilestream.rendezvous.SessionClient
import com.github.p2pfilestream.rendezvous.SessionServer
import javafx.application.Platform
import javafx.scene.control.Alert
import javafx.scene.control.ButtonType
import mu.KLogging
import tornadofx.Controller
import tornadofx.alert
import tornadofx.observable

class SessionController : Controller() {
    private lateinit var sessionServer: SessionServer
    val chats = mutableListOf<Chat>().observable()
    private lateinit var rendezvousServer: RendezvousServer
    private lateinit var receiver: Receiver

    /** Maps nicknames to requests */
    //val requests = HashMap<String, Request>()

    companion object : KLogging() {}

    fun chatRequest(nickname: String) {
        sessionServer.request(nickname)
    }

    /**
     * Called when user has chosen a nickname
     */
    fun login(jwt: String) {
        logger.info { "Login nickname JWT $jwt" }
        receiver = Receiver()
        rendezvousServer = RendezvousServer(jwt)
        SessionWebSocket(receiver, rendezvousServer)
    }

    inner class Receiver : SessionClient {
        fun connectionEstablished(server: SessionServer) {
            logger.info { "Connected to Session Server" }
            sessionServer = server
        }

        fun connectionClosed() {
            Platform.runLater {
                alert(Alert.AlertType.ERROR, "SessionServer unexpectedly closed connection") {
                    SessionWebSocket(receiver, rendezvousServer)
                }
            }
        }

        override fun request(device: Device) {
            logger.info { "Got request from $device" }
            val confirm = ButtonType("Confirm")
            val decline = ButtonType("Decline")
            Platform.runLater {
                alert(
                    Alert.AlertType.CONFIRMATION,
                    "${device.nickname} requested a chat with you",
                    buttons = *arrayOf(confirm, decline)
                ) {
                    sessionServer.response(device.nickname, it == confirm)
                }
            }
        }

        override fun startChat(device: Device, chatId: Long) {
            logger.info { "Start a chat with $device; id: $chatId" }
            RelayWebSocket(chatId, rendezvousServer) {
                logger.info { "Connected to Relay Server" }
                val chat = Chat(device, it)
                chats.add(chat)
                chat.receiver
            }
        }

        override fun declined(nickname: String, error: SessionClient.ResponseError) {
            Platform.runLater {
                alert(
                    Alert.AlertType.INFORMATION,
                    "Could not connect you with $nickname",
                    error.message
                )
            }
        }

        override fun deleteRequest(nickname: String) {
            logger.info { "Delete request from ${nickname}" }
        }
    }
}

