package com.github.p2pfilestream.accountserver

import com.github.p2pfilestream.Device
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm

class RegisterRequestException(message: String) : Exception(message)

//todo: Refactor ugly constructors
class RegisterResponse(
    val success: Boolean,
    val error: String?,
    val jwt: String?
) {

    constructor(exception: RegisterRequestException) :
            this(false, exception.message, null)

    constructor(device: Device) :
            this(
                true,
                null,
                Jwts.builder()
                    .setSubject(device.nickname)
                    .signWith(SignatureAlgorithm.HS512, "MySecret")
                    .compact()
            )
}