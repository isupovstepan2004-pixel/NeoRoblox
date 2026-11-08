package com.stepan.neoroblox.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.matchParentSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.stepan.neoroblox.models.GameSummary
import com.stepan.neoroblox.ui.theme.NeoColors
import com.stepan.neoroblox.utils.formatCount

@Composable
fun RemoteImage(url: String?, modifier: Modifier, shape: Shape) {
    val context = LocalContext.current
    val request = remember(url) {
        ImageRequest.Builder(context).data(url).crossfade(true).build()
    }
    Box(modifier.clip(shape)) {
        // Static placeholder (no running animation behind loaded images -> saves battery).
        Box(Modifier.matchParentSize().background(Color(0x14FFFFFF)))
        if (url != null) {
            AsyncImage(
                model = request,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.matchParentSize(),
            )
        }
    }
}

@Composable
fun GameCard(game: GameSummary, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Column(modifier.width(140.dp).pressScale(onClick)) {
        RemoteImage(game.iconUrl, Modifier.size(140.dp), RoundedCornerShape(18.dp))
        Spacer(Modifier.height(8.dp))
        Text(
            text = game.name,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
        )
        game.playerCount?.let {
            Text(
                text = "${formatCount(it.toLong())} playing",
                style = MaterialTheme.typography.labelSmall,
                color = NeoColors.Teal,
            )
        }
    }
}
