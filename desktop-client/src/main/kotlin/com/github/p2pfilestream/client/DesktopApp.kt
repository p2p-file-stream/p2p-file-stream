package com.github.p2pfilestream.client

import com.github.p2pfilestream.client.views.MainView
import javafx.application.Application
import tornadofx.App

class DesktopApp : App(MainView::class, Styles::class)

/**
 * The main method is needed to support the mvn jfx:run goal.
 */
fun main(args: Array<String>) {
    Application.launch(DesktopApp::class.java, *args)
}