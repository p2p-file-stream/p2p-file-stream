package com.github.p2pfilestream.rendezvous.server

import org.springframework.stereotype.Component
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler

@Component
class SocketHandler : TextWebSocketHandler() {
    override fun afterConnectionEstablished(session: WebSocketSession) {
        println("afterConnectionEstablished")
    }

    override fun handleTextMessage(session: WebSocketSession, message: TextMessage) {
        println(message)
    }
}
