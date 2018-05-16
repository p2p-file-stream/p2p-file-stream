package com.github.p2pfilestream.accountserver

import com.github.p2pfilestream.Account
import com.github.p2pfilestream.Device
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

interface DeviceRepository : CrudRepository<Device, Long> {
    fun findByNickname(nickname: String)
}