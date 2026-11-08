package com.stepan.neoroblox.ui.screens.login

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stepan.neoroblox.NeoApp
import com.stepan.neoroblox.data.ApiException
import com.stepan.neoroblox.models.RobloxUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AuthUiState(
    val user: RobloxUser? = null,
    val avatarUrl: String? = null,
    val loading: Boolean = false,
    val configured: Boolean = false,
    val error: String? = null,
)

class AuthViewModel : ViewModel() {
    private val auth = NeoApp.graph.auth
    private val repository = NeoApp.graph.repository

    private val _state = MutableStateFlow(
        AuthUiState(loading = auth.hasSession(), configured = auth.isConfigured()),
    )
    val state: StateFlow<AuthUiState> = _state.asStateFlow()

    init {
        if (auth.hasSession()) {
            viewModelScope.launch {
                auth.restoreUser().fold(
                    onSuccess = { applyUser(it) },
                    onFailure = { e ->
                        _state.update {
                            it.copy(
                                loading = false,
                                error = if (e is ApiException) null else "Offline - couldn't refresh your profile",
                            )
                        }
                    },
                )
            }
        }
    }

    fun saveClientId(id: String) {
        auth.setClientId(id.trim())
        _state.update { it.copy(configured = auth.isConfigured(), error = null) }
    }

    fun startLogin(context: Context) {
        if (!auth.isConfigured()) {
            _state.update { it.copy(error = "Enter your Roblox OAuth Client ID first") }
            return
        }
        try {
            context.startActivity(Intent(Intent.ACTION_VIEW, auth.buildAuthUri()))
            _state.update { it.copy(error = null) }
        } catch (_: ActivityNotFoundException) {
            _state.update { it.copy(error = "No browser found to complete sign-in") }
        }
    }

    fun handleRedirect(uri: Uri) {
        viewModelScope.launch {
            _state.update { it.copy(loading = true, error = null) }
            auth.handleRedirect(uri).fold(
                onSuccess = { applyUser(it) },
                onFailure = { e ->
                    _state.update { it.copy(loading = false, error = e.message ?: "Sign-in failed") }
                },
            )
        }
    }

    fun signOut() {
        viewModelScope.launch {
            auth.signOut()
            _state.value = AuthUiState(configured = auth.isConfigured())
        }
    }

    private suspend fun applyUser(user: RobloxUser) {
        val avatar = repository.userHeadshot(user.id)
        _state.value = AuthUiState(user = user, avatarUrl = avatar, configured = true)
    }
}
