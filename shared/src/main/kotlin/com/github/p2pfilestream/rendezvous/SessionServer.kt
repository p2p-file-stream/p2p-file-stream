package com.github.p2pfilestream.rendezvous

interface SessionServer {
    /** Send request to server */
    fun request(nickname: String)

    /** Respond to request
     *
     * fixme: add paramters
     */
    fun response(nickname: String, confirm: Boolean)
}