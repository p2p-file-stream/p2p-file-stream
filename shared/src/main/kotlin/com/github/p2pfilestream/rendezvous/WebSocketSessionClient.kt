package com.github.p2pfilestream.rendezvous

import com.github.p2pfilestream.Device
import kotlin.reflect.KFunction

class WebSocketSessionClient(
    private val receiver: (WebSocketMessage) -> Unit
) : SessionClient {

    override fun request(device: Device) {
        message(::request, device)
    }

    override fun response(device: Device, success: Boolean, error: SessionClient.ResponseError) {
        message(::response, device, success, error)
    }

    fun message(type: KFunction<*>, vararg elements: Any) {
        receiver(WebSocketMessage(type.name, elements.asList()))
    }
}