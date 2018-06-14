package com.github.p2pfilestream.accountserver.repositories

import com.github.p2pfilestream.Account
import org.springframework.data.repository.CrudRepository

interface AccountRepository : CrudRepository<Account, String> {
    fun findByUsername(username: String): Account?
}