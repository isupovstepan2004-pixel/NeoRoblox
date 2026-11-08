package com.stepan.neoroblox

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.stepan.neoroblox.navigation.NeoNavHost
import com.stepan.neoroblox.ui.screens.login.AuthViewModel
import com.stepan.neoroblox.ui.theme.NeoTheme

class MainActivity : ComponentActivity() {
    private val authVm: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        if (savedInstanceState == null) handleIntent(intent)
        setContent { NeoTheme { NeoNavHost(authVm) } }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    /** OAuth redirect: neoroblox://oauth?code=...&state=... */
    private fun handleIntent(intent: Intent?) {
        val data = intent?.data ?: return
        if (data.scheme == "neoroblox" && data.host == "oauth") authVm.handleRedirect(data)
    }
}
