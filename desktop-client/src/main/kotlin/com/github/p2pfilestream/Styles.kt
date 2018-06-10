package com.github.p2pfilestream

import tornadofx.Stylesheet
import tornadofx.cssclass
import tornadofx.em

class Styles : Stylesheet() {
    companion object {
        val nickname by cssclass()
        val chatList by cssclass()
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
    }
}