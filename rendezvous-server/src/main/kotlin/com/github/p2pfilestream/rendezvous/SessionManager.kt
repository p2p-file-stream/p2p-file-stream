package com.github.p2pfilestream.rendezvous

import com.github.p2pfilestream.Device
import mu.KLogging
import java.util.*

class SessionManager(
    private val random: Random = Random()
) {
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
        val session = sessions.remove(nickname)
        if (session == null) {
            logger.info { "Session to disconnect not found" }
            return
        }
        // Delete requests with session as sender
        requests.filter { it.sender == session.client }.forEach {
            it.receiver.deleteRequest(session.device.nickname)
        }
        // Decline requests with session as receiver
        requests.filter { it.receiver == session.client }.forEach {
            it.sender.declined(nickname, SessionClient.ResponseError.DISCONNECTED)
        }
    }

    private inner class SessionService(
        val client: SessionClient,
        val device: Device
    ) : SessionServer {
        override fun request(nickname: String) {
            val other = sessions[nickname]
            if (other == null) {
                // Nickname not found
                client.declined(nickname, SessionClient.ResponseError.NOT_FOUND)
            } else {
                // Do request
                other.client.request(device)
                requests.add(ChatRequest(client, other.client))
            }
        }

        override fun response(nickname: String, confirm: Boolean) {
            val sender = sessions[nickname]
            if (sender == null) {
                // Nickname not found
                logger.info { "Nickname not found in response()" }
                return
            }
            val request = ChatRequest(sender.client, client)
            if (!requests.remove(request)) {
                // Request not found
                logger.info { "Request not found in response()" }
                return
            }
            val other = request.sender
            // Handle response
            if (confirm) {
                // Generate chatId
                val chatId = random.nextLong()
                other.startChat(device, chatId)
                client.startChat(sender.device, chatId)
            } else {
                other.declined(device.nickname, SessionClient.ResponseError.DECLINED)
            }
        }
    }

    private data class ChatRequest(
        val sender: SessionClient,
        val receiver: SessionClient
    )
}

