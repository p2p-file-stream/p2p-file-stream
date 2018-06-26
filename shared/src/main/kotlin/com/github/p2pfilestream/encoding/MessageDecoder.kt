package com.github.p2pfilestream.encoding

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import kotlin.reflect.full.declaredFunctions
import kotlin.reflect.full.valueParameters
import kotlin.reflect.jvm.isAccessible
import kotlin.reflect.jvm.javaType

/**
 * Decodes json, and calls the method on an interface.
 *
 * For example, this JSON would be decoded to the call `receiver.doStuff("abc", 123)`
 * ```
 * {
 *   "type": "doStuff",
 *   "arguments": [
 *      "abc",
 *      123
 *   ]
 * }
 * ```
 */
class MessageDecoder<T : Any>(
    private val receiver: T
) : (ByteArray) -> Unit {
    private val types = receiver::class
        .declaredFunctions.map { it.name to it }.toMap()

    override fun invoke(bytes: ByteArray) {
        val mapper = jacksonObjectMapper()
        // We cannot decode the arguments yet, because we don't now the types
        // So just parse it to a tree
        val message = mapper.readTree(bytes)
        val type = message[WebSocketMessage::type.name].asText()
        val jsonArguments = message[WebSocketMessage::arguments.name]
        // Find the method with name specified in type
        val function = types[type] ?: throw IllegalArgumentException("Type not found")
        // Parse the arguments to the right types
        val arguments = function.valueParameters.mapIndexed {i, parameter ->
            val javaType = mapper.typeFactory.constructType(parameter.type.javaType)
            mapper.readValue<Any?>(mapper.treeAsTokens(jsonArguments[i]), javaType)
        }
        // Call the method
        function.isAccessible = true
        function.call(receiver, *arguments.toTypedArray())
    }
}