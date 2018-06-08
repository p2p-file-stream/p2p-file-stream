package com.github.p2pfilestream.views

import com.mashape.unirest.http.Unirest
import javafx.scene.control.Alert
import org.apache.commons.codec.binary.Base64
import tornadofx.View
import tornadofx.alert
import tornadofx.webview
import java.nio.charset.Charset
import java.security.MessageDigest
import java.security.SecureRandom

/**
 * Uses Auth0 to login.
 *
 * https://auth0.com/docs/application-auth/current/mobile-desktop
 */
class LoginView : View() {

    private val baseURL = "https://p2p-file-stream.eu.auth0.com"
    private val clientId = "XFRf3j_s8wJZy8UK7sb5a1rY2QW56wHe"

    private val sessionController: SessionController by inject()

    override val root = webview()

    init {
        val (verifier, challenge) = generateChallenge().apply { println(this) }
        root.engine.load(
            "$baseURL/authorize?scope=openid%20email%20offline_access&response_type=code&" +
                    "client_id=$clientId&audience=nickname-server&" +
                    "code_challenge=$challenge&code_challenge_method=S256&" +
                    "redirect_uri=$baseURL/mobile"
        )
        root.engine.locationProperty().addListener { _, _, location ->
            if (location.startsWith("$baseURL/mobile", ignoreCase = true)) {
                val matchResult = Regex("\\?code=(.*)\$").find(location)
                if (matchResult == null) {
                    alert(Alert.AlertType.ERROR, "Something went wrong", location)
                } else {
                    val authCode = matchResult.groupValues[1]
                    println("authCode: $authCode")
                    val data = """{"grant_type":"authorization_code","client_id": "$clientId",
                            |"code_verifier": "$verifier","code": "$authCode",
                            |"redirect_uri": "$baseURL/mobile" }""".trimMargin()
                    val response = Unirest.post("$baseURL/oauth/token")
                        .header("content-type", "application/json")
                        .body(data)
                        .asString()
                    println("Auth0 response: ${response.body}")
                    sessionController.processResponse(response.body)
                    replaceWith(MainView::class)
                }
            }
        }
    }
}


private fun generateChallenge(): Auth0Challenge {
    val sr = SecureRandom()
    val code = ByteArray(32)
    sr.nextBytes(code)
    val verifier = Base64.encodeBase64URLSafeString(code)
    val bytes = verifier.toByteArray(Charset.defaultCharset())
    val md = MessageDigest.getInstance("SHA-256")
    md.update(bytes)
    val digest = md.digest()
    val challenge = Base64.encodeBase64URLSafeString(digest)
    return Auth0Challenge(verifier, challenge)
}

private data class Auth0Challenge(val verifier: String, val challenge: String)

private data class Auth0TokenResponse(
    val accessToken: String,
    val idToken: String,
    val expiresIn: String,
    val tokenType: String
)