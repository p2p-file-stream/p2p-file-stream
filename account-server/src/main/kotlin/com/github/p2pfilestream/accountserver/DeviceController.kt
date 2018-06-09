package com.github.p2pfilestream.accountserver

import com.github.p2pfilestream.Device
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class DeviceController(val repository: DeviceRepository) {

    @PostMapping("/nickname")
    fun registerNickname(@RequestBody request: Device, authentication: Authentication): RegisterResponse {
        try {
            // Check account-id
            val accountId = authentication.principal as String
            if (request.account.id != accountId) {
                throw RegisterRequestException("AccountId does not match authentication token")
            }
            validate(request)
        } catch (e: RegisterRequestException) {
            return RegisterResponse(e)
        }
        val device = repository.saveOrNull(request)
        if (device == null) {
            return RegisterResponse(RegisterRequestException("Nickname already exists"))
        }
        return RegisterResponse(device)
    }

    private fun validate(device: Device) {
        when {
            device.nickname.length < 3 ->
                throw RegisterRequestException("Nickname should contain at least 3 characters")
            device.nickname.length > 25 ->
                throw RegisterRequestException("Nickname should contain at most 25 characters")
        }
    }
}
