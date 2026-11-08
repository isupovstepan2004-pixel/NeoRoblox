package com.stepan.neoroblox.data

import android.net.Uri
import com.stepan.neoroblox.BuildConfig
import com.stepan.neoroblox.models.RobloxUser
import java.io.IOException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

class ApiException(val code: Int, message: String) : IOException(message)

/**
 * Official "Sign in with Roblox": OAuth 2.0 authorization code flow + PKCE (public client).
 * The password never touches this app, and no cookies are read.
 */
class AuthManager(
    private val store: TokenStore,
    private val http: OkHttpClient,
) {
    fun clientId(): String = BuildConfig.ROBLOX_CLIENT_ID.ifBlank { store.clientId }
    fun isConfigured(): Boolean = clientId().isNotBlank()
    fun setClientId(id: String) { store.clientId = id }
    fun hasSession(): Boolean = store.refreshToken != null || store.accessToken != null

    fun buildAuthUri(): Uri {
        val verifier = Pkce.newVerifier()
        val state = Pkce.newVerifier().take(32)
        store.pkceVerifier = verifier
        store.pkceState = state
        return Uri.parse(AUTH_URL).buildUpon()
            .appendQueryParameter("client_id", clientId())
            .appendQueryParameter("response_type", "code")
            .appendQueryParameter("redirect_uri", BuildConfig.REDIRECT_URI)
            .appendQueryParameter("scope", "openid profile")
            .appendQueryParameter("code_challenge", Pkce.challenge(verifier))
            .appendQueryParameter("code_challenge_method", "S256")
            .appendQueryParameter("state", state)
            .build()
    }

    suspend fun handleRedirect(uri: Uri): Result<RobloxUser> = runCatching {
        uri.getQueryParameter("error")?.let { code ->
            throw IOException(uri.getQueryParameter("error_description") ?: code)
        }
        val code = uri.getQueryParameter("code") ?: throw IOException("No authorization code received")
        val verifier = store.pkceVerifier ?: throw IOException("Sign-in session expired, please try again")
        if (uri.getQueryParameter("state") != store.pkceState) throw IOException("Sign-in state mismatch")
        store.pkceVerifier = null
        store.pkceState = null

        val tokens = post(
            TOKEN_URL,
            mapOf(
                "grant_type" to "authorization_code",
                "code" to code,
                "client_id" to clientId(),
                "code_verifier" to verifier,
                "redirect_uri" to BuildConfig.REDIRECT_URI,
            ),
        )
        saveTokens(tokens)
        fetchUser(store.accessToken ?: throw IOException("No access token received"))
    }

    suspend fun restoreUser(): Result<RobloxUser> = runCatching {
        try {
            fetchUser(store.accessToken ?: throw ApiException(401, "no access token"))
        } catch (e: ApiException) {
            if (e.code != 401 && e.code != 403) throw e
            refresh()
            fetchUser(store.accessToken ?: throw IOException("No access token"))
        }
    }.onFailure { e ->
        // Refresh token rejected: the session is really gone.
        if (e is ApiException && (e.code == 400 || e.code == 401)) store.clearSession()
    }

    suspend fun signOut() {
        val token = store.refreshToken ?: store.accessToken
        if (token != null) {
            runCatching { post(REVOKE_URL, mapOf("token" to token, "client_id" to clientId())) }
        }
        store.clearSession()
    }

    private suspend fun refresh() {
        val refresh = store.refreshToken ?: throw ApiException(401, "No refresh token")
        val tokens = post(
            TOKEN_URL,
            mapOf("grant_type" to "refresh_token", "refresh_token" to refresh, "client_id" to clientId()),
        )
        saveTokens(tokens)
    }

    private fun saveTokens(json: JSONObject) {
        json.str("access_token")?.let { store.accessToken = it }
        json.str("refresh_token")?.let { store.refreshToken = it }
    }

    private suspend fun fetchUser(accessToken: String): RobloxUser = withContext(Dispatchers.IO) {
        val request = Request.Builder().url(USERINFO_URL)
            .header("Authorization", "Bearer $accessToken").build()
        http.newCall(request).execute().use { r ->
            val text = r.body?.string().orEmpty()
            if (!r.isSuccessful) throw ApiException(r.code, parseError(text) ?: "HTTP ${r.code}")
            val j = JSONObject(text)
            val id = j.str("sub") ?: throw IOException("Roblox did not return a user id")
            val username = j.str("preferred_username") ?: j.str("name") ?: id
            RobloxUser(
                id = id,
                username = username,
                displayName = j.str("nickname") ?: j.str("name") ?: username,
                profileUrl = j.str("profile") ?: "https://www.roblox.com/users/$id/profile",
            )
        }
    }

    private suspend fun post(url: String, form: Map<String, String>): JSONObject =
        withContext(Dispatchers.IO) {
            val body = FormBody.Builder().apply { form.forEach { (k, v) -> add(k, v) } }.build()
            http.newCall(Request.Builder().url(url).post(body).build()).execute().use { r ->
                val text = r.body?.string().orEmpty()
                if (!r.isSuccessful) throw ApiException(r.code, parseError(text) ?: "HTTP ${r.code}")
                JSONObject(text.ifBlank { "{}" })
            }
        }

    private fun parseError(text: String): String? = runCatching {
        val j = JSONObject(text)
        j.str("error_description") ?: j.str("error")
    }.getOrNull()

    private fun JSONObject.str(key: String): String? =
        optString(key, "").takeIf { it.isNotBlank() && it != "null" }

    private companion object {
        const val AUTH_URL = "https://apis.roblox.com/oauth/v1/authorize"
        const val TOKEN_URL = "https://apis.roblox.com/oauth/v1/token"
        const val REVOKE_URL = "https://apis.roblox.com/oauth/v1/token/revoke"
        const val USERINFO_URL = "https://apis.roblox.com/oauth/v1/userinfo"
    }
}
