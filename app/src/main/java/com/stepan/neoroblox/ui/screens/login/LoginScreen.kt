package com.stepan.neoroblox.ui.screens.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.stepan.neoroblox.ui.components.GlassCard
import com.stepan.neoroblox.ui.components.pressScale
import com.stepan.neoroblox.ui.theme.NeoColors

@Composable
fun LoginScreen(vm: AuthViewModel, onSkip: () -> Unit) {
    val state by vm.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var clientIdInput by remember { mutableStateOf("") }

    Column(
        Modifier.fillMaxSize().padding(28.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            "NeoRoblox",
            style = TextStyle(brush = NeoColors.accentBrush, fontSize = 40.sp, fontWeight = FontWeight.ExtraBold),
        )
        Spacer(Modifier.height(8.dp))
        Text(
            "Your games, your profile, one tap from Roblox.",
            color = NeoColors.TextSecondary,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(32.dp))

        if (!state.configured) {
            GlassCard(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text("Roblox OAuth Client ID", fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "Create an OAuth 2.0 app at create.roblox.com (Credentials), redirect URI: neoroblox://oauth",
                        style = MaterialTheme.typography.bodySmall,
                        color = NeoColors.TextSecondary,
                    )
                    Spacer(Modifier.height(10.dp))
                    OutlinedTextField(
                        value = clientIdInput,
                        onValueChange = { clientIdInput = it },
                        singleLine = true,
                        label = { Text("Client ID") },
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Spacer(Modifier.height(10.dp))
                    TextButton(
                        onClick = { vm.saveClientId(clientIdInput) },
                        enabled = clientIdInput.isNotBlank(),
                    ) { Text("Save") }
                }
            }
            Spacer(Modifier.height(16.dp))
        }

        if (state.loading) {
            CircularProgressIndicator(color = NeoColors.Violet)
        } else {
            Box(
                Modifier.fillMaxWidth().pressScale { vm.startLogin(context) }
                    .clip(RoundedCornerShape(16.dp))
                    .background(NeoColors.accentBrush)
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text("Sign in with Roblox", fontWeight = FontWeight.Bold, color = Color.White)
            }
        }

        state.error?.let {
            Spacer(Modifier.height(12.dp))
            Text(it, color = NeoColors.Danger, textAlign = TextAlign.Center)
        }

        Spacer(Modifier.height(8.dp))
        TextButton(onClick = onSkip) { Text("Continue without signing in", color = NeoColors.TextSecondary) }

        Spacer(Modifier.height(24.dp))
        Text(
            "Sign-in happens on Roblox's official page. NeoRoblox never sees your password. " +
                "Independent third-party app, not affiliated with Roblox Corporation.",
            style = MaterialTheme.typography.labelSmall,
            color = NeoColors.TextSecondary,
            textAlign = TextAlign.Center,
        )
    }
}
