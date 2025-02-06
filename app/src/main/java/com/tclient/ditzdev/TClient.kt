package com.tclient.ditzdev;

import android.app.Application
import com.tclient.ditzdev.handler.CrashHandler

class TClient : Application() {
    override fun onCreate() {
        super.onCreate()
        CrashHandler.initialize(this)
    }
}