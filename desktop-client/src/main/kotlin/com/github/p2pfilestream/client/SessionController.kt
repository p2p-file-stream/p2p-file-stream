package com.github.p2pfilestream.client

import com.github.p2pfilestream.Device
import com.github.p2pfilestream.client.websocket.RelayWebSocket
import com.github.p2pfilestream.client.websocket.RendezvousServer
import com.github.p2pfilestream.client.websocket.SessionWebSocket
import com.github.p2pfilestream.rendezvous.SessionClient
import com.github.p2pfilestream.rendezvous.SessionServer
import mu.KLogging
import tornadofx.Controller
import tornadofx.observable

class SessionController : Controller() {
    private lateinit var sessionServer: SessionServer
    val chats = mutableListOf<Chat>().observable()
    private lateinit var rendezvousServer: RendezvousServer

    /** Maps nicknames to requests */
    //val requests = HashMap<String, Request>()

    companion object : KLogging() {}

    fun chatRequest(nickname: String) {
        sessionServer.request(nickname)
    }

    /**
     * Called when user has chosen a nickname
     */
    fun login(jwt: String) {
        val receiver = Receiver()
        rendezvousServer = RendezvousServer(jwt)
        val webSocket = SessionWebSocket(receiver, rendezvousServer)
    }

    inner class Receiver : SessionClient {
        fun connectionEstablished(server: SessionServer) {
            sessionServer = server
        }

        override fun request(device: Device) {
            logger.info { "Got request from $device" }
            sessionServer.response(device.nickname, true)
        }

        override fun startChat(device: Device, chatId: Long) {
            logger.info { "Start a chat with $device; id: $chatId" }
            RelayWebSocket(rendezvousServer) {
                val chat = Chat(device, it)
                chats.add(chat)
                chat.receiver
            }
        }

        override fun declined(error: SessionClient.ResponseError) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun deleteRequest(nickname: String) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }
    }
}

