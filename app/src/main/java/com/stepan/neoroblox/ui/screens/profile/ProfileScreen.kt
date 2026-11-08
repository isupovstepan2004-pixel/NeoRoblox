package com.stepan.neoroblox.ui.screens.profile

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.stepan.neoroblox.ui.components.GlassCard
import com.stepan.neoroblox.ui.components.RemoteImage
import com.stepan.neoroblox.ui.components.pressScale
import com.stepan.neoroblox.ui.screens.login.AuthViewModel
import com.stepan.neoroblox.ui.theme.NeoColors

@Composable
fun ProfileScreen(vm: AuthViewModel) {
    val state by vm.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val user = state.user

    Column(
        Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.height(24.dp))
        when {
            state.loading -> CircularProgressIndicator(color = NeoColors.Violet)
            user == null -> {
                Text("You're not signed in", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                Text(
                    "Sign in with Roblox to see your profile.",
                    color = NeoColors.TextSecondary,
                    textAlign = TextAlign.Center,
                )
                state.error?.let {
                    Spacer(Modifier.height(8.dp))
                    Text(it, color = NeoColors.Danger, textAlign = TextAlign.Center)
                }
                Spacer(Modifier.height(20.dp))
                Box(
                    Modifier.pressScale { vm.startLogin(context) }
                        .clip(RoundedCornerShape(16.dp)).background(NeoColors.accentBrush)
                        .padding(horizontal = 28.dp, vertical = 14.dp),
                ) { Text("Sign in with Roblox", fontWeight = FontWeight.Bold, color = Color.White) }
            }
            else -> {
                RemoteImage(state.avatarUrl, Modifier.size(110.dp), CircleShape)
                Spacer(Modifier.height(14.dp))
                Text(user.displayName, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold)
                Text("@${user.username}", color = NeoColors.TextSecondary)
                Spacer(Modifier.height(20.dp))
                GlassCard(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp)) {
                        Text("User ID", style = MaterialTheme.typography.labelSmall, color = NeoColors.TextSecondary)
                        Text(user.id, fontWeight = FontWeight.SemiBold)
                    }
                }
                Spacer(Modifier.height(12.dp))
                user.profileUrl?.let { url ->
                    TextButton(onClick = {
                        context.startActivity(
                            Intent(Intent.ACTION_VIEW, Uri.parse(url)).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
                        )
                    }) { Text("Open profile on roblox.com") }
                }
                TextButton(onClick = { vm.signOut() }) { Text("Sign out", color = NeoColors.Danger) }
            }
        }
        Spacer(Modifier.weight(1f))
        Text(
            "NeoRoblox is an independent third-party app, not affiliated with Roblox Corporation.",
            style = MaterialTheme.typography.labelSmall,
            color = NeoColors.TextSecondary,
            textAlign = TextAlign.Center,
        )
    }
}
