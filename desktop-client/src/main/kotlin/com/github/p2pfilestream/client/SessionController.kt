package com.github.p2pfilestream.client

import com.github.p2pfilestream.Device
import com.github.p2pfilestream.rendezvous.SessionClient
import com.github.p2pfilestream.rendezvous.SessionServer
import tornadofx.Controller

class SessionController : Controller() {
    val accountController: AccountController by inject()
    var sessionServer: SessionServer? = null

    /** Maps nicknames to requests */
    //val requests = HashMap<String, Request>()

    init {
        val receiver = Receiver()
        val webSocket = SessionWebSocket(receiver)
    }

    fun chatRequest(nickname: String) {
        sessionServer?.request(nickname)
    }

    val chatModel: ChatModel by inject()

    inner class Receiver : SessionClient {
        override fun request(device: Device) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun startChat(device: Device, chatId: Long) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun declined(error: SessionClient.ResponseError) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun deleteRequest(nickname: String) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        fun connectionEstablished(server: SessionServer) {
            sessionServer = server
        }
    }
}

