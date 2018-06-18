package com.github.p2pfilestream.client.files

import javafx.beans.binding.DoubleBinding
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleDoubleProperty
import tornadofx.getValue
import tornadofx.setValue

abstract class FileStreamProgress(
    /** Total file-fileSize in bytes */
    fileSize: Long
) {
    /** Bytes processed */
    private val bytesProcessedProperty = SimpleDoubleProperty(0.0)
    private var bytesProcessed by bytesProcessedProperty
    /** Progress percentage */
    val progressPercentage: DoubleBinding = bytesProcessedProperty.divide(fileSize)
    val finishedProperty = SimpleBooleanProperty()

    protected fun madeProgress(byteCount: Int) {
        bytesProcessed += byteCount
    }

    protected fun finished() {
        finishedProperty.set(true)
    }

    /** Triggered if the user presses cancel */
    abstract fun cancel()
}