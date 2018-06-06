package com.github.p2pfilestream.rendezvous

import com.github.p2pfilestream.Device
import com.github.p2pfilestream.encoding.OldMessageEncoder

class SessionClientEncoder(receiver: (ByteArray) -> Unit) : SessionClient, OldMessageEncoder(receiver) {
    override fun confirmed(device: Device) {
        message(::confirmed, device)
    }

    override fun declined(error: SessionClient.ResponseError) {
        message(::declined, error)
    }

    override fun deleteRequest(nickname: String) {
        message(::deleteRequest, nickname)
    }

    override fun request(device: Device) {
        message(::request, device)
    }
}