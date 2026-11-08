package com.stepan.neoroblox.utils

import java.util.Locale

fun formatCount(n: Long): String {
    fun fmt(v: Double, suffix: String) =
        String.format(Locale.US, "%.1f", v).removeSuffix(".0") + suffix
    return when {
        n >= 1_000_000_000 -> fmt(n / 1e9, "B")
        n >= 1_000_000 -> fmt(n / 1e6, "M")
        n >= 1_000 -> fmt(n / 1e3, "K")
        else -> n.toString()
    }
}
