package com.github.p2pfilestream.rendezvous.relay

interface Relayer {
    fun relay(a: RelayClient, b: RelayClient)
}