package com.github.p2pfilestream.encoding

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import kotlin.reflect.full.declaredFunctions
import kotlin.reflect.full.valueParameters
import kotlin.reflect.jvm.javaType

class MessageDecoder<T : Any>(
    private val receiver: T
) {
    private val types = receiver::class
        .declaredFunctions.map { it.name to it }.toMap()

    fun decode(bytes: ByteArray) {
        val mapper = jacksonObjectMapper()
        val message = mapper.readTree(bytes)
        val type = message[WebSocketMessage::type.name].asText()
        val jsonArguments = message[WebSocketMessage::arguments.name];
        val function = types[type] ?: throw IllegalArgumentException("Type not found")
        val arguments = arrayOfNulls<Any?>(function.valueParameters.size)
        for ((i, parameter) in function.valueParameters.withIndex()) {
            val javaType = mapper.typeFactory.constructType(parameter.type.javaType)
            arguments[i] = mapper.readValue(mapper.treeAsTokens(jsonArguments[i]), javaType)
        }
        function.call(receiver, *arguments)
    }
}