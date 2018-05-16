package com.github.p2pfilestream

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

/**
 * An account can be singed in on multiple Devices.
 * Each device has it's own unique nickname.
 */
@Entity
data class Device(
    @Column(unique = true)
    val nick: String,

    val account: Account,

    @Id @GeneratedValue
    val id: Long = 0
)