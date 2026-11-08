package com.stepan.neoroblox

import android.app.Application

class NeoApp : Application() {
    override fun onCreate() {
        super.onCreate()
        graph = AppGraph(this)
    }

    companion object {
        lateinit var graph: AppGraph
            private set
    }
}
