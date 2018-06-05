package com.github.p2pfilestream.views

import org.apache.commons.codec.binary.Base64
import java.nio.charset.Charset
import java.security.MessageDigest
import java.security.SecureRandom

fun main(args: Array<String>) {
    val sr = SecureRandom()
    val code = ByteArray(32)
    sr.nextBytes(code)
    val verifier = Base64.encodeBase64URLSafeString(code);
//    val base64 = Base64(true)
//    val verifier = base64.encodeToString(code);
    println("verifier: ${verifier}")

    val bytes = verifier.toByteArray(Charset.defaultCharset())
    val md = MessageDigest.getInstance("SHA-256");
    md.update(bytes);
    val digest = md.digest();
    val challenge = Base64.encodeBase64URLSafeString(digest);
    println("challenge: ${challenge}")


}