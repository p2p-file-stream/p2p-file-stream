package com.github.p2pfilestream.client.views

import com.github.p2pfilestream.client.AccountController
import com.github.p2pfilestream.client.ChatModel
import com.github.p2pfilestream.client.SessionController
import com.github.p2pfilestream.client.Styles
import javafx.beans.property.SimpleStringProperty
import javafx.scene.layout.Priority
import tornadofx.*
import java.time.format.DateTimeFormatter

class MainView : View("P2P File Stream") {
    private val accountController: AccountController by inject()
    private val sessionController: SessionController by inject()
    private val chats = sessionController.chats
    private val currentChat: ChatModel by inject()

    private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

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