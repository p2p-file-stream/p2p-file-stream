package com.github.p2pfilestream.encoding

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import net.sf.cglib.proxy.Enhancer
import net.sf.cglib.proxy.MethodInterceptor
import kotlin.reflect.KClass


/** Translates a call on an interface to a JSON-message */
object MessageEncoder {
    val mapper = jacksonObjectMapper()

    inline fun <reified T : Any> create(noinline callback: (ByteArray) -> Unit): T {
        val implementation = proxy(T::class) {
            callback(mapper.writeValueAsBytes(it))
        }
        if (implementation !is T) {
            throw IllegalArgumentException()
        }
        return implementation
    }

    fun <T : Any> proxy(kClass: KClass<T>, callback: (WebSocketMessage) -> Unit): Any {
        val e = Enhancer()
        e.setInterfaces(arrayOf(kClass.java))
        e.setCallback(MethodInterceptor { target, method, args, proxy ->
            callback(WebSocketMessage(method.name, args.toList()))
        })
        return e.create()
    }
}