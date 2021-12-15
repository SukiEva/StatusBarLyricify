package io.github.sukieva.statusBarLyricify.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import io.github.sukieva.statusBarLyricify.data.Media
import io.github.sukieva.statusBarLyricify.utils.LogUtil
import io.github.sukieva.statusBarLyricify.utils.Lyricify


/* API Document
https://developer.spotify.com/documentation/android/guides/android-media-notifications/
 */
class SpotifyReceiver : BroadcastReceiver() {

    private val TAG = "SpotifyReceiver"

    internal object BroadcastTypes {
        private const val SPOTIFY_PACKAGE = "com.spotify.music"
        const val PLAYBACK_STATE_CHANGED = "$SPOTIFY_PACKAGE.playbackstatechanged"
        const val QUEUE_CHANGED = "$SPOTIFY_PACKAGE.queuechanged"
        const val METADATA_CHANGED = "$SPOTIFY_PACKAGE.metadatachanged"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val timeSentInMs = intent.getLongExtra("timeSent", 0L)
        //Lyricify.updatePosition(positionInMs)
        when (intent.action) {
            BroadcastTypes.METADATA_CHANGED -> {
                LogUtil.d(TAG, "METADATA_CHANGED")
                //val trackId = intent.getStringExtra("id") ?: ""
                val artistName = intent.getStringExtra("artist") ?: ""
                val albumName = intent.getStringExtra("album") ?: ""
                val trackName = intent.getStringExtra("track") ?: ""
                val trackLengthInSec = intent.getIntExtra("length", 0).toLong()
                val data = Media(trackName, artistName, albumName, trackLengthInSec)
                //Lyricify.setLrc(data)
            }
            BroadcastTypes.PLAYBACK_STATE_CHANGED -> {
                LogUtil.d(TAG, "PLAYBACK_STATE_CHANGED")
                val isPlaying = intent.getBooleanExtra("playing", false)
                val positionInMs = intent.getIntExtra("playbackPosition", 0)
                Lyricify.updatePosition(positionInMs.toLong())
                if (isPlaying) Lyricify.startLyric()
                else Lyricify.stopLyric()
            }
            BroadcastTypes.QUEUE_CHANGED -> {
                LogUtil.d(TAG, "QUEUE_CHANGED")
            }
        }
    }


}