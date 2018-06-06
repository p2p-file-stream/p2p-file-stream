package com.github.p2pfilestream.encoding

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import net.sf.cglib.proxy.Enhancer
import net.sf.cglib.proxy.MethodInterceptor
import kotlin.reflect.KClass
import kotlin.reflect.jvm.javaMethod


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
        val hash = e.hashCode()
        e.setCallback(MethodInterceptor { _, method, args, _ ->
            return@MethodInterceptor when (method) {
                Any::toString.javaMethod -> "MessageEncoder proxy"
                Any::hashCode.javaMethod -> hash
                else -> callback(WebSocketMessage(method.name, args.toList()))
            }
        })
        return e.create()
    }
}