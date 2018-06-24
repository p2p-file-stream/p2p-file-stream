package com.github.p2pfilestream.client.websocket

import com.github.p2pfilestream.client.session.SessionController
import io.mockk.mockk
import io.mockk.verify
import org.junit.Ignore
import org.junit.jupiter.api.Test

/**
 * Integration test for WebSocket connection
 *
 * RendezvousServer must be running
 */
@Ignore
class SessionWebSocketTestIt {
    @Test
    fun `Connect to session-server`() {
        val mock = mockk<SessionController.Receiver>(relaxed = true)
        val jwt =
            "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJIZWxsb1dvcmxkNiIsImVtYWlsIjoidGpldS5rYXlpbUBnbWFpbC5jb20iLCJhY2NvdW50IjoiYXV0aDB8NWIxNjM1NjgyMTY1MmExMzFiMDViYmQ1In0.exW4JXsA92BL1RY74BLqmGqTLKfS14asCqEiSX4bwPfsOUltIiBJvTGf1eAYNbu8nNzZCDMrTAkynjZvv1PSIA"
        val sessionWebSocket = SessionWebSocket(mock, RendezvousServer(jwt))
        Thread.sleep(2000)
        verify {
            mock.connectionEstablished(any())
        }
        sessionWebSocket.disconnect()
    }
}
