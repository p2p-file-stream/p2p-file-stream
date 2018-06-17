package com.github.p2pfilestream.client

import com.github.p2pfilestream.Device
import com.github.p2pfilestream.chat.ChatMessage
import java.time.LocalDateTime

/**
 * Gives a ChatMessage context, like an author and time received.
 */
class ReceivedChatMessage(
    val chatMessage: ChatMessage,
    val author: Device,
    val userAuthored: Boolean,
    val fileStreamProgress: FileStreamProgress? = null
) {
    val time: LocalDateTime = LocalDateTime.now()
}