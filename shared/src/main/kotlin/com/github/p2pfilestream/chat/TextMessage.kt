package com.github.p2pfilestream.chat

data class TextMessage(
    override val index: Int,
    val payload: String
) : ChatMessage