package com.github.p2pfilestream.rendezvous

import com.github.p2pfilestream.Device

interface SessionClient {
    /** Notify client about new request to him */
    fun request(device: Device)

    /** The chat is started, both clients receive this */
    fun startChat(device: Device, chatId: Long)

    /** Request is declined */
    fun declined(nickname: String, error: ResponseError)

    /** Delete request when other disconnected */
    fun deleteRequest(nickname: String)

    enum class ResponseError(val message: String) {
        NOT_FOUND("Nickname not found or offline"),
        DECLINED("Request declined"),
        DISCONNECTED("Nickname just disconnected")
    }
}