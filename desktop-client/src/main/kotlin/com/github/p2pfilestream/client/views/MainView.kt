package com.github.p2pfilestream.client.views

import com.github.p2pfilestream.Account
import com.github.p2pfilestream.Device
import com.github.p2pfilestream.client.*
import javafx.beans.property.SimpleStringProperty
import javafx.scene.layout.Priority
import tornadofx.*
import java.time.format.DateTimeFormatter

class MainView : View("P2P File Stream") {
    private val accountController: AccountController by inject()
    private val sessionController: SessionController by inject()
    private val chats = mutableListOf<Chat>().observable()
    private val currentChat: ChatModel by inject()

    private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

    init {
        chats.addAll(
            Chat(Device("MyFriend", Account("e@mail", "a"), 123)),
            Chat(Device("AnotherFriend", Account("mail@me", "b"), 321))
        )
    }

    override val root = gridpane {
        row {
            vbox {
                addClass(Styles.header)
                label(accountController.nickname).addClass(Styles.nickname)
                label(accountController.username)
            }
            hbox {
                addClass(Styles.header)
                label(currentChat.deviceProperty)
            }
        }
        row {
            vbox {
                listview(chats) {
                    cellFormat {
                        graphic = hbox {
                            addClass(Styles.chatList)
                            label(it.device.nickname).addClass(Styles.nickname)
                            region { hgrow = Priority.ALWAYS }
                            label(it.start.format(timeFormatter))
                        }
                    }
                    bindSelected(currentChat)
                }
                button("New chat").action(::newChat)
            }
            add(ChatView::class)
        }
    }

    private fun newChat() {
        dialog("New chat") {
            val model = ViewModel()
            val nickname = model.bind { SimpleStringProperty() }

            field("Nickname") {
                textfield(nickname) {
                    required()
                    whenDocked { requestFocus() }
                }
            }
            buttonbar {
                button("Send request").action {
                    model.commit {
                        sessionController.chatRequest(nickname.value)
                        close()
                    }
                }
            }
        }
    }
}