package com.github.p2pfilestream.rendezvousserver

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class RendezvousServerApplication

fun main(args: Array<String>) {
    runApplication<RendezvousServerApplication>(*args)
}
