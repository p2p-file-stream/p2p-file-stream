package com.github.p2pfilestream.chat

data class BinaryMessage(
    override val index: Int,
    /** File-name with extension */
    val name: String,
    /** File-size in bits */
    val size: Long
) : ChatMessage