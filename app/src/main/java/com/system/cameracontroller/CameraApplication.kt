package com.system.cameracontroller

import android.app.Application
import kotlin.properties.Delegates

class CameraApplication:Application() {

    companion object {
        var instance: CameraApplication by Delegates.notNull()
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}