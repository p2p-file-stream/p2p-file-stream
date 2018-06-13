package com.github.p2pfilestream.client.dal

import java.util.prefs.Preferences

private const val systemProperty = "com.github.p2pfilestream.client.deviceToken"
private val key = System.getProperty(systemProperty, "deviceToken")

/**
 * Use Java Preferences API to store logged in device Json Web Token (JWT)
 *
 * https://dev.to/argherna/the-java-preferences-api-is-a-little-thing-thats-a-huge-benefit-13ac
 */
class PreferencesDeviceStore : DeviceStore {
    private val preferences = Preferences.userRoot().node("com/github/p2p-file-stream/client")

    override fun save(jwt: String) {
        preferences.put(key, jwt)
    }

    override fun get(): String? {
        return preferences.get(key, null)
    }

    override fun remove() {
        preferences.remove(key)
    }
}