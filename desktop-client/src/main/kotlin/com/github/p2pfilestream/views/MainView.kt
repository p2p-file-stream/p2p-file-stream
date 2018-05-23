package com.github.p2pfilestream.views

import tornadofx.*

class MainView : View("P2P File Stream") {
    val sessionController: SessionController by inject()

    override val root = borderpane {
        top {
            hbox {
                label(sessionController.nickname)
                label(sessionController.username ?: "Anonymous")
            }
        }
        center {
            stackpane {

            }
        }
    }
}

class SessionController : Controller() {
    val nickname: String = "Jan2000"
    val username: String? = "Jan Jansen"
}
