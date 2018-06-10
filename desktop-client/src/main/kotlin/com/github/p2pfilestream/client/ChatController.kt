package com.github.p2pfilestream.client

import java.io.File

interface ChatController {
    fun sendFile(file: File)

    fun sendText(text: String)
}