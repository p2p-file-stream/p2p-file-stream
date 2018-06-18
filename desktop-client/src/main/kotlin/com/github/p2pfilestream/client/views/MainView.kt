package com.github.p2pfilestream.client.views

import com.github.p2pfilestream.client.chat.ChatModel
import com.github.p2pfilestream.client.dal.PreferencesDeviceStore
import com.github.p2pfilestream.client.session.AccountController
import com.github.p2pfilestream.client.session.SessionController
import javafx.beans.property.SimpleStringProperty
import javafx.collections.ListChangeListener
import javafx.geometry.Pos
import javafx.scene.layout.Priority
import tornadofx.*
import java.time.format.DateTimeFormatter

class MainView : View("P2P File Stream") {
    private val accountController: AccountController by inject()
    private val sessionController: SessionController by inject()
    private val chats = sessionController.chats
    private val currentChat: ChatModel by inject()
    private val chatView: ChatView by inject()

    private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

    override val root = gridpane {
        constraintsForColumn(0).prefWidth = 250.0
        constraintsForColumn(1).hgrow = Priority.ALWAYS
        constraintsForRow(1).vgrow = Priority.ALWAYS
        row {
            vbox {
                // Display logged in user-info
                addClass(Styles.header)
                label(sessionController.nicknameProperty).addClass(Styles.nickname)
                label(sessionController.emailProperty)
                button("Logout").action {
                    PreferencesDeviceStore().remove()
                    replaceWith(LoginView::class)
                }
            }
            hbox(10) {
                // Information about current chat
                alignment = Pos.CENTER_RIGHT
                addClass(Styles.header)
                label(currentChat.nicknameProperty)
                label(currentChat.emailProperty)
                button("Download directory").action {
                    currentChat.item?.chooseDirectory()
                }
                button("Close chat") {
                    action {
                        currentChat.item.close()
                    }
                    visibleWhen(currentChat.closed.not())
                }
                visibleWhen(currentChat.empty.not())
            }
        }
        row {
            vbox {
                // List of chats
                listview(chats) {
                    vgrow = Priority.ALWAYS
                    cellFormat {
                        graphic = hbox {
                            addClass(Styles.chatList)
                            label(it.peerNickname).addClass(Styles.nickname)
                            region { hgrow = Priority.ALWAYS }
                            label(it.start.format(timeFormatter))
                        }
                    }
                    bindSelected(currentChat)
                }
                button("New chat").action(::newChat)
            }
            add(chatView)
        }
    }

    init {
        chats.addListener(ListChangeListener {
            while (it.next()) {
                // When a new chat is opened, view it
                val newChat = it.addedSubList.firstOrNull()
                if (newChat != null) {
                    currentChat.item = newChat
                }
                // Close removed chats
                if (currentChat.item in it.removed) {
                    currentChat.item = null
                }
            }
        })
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