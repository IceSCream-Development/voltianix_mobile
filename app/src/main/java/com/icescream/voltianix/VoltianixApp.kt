package com.icescream.voltianix

import android.app.Application
import android.content.Context
import dagger.hilt.android.HiltAndroidApp
import org.osmdroid.config.Configuration

@HiltAndroidApp
class VoltianixApp : Application() {
    override fun onCreate() {
        super.onCreate()

        // load() es el que decide dónde vive la caché de teselas. Sin él, osmdroid se queda con
        // la ruta de almacenamiento "externo", que en Android 10+ normalmente no es escribible:
        // el mkdirs() falla en silencio, se pierde el respaldo a getFilesDir() y el mapa vuelve
        // a descargar las teselas en cada paneo.
        Configuration.getInstance().load(
            this,
            getSharedPreferences("osmdroid", Context.MODE_PRIVATE)
        )

        // Importante: OSMDroid necesita un User-Agent para no ser bloqueado por los servidores de
        // mapas. Va después del load(), que lo sobrescribe con lo que tenga guardado.
        Configuration.getInstance().userAgentValue = packageName
    }
}
