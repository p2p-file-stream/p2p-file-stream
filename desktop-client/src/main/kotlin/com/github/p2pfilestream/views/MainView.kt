package com.github.p2pfilestream.views

import com.github.p2pfilestream.Device
import tornadofx.*

class MainView : View("P2P File Stream") {
    private val sessionController: SessionController by inject()
    private val chats = mutableListOf<Chat>().observable()
    private val currentChat: ChatModel by inject()

    init {
        chats.addAll(
            Chat(Device("MyFriend", null, 123)),
            Chat(Device("AnotherFriend", null, 321))
        )
    }

    override val root = borderpane {
        top {
            hbox {
                label(sessionController.nickname)
                label(sessionController.username ?: "Anonymous")
            }
        }
        left {
            listview(chats) {
                bindSelected(currentChat)
            }
        }
        center(ChatView::class)
    }
}

class ChatModel : ItemViewModel<Chat>() {
    //    val nickname get() = item?.device?.nickname.orEmpty()
    val device = bind(Chat::device)
}

class Chat(
    val device: Device
)

class SessionController : Controller() {
    fun processResponse(response: String) {

    }

    val nickname: String = "Jan2000"
    val username: String? = "Jan Jansen"
}

