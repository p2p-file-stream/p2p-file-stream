package com.github.p2pfilestream.encoding

data class WebSocketMessage(
    val type: String,
    val arguments: List<Any>
)