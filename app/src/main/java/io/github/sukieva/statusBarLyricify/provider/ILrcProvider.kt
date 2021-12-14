package io.github.sukieva.statusBarLyricify.provider

import android.media.MediaMetadata


interface ILrcProvider {
    fun getLyric(data: MediaMetadata?): LyricResult?

    class LyricResult {
        var mLyric: String? = null
        var mDistance: Long = 0
    }
}