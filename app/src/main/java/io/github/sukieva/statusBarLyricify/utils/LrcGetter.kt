package io.github.sukieva.statusBarLyricify.utils

import android.content.Context
import cn.zhaiyifan.lyric.LyricUtils
import cn.zhaiyifan.lyric.model.Lyric
import io.github.sukieva.statusBarLyricify.data.Media
import io.github.sukieva.statusBarLyricify.provider.ILrcProvider.LyricResult
import io.github.sukieva.statusBarLyricify.provider.NeteaseProvider
import io.github.sukieva.statusBarLyricify.provider.QQMusicProvider
import io.github.sukieva.statusBarLyricify.provider.KugouProvider
import okhttp3.internal.and
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.lang.StringBuilder
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException


object LrcGetter {
    private val providers = arrayOf(
        KugouProvider(),
        QQMusicProvider(),
        NeteaseProvider()
    )
    private var messageDigest: MessageDigest? = null
    private val hexCode = "0123456789ABCDEF".toCharArray()
    suspend fun getLyric(context: Context, mediaMetadata: Media): Lyric? {
        if (messageDigest == null) {
            try {
                messageDigest = MessageDigest.getInstance("SHA")
            } catch (e: NoSuchAlgorithmException) {
                e.printStackTrace()
                return null
            }
        }
        val cachePath: File = context.cacheDir
        val meta = mediaMetadata.title + "," + mediaMetadata.artist + "," +
                mediaMetadata.album + ", " + mediaMetadata.duration
        val requireLrcPath = File(cachePath, printHexBinary(messageDigest!!.digest(meta.toByteArray())) + ".lrc")
        if (requireLrcPath.exists()) {
            return LyricUtils.parseLyric(requireLrcPath, "UTF-8")
        }
        var currentResult: LyricResult? = null
        for (provider in providers) {
            try {
                val lyricResult = provider.getLyric(mediaMetadata)
                if (lyricResult != null && LyricSearch.isLyricContent(lyricResult.mLyric) && (currentResult == null || currentResult.mDistance > lyricResult.mDistance)) {
                    currentResult = lyricResult
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        if (currentResult != null && LyricSearch.isLyricContent(currentResult.mLyric)) {
            try {
                val lrcOut = FileOutputStream(requireLrcPath)
                lrcOut.write(currentResult.mLyric.toByteArray())
                lrcOut.close()
                return LyricUtils.parseLyric(requireLrcPath, "UTF-8")
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return null
    }

    private fun printHexBinary(data: ByteArray): String {
        val r = StringBuilder(data.size * 2)
        for (b in data) {
            r.append(hexCode[(b.toInt() shr 4).toByte() and 0xF])
            r.append(hexCode[b and 0xF])
        }
        return r.toString()
    }
}