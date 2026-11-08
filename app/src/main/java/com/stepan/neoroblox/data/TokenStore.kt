package com.stepan.neoroblox.data

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

/**
 * Keeps OAuth tokens in EncryptedSharedPreferences (Android Keystore).
 * If the Keystore is unavailable, tokens live in memory only and are never written to disk in plain text.
 */
class TokenStore(context: Context) {
    private val secure: SharedPreferences? = runCatching {
        EncryptedSharedPreferences.create(
            context,
            "neo_secure",
            MasterKey.Builder(context).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build(),
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
        )
    }.getOrNull()

    private val memory = HashMap<String, String>()
    private val plain = context.getSharedPreferences("neo_config", Context.MODE_PRIVATE)

    private fun read(key: String): String? = secure?.getString(key, null) ?: memory[key]

    private fun write(key: String, value: String?) {
        if (value == null) {
            secure?.edit()?.remove(key)?.apply()
            memory.remove(key)
        } else if (secure != null) {
            secure.edit().putString(key, value).apply()
        } else {
            memory[key] = value
        }
    }

    var accessToken: String?
        get() = read("access_token")
        set(value) = write("access_token", value)

    var refreshToken: String?
        get() = read("refresh_token")
        set(value) = write("refresh_token", value)

    var pkceVerifier: String?
        get() = read("pkce_verifier")
        set(value) = write("pkce_verifier", value)

    var pkceState: String?
        get() = read("pkce_state")
        set(value) = write("pkce_state", value)

    /** OAuth Client ID is a public identifier, so plain prefs are fine. */
    var clientId: String
        get() = plain.getString("client_id", "").orEmpty()
        set(value) = plain.edit().putString("client_id", value).apply()

    fun clearSession() {
        accessToken = null
        refreshToken = null
        pkceVerifier = null
        pkceState = null
    }
}
