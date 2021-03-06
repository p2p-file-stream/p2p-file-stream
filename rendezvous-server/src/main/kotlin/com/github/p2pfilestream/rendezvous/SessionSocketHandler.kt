package com.github.p2pfilestream.rendezvous

import com.github.p2pfilestream.Device
import com.github.p2pfilestream.encoding.MessageDecoder
import com.github.p2pfilestream.encoding.MessageEncoder
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.stereotype.Component
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler

@Component
class SessionSocketHandler : TextWebSocketHandler() {
    private val decoders = HashMap<WebSocketSession, MessageDecoder<SessionServer>>()
    private val sessionManager = SessionManager()

    override fun afterConnectionEstablished(session: WebSocketSession) {
        val device = getDevice(session)
        val client: SessionClient = MessageEncoder.create {
            if (session.isOpen) {
                session.sendMessage(TextMessage(it))
            }
        }
        val service = sessionManager.connect(client, device)
        val messageDecoder = MessageDecoder(service)
        decoders[session] = messageDecoder
    }

    override fun handleTextMessage(session: WebSocketSession, message: TextMessage) {
        decoders[session]?.invoke(message.asBytes())
    }

    override fun afterConnectionClosed(session: WebSocketSession, status: CloseStatus) {
        sessionManager.disconnect(getDevice(session).nickname)
        decoders.remove(session)
    }

    private fun getDevice(session: WebSocketSession): Device {
        val token = session.principal as UsernamePasswordAuthenticationToken
        return token.principal as Device
    }
}
