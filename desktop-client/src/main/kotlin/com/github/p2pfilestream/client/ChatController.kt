package com.github.p2pfilestream.client

import com.github.p2pfilestream.chat.ChatPeer
import java.io.File

interface ChatController {
    fun sendFile(file: File)

    fun sendText(text: String)

    interface Receiver : ChatPeer
}