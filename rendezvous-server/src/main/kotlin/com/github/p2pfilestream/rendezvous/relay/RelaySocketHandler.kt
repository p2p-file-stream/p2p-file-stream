package com.github.p2pfilestream.rendezvous.relay

import mu.KLogging
import org.springframework.stereotype.Component
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler

@Component
class RelaySocketHandler : TextWebSocketHandler() {
    private val sessions = PairSet<WebSocketSession>()

    /** Clients waiting on a match, keys are chat-ids */
    private val queue = HashMap<Long, WebSocketSession>();

    private companion object : KLogging()

    override fun afterConnectionEstablished(session: WebSocketSession) {
        val chatId = getChatId(session)
        val match = queue.remove(chatId)
        if (match == null) {
            // No match found -> put into queue
            queue[chatId] = session
            logger.info { "New client in queue, chatId: $chatId" }
        } else {
            // Match found -> connect them
            sessions.pair(session, match)
            logger.info { "Found a match, chatId: $chatId" }
        }
    }

    /** Relay text-message to other client */
    override fun handleTextMessage(session: WebSocketSession, message: TextMessage) {
        sessions.other(session)?.sendIfOpen(message)
                ?: logger.error { "Client sent message, but other was not yet connected" }
    }

    override fun afterConnectionClosed(session: WebSocketSession, status: CloseStatus) {
        val other = sessions.remove(session)
        if (other != null) {
            // Close session of other member
            other.close()
        } else {
            // Not matched, so could still be in queue
            // Remove from queue
            queue.remove(getChatId(session))
        }
    }

    private fun getChatId(session: WebSocketSession): Long {
        return session.attributes["chatId"] as Long
    }

    private fun WebSocketSession.sendIfOpen(message: TextMessage) {
        if (this.isOpen) {
            this.sendMessage(message)
        }
    }
}