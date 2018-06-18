package com.github.p2pfilestream.client.views

import com.github.p2pfilestream.client.dal.DeviceStore
import com.github.p2pfilestream.client.dal.PreferencesDeviceStore
import com.github.p2pfilestream.client.session.AccountController
import com.github.p2pfilestream.client.session.SessionController
import javafx.beans.property.SimpleStringProperty
import javafx.scene.control.Alert
import tornadofx.*

class NicknameChooserView(
    private val deviceStore: DeviceStore = PreferencesDeviceStore()
) : View() {
    private val input = SimpleStringProperty()
    private val accountController: AccountController by inject()
    private val sessionController: SessionController by inject()

    override val root = form {
        fieldset {
            field("Choose nickname") {
                textfield(input)
            }

            button("Commit") {
                action {
                    shortcut("Enter")
                    val response = accountController.chooseNickname(input.value)
                    if (response.success) {
                        val deviceJwt = response.jwt!!
                        deviceStore.save(deviceJwt)
                        sessionController.login(deviceJwt)
                        replaceWith(MainView::class)
                    } else {
                        alert(Alert.AlertType.ERROR, "Sorry", response.error)
                    }
                }
            }
        }
    }
}