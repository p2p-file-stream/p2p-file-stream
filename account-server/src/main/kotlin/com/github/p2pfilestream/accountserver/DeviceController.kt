package com.github.p2pfilestream.accountserver

import com.github.p2pfilestream.Device
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class DeviceController(val repository: DeviceRepository) {

    @PostMapping("/nickname")
    fun registerNickname(@RequestBody registerRequest: RegisterRequest, authentication: Authentication): RegisterResponse {
        try {
            // Check account-id
            val accountId = authentication.principal as String
            if (registerRequest.device.account.id != accountId) {
                throw RegisterRequestException("AccountId does not match authentication token")
            }
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
            jwt = Jwts.builder()
                .setSubject(device.nickname)
                .signWith(SignatureAlgorithm.HS512, "MySecret")
                .compact()
            error = null
        }
    }
}
