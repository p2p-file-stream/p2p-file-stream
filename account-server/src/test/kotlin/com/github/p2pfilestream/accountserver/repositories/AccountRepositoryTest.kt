package com.github.p2pfilestream.accountserver.repositories

import com.github.p2pfilestream.Account
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension

@SpringBootTest
@ExtendWith(SpringExtension::class)
class AccountRepositoryTest {
    @Autowired
    lateinit var accountRepository: AccountRepository

    @Test
    fun `Save account in database`() {
        val account = Account("John", "abcd")
        val result = accountRepository.save(account)
        assertEquals(account, result)
    }
}