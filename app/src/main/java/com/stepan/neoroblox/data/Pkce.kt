package com.stepan.neoroblox.data

import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Base64

object Pkce {
    private val encoder = Base64.getUrlEncoder().withoutPadding()

    fun newVerifier(): String {
        val bytes = ByteArray(64)
        SecureRandom().nextBytes(bytes)
        return encoder.encodeToString(bytes)
    }

    fun challenge(verifier: String): String {
        val digest = MessageDigest.getInstance("SHA-256").digest(verifier.toByteArray(Charsets.US_ASCII))
        return encoder.encodeToString(digest)
    }
}
