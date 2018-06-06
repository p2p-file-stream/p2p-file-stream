package com.github.p2pfilestream.encoding

interface Disconnectable {
    fun disconnect(reason: String?)
}