package com.github.p2pfilestream

import java.io.File
import java.nio.charset.Charset

class FileProcessor {
    fun readFile(file: File) {
        val inputStream = file.inputStream()
        val data = ByteArray(8)
        var bytesRead = inputStream.read(data)
        while (bytesRead != -1) {
            processChunk(data, bytesRead)
            bytesRead = inputStream.read(data)
        }
        inputStream.close()
    }

    private fun processChunk(bytes: ByteArray, bytesRead: Int) {
        println("Chunk: " + bytes.toString(Charset.defaultCharset()))
    }
}