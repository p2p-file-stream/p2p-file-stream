package com.github.p2pfilestream.rendezvous

import com.github.p2pfilestream.Device

class SessionClientEncoder(receiver: (ByteArray) -> Unit) : SessionClient, MessageEncoder(receiver) {

    override fun deleteRequest(nickname: String) {
        message(::deleteRequest, nickname)
    }

    override fun request(device: Device) {
        message(::request, device)
    }

    override fun response(device: Device, success: Boolean, error: SessionClient.ResponseError) {
        message(::response, device, success, error)
    }
}