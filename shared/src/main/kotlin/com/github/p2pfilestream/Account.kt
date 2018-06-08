package com.github.p2pfilestream

import javax.persistence.Entity
import javax.persistence.Id

@Entity
data class Account(
    /** username or e-mail */
    val username: String,
    @Id
    val id: String
)