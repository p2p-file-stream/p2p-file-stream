package com.github.p2pfilestream.client.session

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.github.p2pfilestream.Account
import com.github.p2pfilestream.Device
import com.github.p2pfilestream.client.chat.Chat
import com.github.p2pfilestream.client.websocket.RelayWebSocket
import com.github.p2pfilestream.client.websocket.RendezvousServer
import com.github.p2pfilestream.client.websocket.SessionWebSocket
import com.github.p2pfilestream.rendezvous.SessionClient
import com.github.p2pfilestream.rendezvous.SessionServer
import javafx.application.Platform
import javafx.beans.property.SimpleStringProperty
import javafx.scene.control.Alert
import javafx.scene.control.ButtonType
import mu.KLogging
import org.apache.commons.codec.binary.Base64
import tornadofx.*

class SessionController : Controller() {
    val nicknameProperty = SimpleStringProperty()
    var nickname: String by nicknameProperty
    val emailProperty = SimpleStringProperty()
    var email: String by emailProperty
    lateinit var userDevice: Device

    private lateinit var sessionServer: SessionServer

    val chats = ArrayList<Chat>().observable()

    private lateinit var rendezvousServer: RendezvousServer
    private lateinit var receiver: Receiver

    /** Maps nicknames to requests */
    //val requests = HashMap<String, Request>()

    companion object : KLogging()

    fun chatRequest(nickname: String) {
        addChat(nickname)
        sessionServer.request(nickname)
    }

    /**
     * Called when user has chosen a nickname
     */
    fun login(jwt: String) {
        parseJwt(jwt)
        logger.info { "Login nickname JWT $jwt" }
        receiver = Receiver()
        rendezvousServer = RendezvousServer(jwt)
        SessionWebSocket(receiver, rendezvousServer)
    }

    private fun parseJwt(jwt: String) {
        val payload = Base64.decodeBase64(jwt.split(".")[1])
        val jsonTree = jacksonObjectMapper().readTree(payload)
        email = jsonTree["email"].asText()
        nickname = jsonTree["sub"].asText()
        val accountId = jsonTree["account"].asText()
        userDevice = Device(nickname, Account(email, accountId))
    }

    private fun chatByNickname(nickname: String): Chat? =
        chats.find { it.peerNickname == nickname && !it.closed }

    private fun addChat(nickname: String) {
        val chat = Chat(nickname, userDevice)
        chats.add(chat)
    }

    private fun removeChat(nickname: String) {
        val chat = chatByNickname(nickname)
        if (chat != null) {
            chats.remove(chat)
        } else {
            logger.error { "Chat to remove not found" }
        }
    }

    /**
     * This class receives all the WebSocket-messages.
     */
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
                    val confirmed = it == confirm
                    if (confirmed) {
                        addChat(device.nickname)
                    }
                    sessionServer.response(device.nickname, confirmed)
                }
            }
        }

        override fun startChat(device: Device, chatId: Long) {
            val chat = chatByNickname(device.nickname)
            if (chat == null) {
                logger.error { "startChat() called, but chat does not exist" }
            } else {
                logger.info { "Start a chat with $device; id: $chatId" }
                RelayWebSocket(chatId, rendezvousServer) { other ->
                    chat.receiver.connectionEstablished(other, device)
                    chat.receiver
                }
            }
        }

        override fun declined(nickname: String, error: SessionClient.ResponseError) {
            Platform.runLater {
                removeChat(nickname)
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

