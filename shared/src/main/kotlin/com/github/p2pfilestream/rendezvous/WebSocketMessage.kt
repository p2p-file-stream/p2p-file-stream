package com.github.p2pfilestream.rendezvous

data class WebSocketMessage(
    val type: String,
    val arguments: List<Any>
)