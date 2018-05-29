package com.github.p2pfilestream

import com.github.p2pfilestream.chat.BinaryMessageChunk
import io.reactivex.Emitter
import io.reactivex.Flowable
import java.io.BufferedInputStream
import java.io.File


class FileProcessor(val file: File) {
    fun flowable(): Flowable<BinaryMessageChunk> {
        val consumer = { inputStream: BufferedInputStream, emitter: Emitter<BinaryMessageChunk> ->
            val data = ByteArray(8)
            val bytesRead = inputStream.read(data)
            if (bytesRead != -1) {
                val chunk = processChunk(data, bytesRead)
                emitter.onNext(chunk)
            } else {
                emitter.onComplete()
            }
        }
        return Flowable.generate(
            { file.inputStream().buffered() },
            consumer, { inputStream: BufferedInputStream -> inputStream.close() }
        )
    }

    private fun processChunk(bytes: ByteArray, bytesRead: Int): BinaryMessageChunk {
        return BinaryMessageChunk(0, bytes.take(bytesRead).toByteArray())
    }
}