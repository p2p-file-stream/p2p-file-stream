package com.github.p2pfilestream.views

import com.github.p2pfilestream.Styles
import javafx.scene.control.Alert.AlertType.INFORMATION
import tornadofx.*

class MainView : View("Hello TornadoFX") {
    override val root = borderpane {
        addClass(Styles.welcomeScreen)
        top {
            stackpane {
                label(title).addClass(Styles.heading)
            }
        }
        center {
            stackpane {
                addClass(Styles.content)
                button("Click me") {
                    setOnAction {
                        alert(INFORMATION, "Well done!", "You clicked me!")
                    }
                }
            }
        }
    }
}