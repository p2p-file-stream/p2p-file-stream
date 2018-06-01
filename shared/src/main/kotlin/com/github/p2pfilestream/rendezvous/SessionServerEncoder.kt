package com.github.p2pfilestream.rendezvous

import com.github.p2pfilestream.encoding.MessageEncoder

class SessionServerEncoder(private val receiver: (ByteArray) -> Unit) : MessageEncoder(receiver),
    SessionServer {
    override fun request(nickname: String) {
        message(::request, nickname)
    }

    override fun response(nickname: String, confirm: Boolean) {
        message(::response, nickname, confirm)
    }
}