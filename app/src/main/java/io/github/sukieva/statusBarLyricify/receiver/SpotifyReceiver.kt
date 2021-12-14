package io.github.sukieva.statusBarLyricify.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import io.github.sukieva.statusBarLyricify.utils.LogUtil
import io.github.sukieva.statusBarLyricify.utils.toast

/* API Document
https://developer.spotify.com/documentation/android/guides/android-media-notifications/
 */
class SpotifyReceiver : BroadcastReceiver() {

    internal object BroadcastTypes {
        private const val SPOTIFY_PACKAGE = "com.spotify.music"
        const val PLAYBACK_STATE_CHANGED = "$SPOTIFY_PACKAGE.playbackstatechanged"
        const val QUEUE_CHANGED = "$SPOTIFY_PACKAGE.queuechanged"
        const val METADATA_CHANGED = "$SPOTIFY_PACKAGE.metadatachanged"
    }


    override fun onReceive(context: Context, intent: Intent) {
        val timeSentInMs = intent.getLongExtra("timeSent", 0L)
        when (intent.action) {
            BroadcastTypes.METADATA_CHANGED -> {
                val trackId = intent.getStringExtra("id")
                val artistName = intent.getStringExtra("artist")
                val albumName = intent.getStringExtra("album")
                val trackName = intent.getStringExtra("track")
                val trackLengthInSec = intent.getIntExtra("length", 0)
                // Do something with extracted information..
                "METADATA_CHANGED".toast()
                LogUtil.d("Spotify", "trackId = $trackId")
                LogUtil.d("Spotify", "artistName = $artistName")
                LogUtil.d("Spotify", "albumName = $albumName")
                LogUtil.d("Spotify", "trackName = $trackName")
                LogUtil.d("Spotify", "trackLengthInSec = $trackLengthInSec")
            }
            BroadcastTypes.PLAYBACK_STATE_CHANGED -> {
                val playing = intent.getBooleanExtra("playing", false)
                val positionInMs = intent.getIntExtra("playbackPosition", 0)
                // Do something with extracted information
                "PLAYBACK_STATE_CHANGED".toast()
                LogUtil.d("Spotify", "playing = $playing")
                LogUtil.d("Spotify\"", "positionInMs = $positionInMs")
            }
            BroadcastTypes.QUEUE_CHANGED -> {
                "QUEUE_CHANGED".toast()
                // Sent only as a notification, your app may want to respond accordingly.
            }
        }
    }
}