package com.github.p2pfilestream.client

import com.github.p2pfilestream.Device
import com.github.p2pfilestream.chat.ChatMessage
import java.time.LocalDateTime

class ReceivedChatMessage(
    val chatMessage: ChatMessage,
    val device: Device,
    val userAuthored: Boolean
) {
    val time = LocalDateTime.now()
}