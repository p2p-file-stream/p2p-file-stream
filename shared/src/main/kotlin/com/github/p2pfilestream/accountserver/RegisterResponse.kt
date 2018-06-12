package com.github.p2pfilestream.accountserver

class RegisterRequestException(message: String) : Exception(message)

//todo: Refactor ugly constructors
class RegisterResponse(
    val success: Boolean,
    val error: String?,
    val jwt: String?
) {
    constructor(exception: RegisterRequestException) :
            this(false, exception.message, null)

    constructor(jwt: String) : this(true, null, jwt)
}