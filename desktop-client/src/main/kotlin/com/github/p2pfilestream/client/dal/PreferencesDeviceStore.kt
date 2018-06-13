package com.github.p2pfilestream.client.dal

import java.util.prefs.Preferences

private const val keyName = "device-jwt"

/**
 * Use Java Preferences API to store logged in device JWT
 *
 * https://dev.to/argherna/the-java-preferences-api-is-a-little-thing-thats-a-huge-benefit-13ac
 */
class PreferencesDeviceStore : DeviceStore {
    private val preferences = Preferences.userRoot().node("com/github/p2p-file-stream/client")

    override fun save(jwt: String) {
        preferences.put(keyName, jwt)
    }

    override fun get(): String? {
        return preferences.get(keyName, null)
    }

    override fun remove() {
        preferences.remove(keyName)
    }
}