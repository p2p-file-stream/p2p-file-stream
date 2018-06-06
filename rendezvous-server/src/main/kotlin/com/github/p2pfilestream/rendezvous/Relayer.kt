package com.github.p2pfilestream.rendezvous

interface Relayer {
    fun relay(a: RelayClient, b: RelayClient)
}