package com.github.p2pfilestream.views

import com.github.p2pfilestream.Account
import com.github.p2pfilestream.Device
import javafx.scene.layout.Priority
import tornadofx.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class MainView : View("P2P File Stream") {
    private val accountController: AccountController by inject()
    private val chats = mutableListOf<Chat>().observable()
    private val currentChat: ChatModel by inject()

    private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

    init {
        chats.addAll(
            Chat(Device("MyFriend", Account("e@mail", "a"), 123)),
            Chat(Device("AnotherFriend", Account("mail@me", "b"), 321))
        )
    }

    override val root = borderpane {
        top {
            hbox {
                label(accountController.nickname)
                label(accountController.username ?: "Anonymous")
            }
        }
        left {
            listview(chats) {
                cellFormat {
                    graphic = hbox {
                        label(it.nickname)
                        region { hgrow = Priority.ALWAYS }
                        label(it.start.format(timeFormatter))
                    }
                }
                bindSelected(currentChat)
            }
        }
        center(ChatView::class)
    }
}

class ChatModel : ItemViewModel<Chat>() {
    // val nickname get() = item?.device?.nickname.orEmpty()
    val deviceProperty = bind(Chat::device)
}

class Chat(
    val device: Device
) {
    val nicknameProperty = property(device.nickname)
    var nickname by nicknameProperty

    val startProperty = property(LocalDateTime.now())
    var start by startProperty
}

