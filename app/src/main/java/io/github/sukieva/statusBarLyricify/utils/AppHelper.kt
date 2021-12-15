package io.github.sukieva.statusBarLyricify.utils

import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.widget.Toast
import com.github.stuxuhai.jpinyin.ChineseHelper
import io.github.sukieva.statusBarLyricify.MyApp
import io.github.sukieva.statusBarLyricify.data.Media
import io.github.sukieva.statusBarLyricify.receiver.SpotifyReceiver
import io.github.sukieva.statusBarLyricify.service.MusicListenerService

fun String.toast(duration: Int = Toast.LENGTH_SHORT) =
    Toast.makeText(MyApp.context, this, duration).show()

fun browse(url: String) {
    val intent = Intent(Intent.ACTION_VIEW)
    intent.data = Uri.parse(url)
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
    MyApp.context.startActivity(intent)
}

val receiver: SpotifyReceiver = SpotifyReceiver()

fun register() {
    val filter = IntentFilter()
    filter.addAction("com.spotify.music.playbackstatechanged")
    filter.addAction("com.spotify.music.metadatachanged")
    filter.addAction("com.spotify.music.queuechanged")
    MyApp.context.registerReceiver(receiver, filter)
}

fun unRegister() {
    MyApp.context.unregisterReceiver(receiver)
}

fun startServe() {
    val intent = Intent(MyApp.context, MusicListenerService::class.java)
    MyApp.context.startService(intent)
}

fun stopServe() {
    val intent = Intent(MyApp.context, MusicListenerService::class.java)
    MyApp.context.stopService(intent)
}

fun Media.toChinese(): Media {
    return Media(this.title.toChinese(), this.artist.toChinese(), this.album.toChinese(), this.duration)
}

fun String.toChinese(): String {
    if (this == "" || !ChineseHelper.containsChinese(this)) return this
    return ChineseHelper.convertToSimplifiedChinese(this)
}