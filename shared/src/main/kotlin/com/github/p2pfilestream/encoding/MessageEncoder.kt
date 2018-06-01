package com.github.p2pfilestream.encoding

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import kotlin.reflect.KFunction

abstract class MessageEncoder(private val receiver: (ByteArray) -> Unit) {
    private val mapper = jacksonObjectMapper()

    protected fun message(type: KFunction<*>, vararg elements: Any) {
        val message = WebSocketMessage(type.name, elements.asList())
        receiver(mapper.writeValueAsBytes(message))
    }
}