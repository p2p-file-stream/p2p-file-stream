package com.github.p2pfilestream.rendezvous.relay

import mu.KLogging
import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalDateTime.now

class RelayManager(
    private val relayer: Relayer,
    /** Timeout in seconds */
    private val timeout: Int
) {
    /** Clients waiting on a match, keys are chat-ids */
    private val clients = HashMap<Long, WaitingClient>()

    private companion object : KLogging()

    fun connect(client: RelayClient, chatId: Long) {
        // Find match
        val other = clients[chatId]?.relayClient
        if (other == null) {
            // not found -> put into queue
            clients[chatId] = WaitingClient(client)
            logger.info { "New client in queue, chatId: $chatId" }
        } else {
            // connect them
            logger.info { "Found a match, chatId: $chatId" }
            relayer.relay(client, other)
            clients.remove(chatId)
        }
        cleanup()
    }

    /** Remove clients that are waiting for longer than the timeout */
    private fun cleanup() {
        clients.entries.removeIf { Duration.between(it.value.time, now()).seconds > timeout }
    }

    fun disconnect(client: RelayClient) {
        val id = clients.entries.firstOrNull { it.value.relayClient == client }?.key
        if (id != null) {
            clients.remove(id)
        }
    }

    private data class WaitingClient(
        val relayClient: RelayClient,
        val time: LocalDateTime = now()
    )
}