package com.stepan.neoroblox.utils

import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import retrofit2.HttpException

fun friendlyError(e: Throwable): String = when (e) {
    is UnknownHostException, is ConnectException, is SocketTimeoutException ->
        "No internet connection. Check your network and try again."
    is HttpException -> "Roblox servers returned an error (${e.code()}). Try again later."
    else -> e.message?.takeIf { it.isNotBlank() } ?: "Something went wrong."
}
