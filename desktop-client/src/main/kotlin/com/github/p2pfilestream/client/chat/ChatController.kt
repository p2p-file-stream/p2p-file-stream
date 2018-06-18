package com.github.p2pfilestream.client.chat

import com.github.p2pfilestream.Device
import com.github.p2pfilestream.chat.DisconnectableChatPeer
import java.io.File

interface ChatController {
    fun sendFile(file: File)

    fun sendText(text: String)

    interface Receiver : DisconnectableChatPeer {
        /** Called after the WebSocket connection is established */
        fun connectionEstablished(
            chatPeer: DisconnectableChatPeer,
            device: Device
        )
    }
}