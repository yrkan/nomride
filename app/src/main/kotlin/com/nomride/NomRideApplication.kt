package com.nomride

import android.app.Application
import timber.log.Timber

class NomRideApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
    }
}
