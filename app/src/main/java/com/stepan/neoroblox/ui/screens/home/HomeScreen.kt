package com.stepan.neoroblox.ui.screens.home

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.stepan.neoroblox.models.GameSummary
import com.stepan.neoroblox.ui.components.GameCard
import com.stepan.neoroblox.ui.components.GlassCard
import com.stepan.neoroblox.ui.components.RemoteImage
import com.stepan.neoroblox.ui.components.SkeletonBox
import com.stepan.neoroblox.ui.components.pressScale
import com.stepan.neoroblox.ui.theme.NeoColors
import com.stepan.neoroblox.utils.LaunchResult
import com.stepan.neoroblox.utils.Launcher
import com.stepan.neoroblox.utils.formatCount

@Composable
fun HomeScreen(
    avatarUrl: String?,
    onOpenProfile: () -> Unit,
    vm: HomeViewModel = viewModel(),
) {
    val state by vm.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val play: (GameSummary) -> Unit = { game ->
        when (Launcher.play(context, game.placeId)) {
            LaunchResult.LAUNCHED -> Unit
            LaunchResult.STORE_OPENED ->
                Toast.makeText(context, "Roblox isn't installed - opening Google Play", Toast.LENGTH_LONG).show()
            LaunchResult.FAILED ->
                Toast.makeText(context, "Couldn't open Roblox", Toast.LENGTH_LONG).show()
        }
    }

    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = 24.dp)) {
        item { TopBar(avatarUrl, onRefresh = vm::load, onOpenProfile = onOpenProfile) }
        when (val s = state) {
            HomeUiState.Loading -> {
                item {
                    SkeletonBox(
                        Modifier.padding(horizontal = 20.dp).fillMaxWidth().height(230.dp),
                        RoundedCornerShape(28.dp),
                    )
                }
                items(2) {
                    Spacer(Modifier.height(24.dp))
                    SkeletonBox(Modifier.padding(horizontal = 20.dp).size(width = 140.dp, height = 20.dp))
                    Spacer(Modifier.height(12.dp))
                    Row(Modifier.padding(horizontal = 20.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        repeat(3) { SkeletonBox(Modifier.size(140.dp), RoundedCornerShape(18.dp)) }
                    }
                }
            }
            is HomeUiState.Error -> item {
                ErrorCard(s.message, onRetry = vm::load)
            }
            is HomeUiState.Success -> {
                s.data.featured?.let { featured ->
                    item { FeaturedCard(featured, onPlay = { play(featured) }) }
                }
                items(s.data.sorts) { sort ->
                    Spacer(Modifier.height(24.dp))
                    Text(
                        text = sort.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 20.dp),
                    )
                    Spacer(Modifier.height(12.dp))
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        items(sort.games, key = { it.universeId }) { game ->
                            GameCard(game, onClick = { play(game) })
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TopBar(avatarUrl: String?, onRefresh: () -> Unit, onOpenProfile: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "NeoRoblox",
            style = TextStyle(brush = NeoColors.accentBrush, fontSize = 26.sp, fontWeight = FontWeight.ExtraBold),
        )
        Spacer(Modifier.weight(1f))
        IconButton(onClick = onRefresh) {
            Icon(Icons.Filled.Refresh, contentDescription = "Refresh", tint = NeoColors.TextSecondary)
        }
        Box(
            Modifier.size(40.dp).pressScale(onOpenProfile).clip(CircleShape)
                .background(Color.White.copy(alpha = 0.08f)),
            contentAlignment = Alignment.Center,
        ) {
            if (avatarUrl != null) {
                RemoteImage(avatarUrl, Modifier.size(40.dp), CircleShape)
            } else {
                Icon(Icons.Filled.Person, contentDescription = "Profile", tint = NeoColors.TextSecondary)
            }
        }
    }
}

@Composable
private fun FeaturedCard(game: GameSummary, onPlay: () -> Unit) {
    Box(
        Modifier.padding(horizontal = 20.dp).fillMaxWidth().height(230.dp)
            .clip(RoundedCornerShape(28.dp)),
    ) {
        RemoteImage(game.bannerUrl ?: game.iconUrl, Modifier.fillMaxSize(), RoundedCornerShape(28.dp))
        Box(
            Modifier.fillMaxSize().background(
                Brush.verticalGradient(listOf(Color.Transparent, Color(0xE6070B14))),
            ),
        )
        Column(Modifier.align(Alignment.BottomStart).padding(18.dp)) {
            Text(
                "FEATURED",
                style = MaterialTheme.typography.labelSmall,
                color = NeoColors.Teal,
                fontWeight = FontWeight.Bold,
            )
            Text(
                game.name,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            game.description?.let {
                Text(
                    it,
                    style = MaterialTheme.typography.bodySmall,
                    color = NeoColors.TextSecondary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Spacer(Modifier.height(10.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Row(
                    Modifier.pressScale(onPlay).clip(RoundedCornerShape(14.dp))
                        .background(NeoColors.accentBrush)
                        .padding(horizontal = 20.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(Icons.Filled.PlayArrow, contentDescription = null, tint = Color.White)
                    Text("Play", fontWeight = FontWeight.Bold, color = Color.White)
                }
                game.playerCount?.let {
                    Spacer(Modifier.size(14.dp))
                    Text(
                        "${formatCount(it.toLong())} playing",
                        style = MaterialTheme.typography.labelMedium,
                        color = NeoColors.Teal,
                    )
                }
            }
        }
    }
}

@Composable
private fun ErrorCard(message: String, onRetry: () -> Unit) {
    GlassCard(Modifier.padding(horizontal = 20.dp).fillMaxWidth()) {
        Column(Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Couldn't load games", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(6.dp))
            Text(message, color = NeoColors.TextSecondary, style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(14.dp))
            Box(
                Modifier.pressScale(onRetry).clip(RoundedCornerShape(14.dp))
                    .background(NeoColors.accentBrush)
                    .padding(horizontal = 22.dp, vertical = 10.dp),
            ) { Text("Retry", fontWeight = FontWeight.Bold, color = Color.White) }
        }
    }
}
