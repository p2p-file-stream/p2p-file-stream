package com.github.p2pfilestream.rendezvous

import com.github.p2pfilestream.Device
import mu.KLogging

class ConnectionManager {
    companion object : KLogging()

    /** Maps nicknames to sessions */
    private val sessions = HashMap<String, Session>()

    /** Maps nicknames of sender to request */
    private val requests = HashSet<ChatRequest>()

    fun connect(client: SessionClient, device: Device): SessionServer {
        val session = Session(client, device)
        sessions[device.nickname] = session
        return SessionService(session)
    }

    fun disconnect(nickname: String) {
        sessions.remove(nickname)
    }

    private inner class SessionService(session: Session) : SessionServer {
        private val client = session.client
        private val device = session.device

        override fun request(nickname: String) {
            val other = sessions[nickname]
            if (other == null) {
                // Nickname not found
                client.declined(SessionClient.ResponseError.NOT_FOUND)
            } else {
                // Do request
                other.client.request(device)
                //requests.add(ChatRequest(this))
            }
        }

        override fun response(nickname: String, confirm: Boolean) {
            val other = sessions[nickname]
            if (other == null) {
                // Nickname not found
                client.deleteRequest(nickname)
                return
            }
            if (confirm) {
                other.client.confirmed(device)
            } else {
                other.client.declined(SessionClient.ResponseError.DECLINED)
            }
        }

    }
}

data class ChatRequest(
    val sender: Session,
    val receiver: Session
)

data class Session(
    val client: SessionClient,
    val device: Device
)