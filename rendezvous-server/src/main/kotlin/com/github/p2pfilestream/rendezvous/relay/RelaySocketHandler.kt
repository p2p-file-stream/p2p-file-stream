package com.github.p2pfilestream.rendezvous.relay

import mu.KLogging
import org.springframework.stereotype.Component
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler

@Component
class RelaySocketHandler : TextWebSocketHandler() {
    private val decoders = HashMap<WebSocketSession, (ByteArray) -> Unit>()

    /** Clients waiting on a match, keys are chat-ids */
    private val queue = HashMap<Long, WebSocketSession>();

    private companion object : KLogging()

    override fun afterConnectionEstablished(session: WebSocketSession) {
        val chatId = getChatId(session)
        val match = queue[chatId]
        if (match == null) {
            // No match found -> put into queue
            queue[chatId] = session
            logger.info { "New client in queue, chatId: $chatId" }
        } else {
            // Match found -> connect them
            decoders[session] = { match.sendBytes(it) }
            decoders[match] = { session.sendBytes(it) }
            logger.info { "Found a match, chatId: $chatId" }
        }
    }

    override fun handleTextMessage(session: WebSocketSession, message: TextMessage) {
        decoders[session]?.invoke(message.asBytes())
    }

    override fun afterConnectionClosed(session: WebSocketSession, status: CloseStatus) {
        decoders.remove(session)
    }

    private fun getChatId(session: WebSocketSession): Long {
        return session.attributes["chatId"] as Long
    }

    private fun WebSocketSession.sendBytes(byteArray: ByteArray) {
        if (this.isOpen) {
            this.sendMessage(TextMessage(byteArray))
        }
    }
}