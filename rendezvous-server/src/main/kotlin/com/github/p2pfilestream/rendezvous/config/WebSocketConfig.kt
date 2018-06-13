package com.github.p2pfilestream.rendezvous.config

import com.github.p2pfilestream.rendezvous.SessionSocketHandler
import com.github.p2pfilestream.rendezvous.relay.RelaySocketHandler
import org.springframework.context.annotation.Configuration
import org.springframework.http.server.ServerHttpRequest
import org.springframework.http.server.ServerHttpResponse
import org.springframework.web.socket.WebSocketHandler
import org.springframework.web.socket.config.annotation.EnableWebSocket
import org.springframework.web.socket.config.annotation.WebSocketConfigurer
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry
import org.springframework.web.socket.server.HandshakeInterceptor
import java.lang.Exception

@Configuration
@EnableWebSocket
class WebSocketConfig(
    private val sessionSocketHandler: SessionSocketHandler,
    private val relaySocketHandler: RelaySocketHandler
) : WebSocketConfigurer {
    override fun registerWebSocketHandlers(registry: WebSocketHandlerRegistry) {
        registry.addHandler(relaySocketHandler, "/relay")
            .setAllowedOrigins("*")
            .addInterceptors(RelayInterceptor())
        registry.addHandler(sessionSocketHandler, "/session")
            .setAllowedOrigins("*")
    }

    /** Interceptor to check if Chat-Id header is present */
    private class RelayInterceptor : HandshakeInterceptor {
        override fun beforeHandshake(
            request: ServerHttpRequest,
            response: ServerHttpResponse,
            wsHandler: WebSocketHandler,
            attributes: MutableMap<String, Any>
        ): Boolean {
            // Check header
            val chatId = request.headers
                .getFirst("Chat-Id")
                ?.toLongOrNull()
            if (chatId == null) {
                return false
            }
            attributes["chatId"] = chatId
            return true
        }

        override fun afterHandshake(
            request: ServerHttpRequest,
            response: ServerHttpResponse,
            wsHandler: WebSocketHandler,
            exception: Exception?
        ) {
            // Nothing to do
        }
    }
}