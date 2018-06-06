package com.github.p2pfilestream.rendezvous

import com.github.p2pfilestream.chat.ChatPeer
import com.github.p2pfilestream.encoding.MessageDecoder
import com.github.p2pfilestream.encoding.MessageEncoder
import org.springframework.stereotype.Component
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler

@Component
class RelaySocketHandler : TextWebSocketHandler() {
    private val decoders = HashMap<WebSocketSession, MessageDecoder<RelayServer>>()
    private val relayManager = RelayManager()

    override fun afterConnectionEstablished(session: WebSocketSession) {
        val chatPeer: ChatPeer =
            MessageEncoder.of {
                session.sendMessage(
                    TextMessage(it)
                )
            }
        val client = object : RelayClient, ChatPeer by chatPeer {
            override fun disconnect(reason: String?) {
                session.close(CloseStatus(1000, reason))
            }
        }
        val service = relayManager.connect(client)
        val messageDecoder = MessageDecoder(service)
        decoders[session] = messageDecoder
    }

    override fun handleTextMessage(session: WebSocketSession, message: TextMessage) {
        decoders[session]?.decode(message.asBytes())
    }

    override fun afterConnectionClosed(session: WebSocketSession, status: CloseStatus) {
        relayManager.disconnect()
        decoders.remove(session)
    }
}