package com.github.p2pfilestream.rendezvous

import com.github.p2pfilestream.Device
import mu.KLogging

class ConnectionManager {
    companion object : KLogging()

    /** Maps nicknames to sessions */
    private val sessions = HashMap<String, SessionService>()

    /** Maps nicknames of sender to request */
    private val requests = HashSet<ChatRequest>()

    fun connect(client: SessionClient, device: Device): SessionServer {
        val session = SessionService(client, device)
        sessions[device.nickname] = session
        return session
    }

    fun disconnect(nickname: String) {
        //fixme: Improve readability of this function
        sessions.remove(nickname)?.let { session ->
            // Delete requests with session as sender
            requests.filter { it.sender == session }.forEach {
                it.receiver.client.deleteRequest(session.device.nickname)
            }
            // Decline requests with session as receiver
            requests.filter { it.receiver == session }.forEach {
                it.sender.client.declined(SessionClient.ResponseError.DISCONNECTED)
            }
        } ?: logger.info { "Session to disconnect not found" }
    }

    private inner class SessionService(
        val client: SessionClient,
        val device: Device
    ) : SessionServer {
        override fun request(nickname: String) {
            val other = sessions[nickname]
            if (other == null) {
                // Nickname not found
                client.declined(SessionClient.ResponseError.NOT_FOUND)
            } else {
                // Do request
                other.client.request(device)
                requests.add(ChatRequest(this, other))
            }
        }

        override fun response(nickname: String, confirm: Boolean) {
            val sender = sessions[nickname]
            if (sender == null) {
                // Nickname not found
                logger.info { "Nickname not found in response()" }
                return
            }
            val request = ChatRequest(sender, this)
            if (!requests.remove(request)) {
                // Request not found
                logger.info { "Request not found in response()" }
                return
            }
            // Notify other client about response
            val other = request.sender.client
            if (confirm) {
                other.confirmed(device)
            } else {
                other.declined(SessionClient.ResponseError.DECLINED)
            }
        }

    }

    private data class ChatRequest(
        val sender: SessionService,
        val receiver: SessionService
    )
}

