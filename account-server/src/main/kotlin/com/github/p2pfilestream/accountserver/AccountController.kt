package com.github.p2pfilestream.accountserver

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/account")
class AccountController(private val accountRepository: AccountRepository) {
    @GetMapping("/by-nickname/{nickname}")
    fun byNickname(@PathVariable nickname: String) {

    }
}