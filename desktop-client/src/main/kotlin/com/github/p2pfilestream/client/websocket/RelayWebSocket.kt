package com.github.p2pfilestream.client.websocket

import com.github.p2pfilestream.chat.ChatPeer
import com.github.p2pfilestream.chat.DisconnectableChatPeer
import com.github.p2pfilestream.chat.onDisconnect
import com.github.p2pfilestream.encoding.MessageDecoder
import com.github.p2pfilestream.encoding.MessageEncoder
import mu.KLogging
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.client.WebSocketConnectionManager
import org.springframework.web.socket.handler.TextWebSocketHandler

class RelayWebSocket(
    chatId: Long,
    rendezvousServer: RendezvousServer,
    private val connected: (DisconnectableChatPeer) -> DisconnectableChatPeer
) : TextWebSocketHandler() {
    companion object : KLogging()

    private val manager: WebSocketConnectionManager
    private lateinit var session: WebSocketSession
    private lateinit var decode: MessageDecoder<ChatPeer>
    private lateinit var chatClient: DisconnectableChatPeer

    init {
        manager = rendezvousServer.connect("/relay", this)
        manager.headers["Chat-Id"] = chatId.toString()
        manager.start()
        SessionWebSocket.logger.info { "Connecting to WebSocket" }
    }

    override fun afterConnectionEstablished(session: WebSocketSession) {
        logger.info { "WebSocket connection established: ${session.uri}" }
        this.session = session
        val other = MessageEncoder.create<ChatPeer> {
            if (session.isOpen) {
                session.sendMessage(TextMessage(it))
            }
        }.onDisconnect {
            manager.stop()
        }
        chatClient = connected(other)
        decode = MessageDecoder<ChatPeer>(chatClient)
    }

    override fun handleTextMessage(session: WebSocketSession, message: TextMessage) {
        val bytes = message.asBytes()
        decode(bytes)
    }

    override fun afterConnectionClosed(session: WebSocketSession, status: CloseStatus) {
        logger.info { "WebSocket disconnected, status: $status" }
        chatClient.disconnect(status.reason)
    }
}