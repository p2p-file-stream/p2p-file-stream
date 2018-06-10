package com.github.p2pfilestream.client.websocket

import com.github.p2pfilestream.client.SessionController
import com.github.p2pfilestream.encoding.MessageDecoder
import com.github.p2pfilestream.encoding.MessageEncoder
import com.github.p2pfilestream.rendezvous.SessionClient
import com.github.p2pfilestream.rendezvous.SessionServer
import mu.KLogging
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.client.WebSocketConnectionManager
import org.springframework.web.socket.handler.TextWebSocketHandler

class SessionWebSocket(
    private val sessionClient: SessionController.Receiver,
    rendezvousServer: RendezvousServer
) : TextWebSocketHandler() {
    companion object : KLogging()

    private lateinit var session: WebSocketSession
    private val manager: WebSocketConnectionManager
    private val decode = MessageDecoder<SessionClient>(sessionClient)

    init {
        manager = rendezvousServer.connect("session", this)
        manager.start()
        logger.info { "Connecting to WebSocket" }
    }

    override fun afterConnectionEstablished(session: WebSocketSession) {
        logger.info { "WebSocket connection established" }
        this.session = session
        val sessionServer: SessionServer = MessageEncoder.create {
            if (session.isOpen) {
                session.sendMessage(TextMessage(it))
            }
        }
        sessionClient.connectionEstablished(sessionServer)
    }

    override fun handleTextMessage(session: WebSocketSession, message: TextMessage) {
        val bytes = message.asBytes()
        decode(bytes)
    }

    override fun afterConnectionClosed(session: WebSocketSession, status: CloseStatus) {
        logger.info { "WebSocket disconnected, status: $status" }
        // todo: Notify user
    }

    fun disconnect() {
        manager.stop()
    }
}