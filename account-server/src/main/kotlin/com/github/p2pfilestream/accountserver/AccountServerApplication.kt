package com.github.p2pfilestream.accountserver

import com.github.p2pfilestream.Account
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.runApplication
import org.springframework.data.repository.CrudRepository

fun main(args: Array<String>) {
    runApplication<AccountServerApplication>(*args)
}

@SpringBootApplication
@EntityScan("com.github.p2pfilestream")
class AccountServerApplication

interface AccountRepository : CrudRepository<Account, Long>