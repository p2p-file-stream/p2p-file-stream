package com.github.p2pfilestream.rendezvous.config

import com.github.p2pfilestream.rendezvous.SessionSocketHandler
import com.github.p2pfilestream.rendezvous.relay.RelaySocketHandler
import org.springframework.context.annotation.Configuration
import org.springframework.web.socket.config.annotation.EnableWebSocket
import org.springframework.web.socket.config.annotation.WebSocketConfigurer
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry

@Configuration
@EnableWebSocket
class WebSocketConfig(
    private val sessionSocketHandler: SessionSocketHandler,
    private val relaySocketHandler: RelaySocketHandler
) : WebSocketConfigurer {
    override fun registerWebSocketHandlers(registry: WebSocketHandlerRegistry) {
        registry.addHandler(relaySocketHandler, "/relay")
            .setAllowedOrigins("*")
        registry.addHandler(sessionSocketHandler, "/session")
            .setAllowedOrigins("*")
        //.addInterceptors(HttpSessionHandshakeInterceptor())
    }
}