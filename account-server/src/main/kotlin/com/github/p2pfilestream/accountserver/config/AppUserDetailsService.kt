package com.github.p2pfilestream.accountserver.config

import com.github.p2pfilestream.accountserver.AccountRepository
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Component

@Component
class AppUserDetailsService(
    private val accountRepository: AccountRepository
) : UserDetailsService {
    override fun loadUserByUsername(username: String): UserDetails {
        val account = accountRepository.findByUsername(username)
                ?: throw UsernameNotFoundException("Not found")
        return User(account.username, "pass", emptyList())
    }
}