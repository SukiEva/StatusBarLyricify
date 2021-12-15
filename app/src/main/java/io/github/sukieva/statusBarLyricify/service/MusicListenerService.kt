package io.github.sukieva.statusBarLyricify.service

import android.app.Service
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import io.github.sukieva.statusBarLyricify.receiver.SpotifyReceiver
import io.github.sukieva.statusBarLyricify.utils.register
import io.github.sukieva.statusBarLyricify.utils.unRegister

class MusicListenerService : Service() {

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        register()
    }

    override fun onDestroy() {
        super.onDestroy()
        unRegister()
    }
}