package com.github.p2pfilestream.encoding

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import kotlin.reflect.full.declaredFunctions
import kotlin.reflect.full.valueParameters
import kotlin.reflect.jvm.javaType

/**
 * Decodes json, and calls the method on an interface.
 */
class MessageDecoder<T : Any>(
    private val receiver: T
) {
    private val types = receiver::class
        .declaredFunctions.map { it.name to it }.toMap()

    fun decode(bytes: ByteArray) {
        val mapper = jacksonObjectMapper()
        val message = mapper.readTree(bytes)
        val type = message[WebSocketMessage::type.name].asText()
        val jsonArguments = message[WebSocketMessage::arguments.name]
        // Find the method with name specified in type
        val function = types[type] ?: throw IllegalArgumentException("Type not found")
        // Parse the arguments to the right types
        val arguments = arrayOfNulls<Any?>(function.valueParameters.size)
        for ((i, parameter) in function.valueParameters.withIndex()) {
            val javaType = mapper.typeFactory.constructType(parameter.type.javaType)
            arguments[i] = mapper.readValue(mapper.treeAsTokens(jsonArguments[i]), javaType)
        }
        // Call the method
        function.call(receiver, *arguments)
    }
}