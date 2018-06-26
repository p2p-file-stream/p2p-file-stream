package com.github.p2pfilestream.encoding

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import net.sf.cglib.proxy.Enhancer
import net.sf.cglib.proxy.MethodInterceptor
import kotlin.reflect.KClass
import kotlin.reflect.jvm.javaMethod


/**
 * Translates a call on an interface to a JSON-message.
 *
 * For example, the call `doStuff("abc", 123)` will be encoded to this JSON:
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
object MessageEncoder {
    private val mapper = jacksonObjectMapper()

    inline fun <reified T : Any> create(noinline receiver: (ByteArray) -> Unit) =
        proxy(T::class, receiver) as? T ?: throw IllegalArgumentException()

    fun <T : Any> proxy(kClass: KClass<T>, receiver: (ByteArray) -> Unit) =
        Enhancer().apply {
            setInterfaces(arrayOf(kClass.java))
            setCallback(MethodInterceptor { obj, method, args, _ ->
                return@MethodInterceptor when (method) {
                    Any::toString.javaMethod -> "MessageEncoder proxy"
                    Any::hashCode.javaMethod -> hashCode()
                    Any::equals.javaMethod -> obj === args[0]
                    else -> {
                        val message = WebSocketMessage(method.name, args.toList())
                        receiver(mapper.writeValueAsBytes(message))
                    }
                }
            })
        }.create()
}