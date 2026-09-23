package com.icescream.voltianix

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import org.osmdroid.config.Configuration

@HiltAndroidApp
class VoltianixApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // Importante: OSMDroid necesita un User-Agent para no ser bloqueado por los servidores de mapas
        Configuration.getInstance().userAgentValue = packageName
    }
}
