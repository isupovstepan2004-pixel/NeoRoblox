package com.stepan.neoroblox.network

import com.stepan.neoroblox.network.dto.GameDetailsDto
import com.stepan.neoroblox.network.dto.GameListDto
import com.stepan.neoroblox.network.dto.GameThumbsDto
import com.stepan.neoroblox.network.dto.IconsDto
import com.stepan.neoroblox.network.dto.SortsDto
import retrofit2.http.GET
import retrofit2.http.Query

/** Public Roblox endpoints on games.roblox.com (no authentication needed). */
interface GamesApi {
    @GET("v1/games/sorts")
    suspend fun sorts(@Query("GameSortsContext") context: String = "GamesDefaultSorts"): SortsDto

    @GET("v1/games/list")
    suspend fun list(
        @Query("model.sortToken") token: String,
        @Query("model.maxRows") rows: Int = 20,
    ): GameListDto

    @GET("v1/games")
    suspend fun details(@Query("universeIds") ids: String): GameDetailsDto
}

/** Public Roblox endpoints on thumbnails.roblox.com. */
interface ThumbsApi {
    @GET("v1/games/icons")
    suspend fun gameIcons(
        @Query("universeIds") ids: String,
        @Query("size") size: String = "256x256",
        @Query("format") format: String = "Png",
    ): IconsDto

    @GET("v1/games/multiget/thumbnails")
    suspend fun gameThumbnails(
        @Query("universeIds") ids: String,
        @Query("size") size: String = "768x432",
        @Query("format") format: String = "Png",
        @Query("countPerUniverse") count: Int = 1,
    ): GameThumbsDto

    @GET("v1/users/avatar-headshot")
    suspend fun headshot(
        @Query("userIds") ids: String,
        @Query("size") size: String = "150x150",
        @Query("format") format: String = "Png",
    ): IconsDto
}
