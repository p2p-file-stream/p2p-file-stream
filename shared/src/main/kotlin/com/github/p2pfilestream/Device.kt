package com.github.p2pfilestream

/**
 * An account can be singed in on multiple Devices.
 * Each device has it's own unique nickname.
 */
data class Device(
    val nick: String,
    val account: Account
)