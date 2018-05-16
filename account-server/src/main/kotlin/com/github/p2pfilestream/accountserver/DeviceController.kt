package com.github.p2pfilestream.accountserver

import com.github.p2pfilestream.Device
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class DeviceController(val repository: DeviceRepository) {

    @PostMapping("/nickname")
    fun registerNickname(@RequestBody registerRequest: RegisterRequest): RegisterResponse {
        try {
            registerRequest.validate()
        } catch (e: RegisterRequestException) {
            return RegisterResponse(e)
        }
        val device = repository.saveOrNull(registerRequest.device)
        if (device == null) {
            return RegisterResponse(RegisterRequestException("Nickname already exists"))
        }
        return RegisterResponse(device)
    }

    class RegisterRequestException(message: String) : Exception(message)

    class RegisterRequest(
        val device: Device,
        val jwt: String?
    ) {
        fun validate() {
            if (device.account != null) {
                //todo: Validate JWT
            }
            if (device.nickname.length < 3) {
                throw RegisterRequestException("Nickname should contain at least 3 characters")
            } else if (device.nickname.length > 25) {
                throw RegisterRequestException("Nickname should contain at most 25 characters")
            }
        }
    }

    class RegisterResponse {
        val success: Boolean
        val error: String?
        val jwt: String?

        constructor(exception: RegisterRequestException) {
            success = false
            error = exception.message
            jwt = null
        }

        constructor(device: Device) {
            success = true
            jwt = "JWT TODO" //TODO jwt
            error = null
        }
    }
}