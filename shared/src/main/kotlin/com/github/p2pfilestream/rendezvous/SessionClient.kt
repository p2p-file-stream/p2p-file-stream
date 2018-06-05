package com.github.p2pfilestream.rendezvous

import com.github.p2pfilestream.Device

interface SessionClient {
    /** Notify client about request */
    fun request(device: Device)

    /** Notify about confirmation */
    fun confirmed(device: Device)

    /** Request is declined */
    fun declined(error: ResponseError)

    /** Delete request when other disconnected */
    fun deleteRequest(nickname: String)

    enum class ResponseError(val message: String) {
        NOT_FOUND("Nickname not found or offline"),
        DECLINED("Request declined"),
        DISCONNECTED("Nickname just disconnected")
    }
}