package com.github.p2pfilestream.rendezvous

import com.github.p2pfilestream.chat.ChatPeer

interface RelayServer : ChatPeer {
    fun id(id: Int)
}