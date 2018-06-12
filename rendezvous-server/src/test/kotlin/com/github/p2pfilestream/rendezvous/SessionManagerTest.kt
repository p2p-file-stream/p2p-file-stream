package com.github.p2pfilestream.rendezvous

import com.github.p2pfilestream.Account
import com.github.p2pfilestream.Device
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

private lateinit var sessionManager: SessionManager

class SessionManagerTest {
    private fun random() = Random(123)

    @BeforeEach
    fun setUp() {
        sessionManager = SessionManager(random())
    }

    @Test
    fun `Nickname not found`() {
        val client: SessionClient = mockk(relaxed = true)
        val server = sessionManager.connect(client, newDevice())
        server.request("Nick")
        verify {
            client.declined("Nick", SessionClient.ResponseError.NOT_FOUND)
        }
    }

    @Test
    fun `Request with positive response`() {
        val alex = Connection("Alex")
        val bob = Connection("Bob")
        // Alex sends Bob a request
        alex.server.request("Bob")
        // Bob receives it, and confirms it
        verify {
            bob.client.request(alex.device)
        }
        bob.server.response("Alex", true)
        // Alex receives the response
        val chatId = random().nextLong()
        verify {
            alex.client.startChat(bob.device, chatId)
            bob.client.startChat(alex.device, chatId)
        }
    }

    @Test
    fun `Request with negative response`() {
        val alex = Connection("Alex")
        val bob = Connection("Bob")
        // Alex sends Bob a request
        alex.server.request("Bob")
        // Bob receives it
        verify {
            bob.client.request(alex.device)
        }
        // He declines the request
        bob.server.response("Alex", false)
        // Alex receives the response
        verify {
            alex.client.declined("Bob", SessionClient.ResponseError.DECLINED)
        }
    }
}

private class Connection(nickname: String) {
    val client: SessionClient = mockk(relaxed = true)
    val device = newDevice(nickname)
    val server = sessionManager.connect(client, device)
}

private var counter = 0L
private fun newDevice(nickname: String = "Hello") =
    Device(nickname, Account("World", "MyId"), counter)
