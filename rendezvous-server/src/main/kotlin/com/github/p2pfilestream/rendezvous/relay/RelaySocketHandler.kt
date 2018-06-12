package com.github.p2pfilestream.rendezvous.relay

import com.github.p2pfilestream.chat.ChatPeer
import com.github.p2pfilestream.encoding.MessageDecoder
import com.github.p2pfilestream.encoding.MessageEncoder
import org.springframework.stereotype.Component
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler

@Component
class RelaySocketHandler : TextWebSocketHandler(), Relayer {
    private val decoders = HashMap<WebSocketSession, MessageDecoder<*>>()
    private val waitingClients = HashMap<WebSocketSession, RelayClient>()
    private val relayManager by lazy { RelayManager(this, 60) }

    override fun afterConnectionEstablished(session: WebSocketSession) {
        val chatPeer: ChatPeer =
            MessageEncoder.create {
                session.sendMessage(
                    TextMessage(it)
                )
            }
        val client = object : RelayClient, ChatPeer by chatPeer {
            override fun disconnect(reason: String?) {
                session.close(CloseStatus(1000, reason))
            }
        }
        val chatId = session.handshakeHeaders.getFirst("Chat-Id")?.toLong()
        if (chatId == null) {
            return
        }
        val service = relayManager.connect(client, chatId)
        val messageDecoder = MessageDecoder(service)
        decoders[session] = messageDecoder
        waitingClients[session] = client
    }

    override fun relay(a: RelayClient, b: RelayClient) {
        val sessionA = detachClient(a)
        val sessionB = detachClient(b)
        if (sessionA == null || sessionB == null) {
            // Disconnect them
            a.disconnect()
            b.disconnect()
            return
        }
        decoders[sessionA] = MessageDecoder(b)
        decoders[sessionB] = MessageDecoder(a)
    }

    /**
     * Unfortunately there are no bidirectional hashmaps in Java or Kotlin.
     */
    private fun detachClient(client: RelayClient): WebSocketSession? {
        val session = waitingClients.entries.firstOrNull { it.value == client }?.key
        if (session != null) {
            waitingClients.remove(session)
        }
        return session
    }

    override fun handleTextMessage(session: WebSocketSession, message: TextMessage) {
        decoders[session]?.invoke(message.asBytes())
    }

    override fun afterConnectionClosed(session: WebSocketSession, status: CloseStatus) {
        decoders.remove(session)
        waitingClients.remove(session)?.let(relayManager::disconnect)
    }
}