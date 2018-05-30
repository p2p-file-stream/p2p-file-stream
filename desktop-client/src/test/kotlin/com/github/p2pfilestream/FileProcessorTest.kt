package com.github.p2pfilestream

import io.reactivex.Flowable
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.io.File
import java.util.concurrent.TimeUnit

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FileProcessorTest {
    private val fileProcessor = FileProcessor()

    @Test
    fun `Split stream in chunks`() {
        val file = File.createTempFile("HelloWorld", null)
        file.writeText("Hello World, this is a test file!")
        val result = fileProcessor.read(file)
            .toList().blockingGet()
            .joinToString("") { String(it.payload) }
        assertThat(result).isEqualTo("Hello World, this is a test file!")
    }

    @Test
    fun `Merge observables`() {
        val a = Flowable.interval(100, TimeUnit.MILLISECONDS).take(10)
        val b = Flowable.interval(50, TimeUnit.MILLISECONDS).take(10)
        val result = a.mergeWith(b)
        result.blockingSubscribe { println(it) }
    }
}