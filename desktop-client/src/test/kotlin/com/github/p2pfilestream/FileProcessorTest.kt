package com.github.p2pfilestream

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.io.File

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FileProcessorTest {
    private lateinit var fileProcessor: FileProcessor

    @BeforeEach
    fun init() {
        val file = File.createTempFile("HelloWorld", null)
        file.writeText("Hello World, this is a test file!")
        fileProcessor = FileProcessor(file)
    }

    @Test
    fun `Split stream in chunks`() {
        val result = fileProcessor.flowable()
            .toList().blockingGet()
            .joinToString("") { String(it.payload) }
        assertThat(result).isEqualTo("Hello World, this is a test file!")
    }
}