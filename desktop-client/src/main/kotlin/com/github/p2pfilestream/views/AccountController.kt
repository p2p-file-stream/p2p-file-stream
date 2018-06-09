package com.github.p2pfilestream.views

import com.auth0.jwk.UrlJwkProvider
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.github.p2pfilestream.Account
import com.github.p2pfilestream.Device
import com.mashape.unirest.http.Unirest
import io.jsonwebtoken.Claims
import io.jsonwebtoken.JwsHeader
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SigningKeyResolverAdapter
import javafx.scene.control.Alert
import org.apache.commons.codec.binary.Base64
import org.springframework.web.client.RestTemplate
import tornadofx.Controller
import tornadofx.alert
import java.nio.charset.Charset
import java.security.Key
import java.security.MessageDigest
import java.security.SecureRandom

class AccountController : Controller() {
    val nickname: String = "Jan2000"
    val username: String? = "Jan Jansen"

    private lateinit var account: Account
    private lateinit var auth0Response: Auth0Response

    private val restTemplate = RestTemplate()

    private val objectMapper = jacksonObjectMapper()

    private val baseURL = "https://p2p-file-stream.eu.auth0.com"
    private val clientId = "XFRf3j_s8wJZy8UK7sb5a1rY2QW56wHe"

    private val accountServerUrl = "http://localhost:8080"

    private val verifier: String
    private val challenge: String

    init {
        val auth0Challenge = generateChallenge()
        verifier = auth0Challenge.verifier
        challenge = auth0Challenge.challenge
    }

    fun loginUrl(): String = "$baseURL/authorize?scope=openid%20email%20offline_access&response_type=code&" +
            "client_id=$clientId&audience=nickname-server&" +
            "code_challenge=$challenge&code_challenge_method=S256&" +
            "redirect_uri=$baseURL/mobile"

    fun locationChanged(location: String, callback: () -> Unit) {
        if (location.contains("$baseURL/mobile", ignoreCase = true)) {
            val matchResult = Regex("\\?code=(.*)\$").find(location)
            if (matchResult == null) {
                alert(Alert.AlertType.ERROR, "Something went wrong", location)
            } else {
                val authCode = matchResult.groupValues[1]
                login(authCode)
                callback()
            }
        }
    }

    private fun login(authCode: String) {
        println("authCode: $authCode")
        val data = """{"grant_type":"authorization_code","client_id": "$clientId",
                            |"code_verifier": "$verifier","code": "$authCode",
                            |"redirect_uri": "$baseURL/mobile" }""".trimMargin()
        val body = Unirest.post("$baseURL/oauth/token")
            .header("content-type", "application/json")
            .body(data)
            .asString().body
        println("Auth0 response: ${body}")
        auth0Response = objectMapper.readValue(body)
        val idToken = Jwts.parser()
            .setSigningKeyResolver(object : SigningKeyResolverAdapter() {
                override fun resolveSigningKey(header: JwsHeader<*>, claims: Claims): Key {
                    val provider = UrlJwkProvider("https://p2p-file-stream.eu.auth0.com/")
                    val kid = header["kid"] as String
                    return provider[kid].publicKey
                }
            })
            .parseClaimsJws(auth0Response.idToken).body
        val email = idToken["email"] as String
        account = Account(email, idToken.subject)
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

    fun chooseNickname(nickname: String) {
        val device = Device(nickname, account)
        Unirest.post("$accountServerUrl/nickname")
            .header("Authorization", "Bearer ${auth0Response.accessToken}")
            .body(objectMapper.writeValueAsBytes(device))
    }
}

private data class Auth0Challenge(val verifier: String, val challenge: String)

private data class Auth0Response(
    @JsonProperty("access-token")
    val accessToken: String,
    @JsonProperty("id-token")
    val idToken: String,
    @JsonProperty("expires-in")
    val expiresIn: String,
    @JsonProperty("token-type")
    val tokenType: String
)