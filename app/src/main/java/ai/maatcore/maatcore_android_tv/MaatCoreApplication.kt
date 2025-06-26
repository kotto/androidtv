package ai.maatcore.maatcore_android_tv

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MaatCoreApplication : Application() {
    override fun onCreate() {
        super.onCreate()
    }
}