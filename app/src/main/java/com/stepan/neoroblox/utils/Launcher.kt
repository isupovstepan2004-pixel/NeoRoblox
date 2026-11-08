package com.stepan.neoroblox.utils

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri

enum class LaunchResult { LAUNCHED, STORE_OPENED, FAILED }

/** Starts games through the official Roblox app only (official deep links). */
object Launcher {
    const val ROBLOX_PACKAGE = "com.roblox.client"

    fun isInstalled(context: Context): Boolean = try {
        context.packageManager.getPackageInfo(ROBLOX_PACKAGE, 0)
        true
    } catch (_: PackageManager.NameNotFoundException) {
        false
    }

    fun play(context: Context, placeId: Long): LaunchResult {
        val links = listOf(
            "roblox://experiences/start?placeId=$placeId",
            "roblox://placeId=$placeId",
        )
        for (link in links) {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
                .setPackage(ROBLOX_PACKAGE)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            try {
                context.startActivity(intent)
                return LaunchResult.LAUNCHED
            } catch (_: ActivityNotFoundException) {
                // try next link
            }
        }
        return openStore(context)
    }

    fun openStore(context: Context): LaunchResult {
        val uris = listOf(
            "market://details?id=$ROBLOX_PACKAGE",
            "https://play.google.com/store/apps/details?id=$ROBLOX_PACKAGE",
        )
        for (u in uris) {
            try {
                context.startActivity(
                    Intent(Intent.ACTION_VIEW, Uri.parse(u)).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
                )
                return LaunchResult.STORE_OPENED
            } catch (_: ActivityNotFoundException) {
                // try next
            }
        }
        return LaunchResult.FAILED
    }
}
