package com.github.p2pfilestream

import javax.persistence.*

/**
 * An account can be singed in on multiple Devices.
 * Each device has it's own unique nickname.
 */
@Entity
data class Device(
    @Column(unique = true)
    val nickname: String,

    @ManyToOne
    val account: Account,

    @Id @GeneratedValue
    val id: Long = 0
)