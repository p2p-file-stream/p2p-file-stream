package com.github.p2pfilestream.encoding

import net.sf.cglib.proxy.Enhancer
import net.sf.cglib.proxy.MethodInterceptor
import kotlin.reflect.KClass


/** Translates a call on an interface to a JSON-message */
object MessageEncoder {
    inline fun <reified T : Any> proxy(noinline callback: (WebSocketMessage) -> Unit): T {
        return Encoder(T::class, callback).proxy as T
    }

    class Encoder(
        private val kClass: KClass<out Any>,
        private val callback: (WebSocketMessage) -> Unit
    ) {
        val proxy: Any

        init {
            val e = Enhancer()
            e.setInterfaces(arrayOf(kClass.java))
            e.setCallback(MethodInterceptor { target, method, args, proxy ->
                callback(WebSocketMessage(method.name, args.toList()))
            })
            proxy = e.create()
        }
    }
}