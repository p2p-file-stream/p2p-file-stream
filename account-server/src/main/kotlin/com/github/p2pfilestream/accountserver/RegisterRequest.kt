package com.github.p2pfilestream.accountserver

import com.github.p2pfilestream.Device

class RegisterRequestException(message: String) : Exception(message)

class RegisterRequest(
    val device: Device
) {
    fun validate() {
        when {
            device.nickname.length < 3 ->
                throw RegisterRequestException("Nickname should contain at least 3 characters")
            device.nickname.length > 25 ->
                throw RegisterRequestException("Nickname should contain at most 25 characters")
        }
    }
}