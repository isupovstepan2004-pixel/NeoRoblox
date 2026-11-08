package com.stepan.neoroblox.models

data class RobloxUser(
    val id: String,
    val username: String,
    val displayName: String,
    val profileUrl: String?,
)

data class GameSummary(
    val universeId: Long,
    val placeId: Long,
    val name: String,
    val playerCount: Int? = null,
    val upVotes: Long? = null,
    val downVotes: Long? = null,
    val creatorName: String? = null,
    val iconUrl: String? = null,
    val bannerUrl: String? = null,
    val description: String? = null,
)

data class GameSort(val title: String, val games: List<GameSummary>)

data class HomeData(val featured: GameSummary?, val sorts: List<GameSort>)
