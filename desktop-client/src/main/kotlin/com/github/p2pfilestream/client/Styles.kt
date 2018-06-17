package com.github.p2pfilestream.client

import javafx.scene.paint.Color
import tornadofx.Stylesheet
import tornadofx.cssclass
import tornadofx.em
import java.awt.Font

class Styles : Stylesheet() {
    companion object {
        val nickname by cssclass()
        val chatList by cssclass()
        val header by cssclass()
        val textMessage by cssclass()
    }

    init {
        nickname {
            fontSize = 1.5.em
        }
        chatList {
            nickname {
                fontSize = 1.3.em
            }
        }
        header {
            backgroundColor += Color.SKYBLUE
        }
        textMessage {
            fontFamily = Font.MONOSPACED
        }
    }
}