package com.github.p2pfilestream.encoding

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import net.sf.cglib.proxy.Enhancer
import net.sf.cglib.proxy.MethodInterceptor
import kotlin.reflect.KClass
import kotlin.reflect.jvm.javaMethod


/** Translates a call on an interface to a JSON-message */
object MessageEncoder {
    val mapper = jacksonObjectMapper()

    inline fun <reified T : Any> create(noinline receiver: (ByteArray) -> Unit): T {
        val implementation = proxy(T::class) {
            receiver(mapper.writeValueAsBytes(it))
        }
        if (implementation !is T) {
            throw IllegalArgumentException()
        }
        return implementation
    }

    fun <T : Any> proxy(kClass: KClass<T>, receiver: (WebSocketMessage) -> Unit): Any {
        val e = Enhancer()
        e.setInterfaces(arrayOf(kClass.java))
        val hash = e.hashCode()
        e.setCallback(MethodInterceptor { obj, method, args, _ ->
            return@MethodInterceptor when (method) {
                Any::toString.javaMethod -> "MessageEncoder proxy"
                Any::hashCode.javaMethod -> hash
                Any::equals.javaMethod -> obj === args[0]
                else -> receiver(WebSocketMessage(method.name, args.toList()))
            }
        })
        return e.create()
    }
}