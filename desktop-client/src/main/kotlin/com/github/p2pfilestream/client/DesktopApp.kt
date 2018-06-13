package com.github.p2pfilestream.client

import com.github.p2pfilestream.client.dal.PreferencesDeviceStore
import com.github.p2pfilestream.client.views.LoginView
import com.github.p2pfilestream.client.views.MainView
import javafx.application.Application
import mu.KLogging
import tornadofx.App
import tornadofx.UIComponent
import kotlin.reflect.KClass

class DesktopApp : App(stylesheet = Styles::class) {
    override val primaryView: KClass<out UIComponent>
    private val sessionController: SessionController by inject()

    private companion object : KLogging()

    init {
        // Show login if not logged in
        val deviceStore = PreferencesDeviceStore()
        val deviceJwt = deviceStore.get()
        primaryView = when (deviceJwt) {
            null -> {
                // Not logged in => show login view
                LoginView::class
            }
            else -> {
                // Logged in => skip login view
                logger.info { "Skipping login view" }
                sessionController.login(deviceJwt)
                MainView::class
            }
        }
    }
}

/**
 * The main method is needed to support the mvn jfx:run goal.
 */
fun main(args: Array<String>) {
    Application.launch(DesktopApp::class.java, *args)
}