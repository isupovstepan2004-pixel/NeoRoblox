package com.stepan.neoroblox.repository

import com.stepan.neoroblox.models.GameSort
import com.stepan.neoroblox.models.GameSummary
import com.stepan.neoroblox.models.HomeData
import com.stepan.neoroblox.network.GamesApi
import com.stepan.neoroblox.network.ThumbsApi
import com.stepan.neoroblox.network.dto.GameItemDto
import java.io.IOException
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

class RobloxRepository(
    private val games: GamesApi,
    private val thumbs: ThumbsApi,
) {
    /** Loads real Roblox game lists. Throws on network failure so the UI can show an error state. */
    suspend fun loadHome(): HomeData = coroutineScope {
        val sorts = games.sorts().sorts.orEmpty()
            .filter { !it.token.isNullOrBlank() }
            .take(4)
        if (sorts.isEmpty()) throw IOException("Roblox returned no game lists")

        val lists = sorts.map { sort ->
            async {
                val title = sort.displayName ?: sort.name ?: "Games"
                val items = runCatching { games.list(sort.token!!, 20).games.orEmpty() }
                    .getOrDefault(emptyList())
                title to items.mapNotNull { it.toModel() }.distinctBy { it.universeId }
            }
        }.awaitAll().filter { it.second.isNotEmpty() }
        if (lists.isEmpty()) throw IOException("Roblox returned empty game lists")

        val icons = fetchIcons(lists.flatMap { it.second }.map { it.universeId }.distinct())
        val gameSorts = lists.map { (title, items) ->
            GameSort(title, items.map { it.copy(iconUrl = icons[it.universeId]) })
        }
        HomeData(featured = buildFeatured(gameSorts.first().games.first()), sorts = gameSorts)
    }

    suspend fun userHeadshot(userId: String): String? =
        runCatching { thumbs.headshot(userId).data?.firstOrNull()?.imageUrl }.getOrNull()

    private suspend fun fetchIcons(ids: List<Long>): Map<Long, String> {
        val result = HashMap<Long, String>()
        ids.chunked(50).forEach { chunk ->
            runCatching { thumbs.gameIcons(chunk.joinToString(",")).data.orEmpty() }
                .getOrDefault(emptyList())
                .forEach { icon ->
                    val id = icon.targetId
                    val url = icon.imageUrl
                    if (id != null && url != null) result[id] = url
                }
        }
        return result
    }

    private suspend fun buildFeatured(base: GameSummary): GameSummary {
        val banner = runCatching {
            thumbs.gameThumbnails(base.universeId.toString()).data
                ?.firstOrNull()?.thumbnails?.firstOrNull()?.imageUrl
        }.getOrNull()
        val description = runCatching {
            games.details(base.universeId.toString()).data?.firstOrNull()?.description?.trim()
        }.getOrNull()?.takeIf { it.isNotEmpty() }
        return base.copy(bannerUrl = banner ?: base.iconUrl, description = description)
    }

    private fun GameItemDto.toModel(): GameSummary? {
        val uid = universeId ?: return null
        val pid = placeId ?: return null
        val title = name?.takeIf { it.isNotBlank() } ?: return null
        return GameSummary(
            universeId = uid,
            placeId = pid,
            name = title,
            playerCount = playerCount,
            upVotes = totalUpVotes,
            downVotes = totalDownVotes,
            creatorName = creatorName,
        )
    }
}
