package com.github.p2pfilestream.client.views

import com.github.p2pfilestream.client.session.AccountController
import tornadofx.View
import tornadofx.webview

/**
 * Uses Auth0 to login.
 *
 * https://auth0.com/docs/application-auth/current/mobile-desktop
 */
class LoginView : View(title = "P2P File Stream | Login") {

    private val accountController: AccountController by inject()

    override val root = webview()

    init {
        root.engine.load(accountController.loginUrl())
        root.engine.locationProperty().addListener { _, _, location ->
            accountController.locationChanged(location) {
                replaceWith(NicknameChooserView::class)
            }
        }
    }
}