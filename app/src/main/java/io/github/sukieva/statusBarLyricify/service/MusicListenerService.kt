package io.github.sukieva.statusBarLyricify.service

import android.app.Service
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import io.github.sukieva.statusBarLyricify.receiver.SpotifyReceiver

class MusicListenerService : Service() {

    private lateinit var receiver: SpotifyReceiver

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        receiver = SpotifyReceiver()
        val filter = IntentFilter()
        filter.addAction("com.spotify.music.playbackstatechanged")
        filter.addAction("com.spotify.music.metadatachanged")
        filter.addAction("com.spotify.music.queuechanged")
        registerReceiver(receiver, filter)
    }


    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
    }
}