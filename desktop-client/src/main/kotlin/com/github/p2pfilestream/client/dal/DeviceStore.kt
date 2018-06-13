package com.github.p2pfilestream.client.dal

interface DeviceStore {
    /** Save device-nickname JWT when logging in */
    fun save(jwt: String)

    /** Get stored login (JWT), or null if absent */
    fun get(): String?

    /** Delete saved login when logging out */
    fun remove()
}