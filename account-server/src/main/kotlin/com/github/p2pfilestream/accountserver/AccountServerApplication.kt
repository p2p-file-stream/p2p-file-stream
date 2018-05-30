package com.github.p2pfilestream.accountserver

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.runApplication

fun main(args: Array<String>) {
    runApplication<AccountServerApplication>(*args)
}

@SpringBootApplication
@EntityScan("com.github.p2pfilestream")
class AccountServerApplication
