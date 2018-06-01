package com.github.p2pfilestream.rendezvous

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.github.p2pfilestream.Device
import kotlin.reflect.KFunction

class SessionClientEncoder(
    private val receiver: (ByteArray) -> Unit
) : SessionClient {
    private val mapper = jacksonObjectMapper()

    override fun deleteRequest(nickname: String) {
        message(::deleteRequest, nickname)
    }

    override fun request(device: Device) {
        message(::request, device)
    }

    override fun response(device: Device, success: Boolean, error: SessionClient.ResponseError) {
        message(::response, device, success, error)
    }

    private fun message(type: KFunction<*>, vararg elements: Any) {
        val message = WebSocketMessage(type.name, elements.asList())
        receiver(mapper.writeValueAsBytes(message))
    }
}