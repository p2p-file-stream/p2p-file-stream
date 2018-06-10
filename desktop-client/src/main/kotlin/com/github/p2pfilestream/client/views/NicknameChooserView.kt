package com.github.p2pfilestream.client.views

import com.github.p2pfilestream.client.AccountController
import javafx.beans.property.SimpleStringProperty
import tornadofx.*

class NicknameChooserView : View() {
    private val input = SimpleStringProperty()
    private val accountController: AccountController by inject()

    override val root = form {
        fieldset {
            field("Choose nickname") {
                textfield(input)
            }

            button("Commit") {
                action {
                    shortcut("Enter")
                    accountController.chooseNickname(input.value)
                    replaceWith(MainView::class)
                }
            }
        }
    }
}