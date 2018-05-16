package com.github.p2pfilestream.accountserver

import com.github.p2pfilestream.Account
import com.github.p2pfilestream.Device
import org.hibernate.exception.ConstraintViolationException
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

    /**
     * Save device
     * @return null if nickname isn't unique
     */
    fun saveOrNull(device: Device): Device? {
        try {
            return save(device)
        } catch (e: ConstraintViolationException) {
            return null
        }
    }
}