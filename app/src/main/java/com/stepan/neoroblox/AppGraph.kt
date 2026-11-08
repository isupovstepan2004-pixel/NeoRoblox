package com.stepan.neoroblox

import android.content.Context
import com.stepan.neoroblox.data.AuthManager
import com.stepan.neoroblox.data.TokenStore
import com.stepan.neoroblox.network.GamesApi
import com.stepan.neoroblox.network.ThumbsApi
import com.stepan.neoroblox.repository.RobloxRepository
import java.io.File
import java.util.concurrent.TimeUnit
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/** Tiny manual dependency container (keeps startup fast, no DI framework needed). */
class AppGraph(context: Context) {
    val tokenStore = TokenStore(context)

    private val http: OkHttpClient = OkHttpClient.Builder()
        .addInterceptor { chain ->
            chain.proceed(
                chain.request().newBuilder()
                    .header("User-Agent", "NeoRoblox/0.1 (independent third-party app)")
                    .build(),
            )
        }
        .cache(Cache(File(context.cacheDir, "http"), 20L * 1024 * 1024))
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .build()

    val auth = AuthManager(tokenStore, http)

    private fun retrofit(baseUrl: String): Retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(http)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val repository = RobloxRepository(
        games = retrofit("https://games.roblox.com/").create(GamesApi::class.java),
        thumbs = retrofit("https://thumbnails.roblox.com/").create(ThumbsApi::class.java),
    )
}
