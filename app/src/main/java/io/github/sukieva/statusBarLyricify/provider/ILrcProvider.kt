package io.github.sukieva.statusBarLyricify.provider

import io.github.sukieva.statusBarLyricify.data.Media

interface ILrcProvider {
    suspend fun getLyric(data: Media): LyricResult?
    class LyricResult {
        var mLyric: String = ""
        var mDistance: Long = 0
    }
}
