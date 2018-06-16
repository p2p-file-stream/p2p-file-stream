package com.github.p2pfilestream.client.websocket

import org.springframework.web.socket.WebSocketHandler
import org.springframework.web.socket.client.WebSocketConnectionManager
import org.springframework.web.socket.client.standard.StandardWebSocketClient

private val rendezvousUrl = System.getProperty(
    "p2pfilestream.rendezvousUrl",
    "wss://p2p-file-stream-rendezvous.herokuapp.com"
)

class RendezvousServer(
    private val jwt: String
) {
    fun connect(path: String, handler: WebSocketHandler): WebSocketConnectionManager {
        val client = StandardWebSocketClient()
        val url = rendezvousUrl + path
        val manager = WebSocketConnectionManager(client, handler, url)
        manager.headers.add("Authorization", "Bearer $jwt")
        return manager
    }
}