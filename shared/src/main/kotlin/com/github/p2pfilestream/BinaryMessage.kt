package com.github.p2pfilestream

data class BinaryMessage(
    /** File-name with extension */
    val name: String,
    /** File-size in bits */
    val size: Long
)