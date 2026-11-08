package com.stepan.neoroblox.network.dto

data class SortsDto(val sorts: List<SortDto>? = null)
data class SortDto(val token: String? = null, val name: String? = null, val displayName: String? = null)

data class GameListDto(val games: List<GameItemDto>? = null)
data class GameItemDto(
    val universeId: Long? = null,
    val placeId: Long? = null,
    val name: String? = null,
    val playerCount: Int? = null,
    val totalUpVotes: Long? = null,
    val totalDownVotes: Long? = null,
    val creatorName: String? = null,
)

data class GameDetailsDto(val data: List<GameDetailDto>? = null)
data class GameDetailDto(
    val id: Long? = null,
    val description: String? = null,
    val playing: Long? = null,
    val visits: Long? = null,
    val genre: String? = null,
)

data class IconsDto(val data: List<IconDto>? = null)
data class IconDto(val targetId: Long? = null, val imageUrl: String? = null, val state: String? = null)

data class GameThumbsDto(val data: List<GameThumbEntryDto>? = null)
data class GameThumbEntryDto(val universeId: Long? = null, val thumbnails: List<IconDto>? = null)
