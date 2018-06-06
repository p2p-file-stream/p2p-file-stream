package com.github.p2pfilestream.rendezvous

import com.github.p2pfilestream.chat.BinaryMessage
import com.github.p2pfilestream.chat.BinaryMessageChunk
import com.github.p2pfilestream.chat.ChatPeer

class RelayManager {
    /** Clients waiting on a match */
    val queue = HashSet<ChatPeer>()

    fun connect(client: ChatPeer): RelayServer {
        // Find match
        return RelayService()
    }

    private class RelayService : RelayServer {
        override fun id(id: Int) {
        }

        override fun binary(binaryMessage: BinaryMessage) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun chunk(binaryMessageChunk: BinaryMessageChunk) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun text(textMessage: com.github.p2pfilestream.chat.TextMessage) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }
    }
}