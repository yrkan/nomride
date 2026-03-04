package com.nomride

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.nomride.karoo.NomRideExtension
import timber.log.Timber

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED ||
            intent.action == "android.intent.action.QUICKBOOT_POWERON"
        ) {
            Timber.d("Boot completed, starting NomRide service")
            val serviceIntent = Intent(context, NomRideExtension::class.java)
            context.startForegroundService(serviceIntent)
        }
    }
}
