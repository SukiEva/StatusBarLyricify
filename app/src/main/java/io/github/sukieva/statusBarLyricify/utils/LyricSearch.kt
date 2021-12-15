package io.github.sukieva.statusBarLyricify.utils

import android.text.TextUtils
import io.github.sukieva.statusBarLyricify.data.Media
import org.json.JSONArray
import org.json.JSONException
import java.io.UnsupportedEncodingException
import java.lang.StringBuilder
import java.net.URLEncoder
import java.util.regex.Pattern
import kotlin.math.min


object LyricSearch {
    private val LyricContentPattern = Pattern.compile("(\\[\\d\\d:\\d\\d\\.\\d{0,3}]|\\[\\d\\d:\\d\\d])[^\\r\\n]")

    fun getSearchKey(metadata: Media): String {
        val title = metadata.title
        val album = metadata.album
        val artist = metadata.artist
        val ret: String = if (!TextUtils.isEmpty(artist)) {
            "$artist-$title"
        } else if (!TextUtils.isEmpty(album)) {
            "$album-$title"
        } else {
            title
        }
        return try {
            URLEncoder.encode(ret, "UTF-8")
        } catch (e: UnsupportedEncodingException) {
            ret
        }
    }

    fun parseArtists(jsonArray: JSONArray, key: String): String {
        try {
            val stringBuilder = StringBuilder()
            for (i in 0 until jsonArray.length()) {
                stringBuilder.append(jsonArray.getJSONObject(i).getString(key))
                if (i < jsonArray.length() - 1) stringBuilder.append('/')
            }
            return stringBuilder.toString()
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return ""
    }

    fun getMetadataDistance(metadata: Media, title: String, artist: String, album: String): Long {
        val realTitle = metadata.title
        val realArtist = metadata.artist
        val realAlbum = metadata.album
        if (!realTitle.contains(title) && !title.contains(realTitle) || TextUtils.isEmpty(title)) {
            return 10000
        }
        var res = levenshtein(title, realTitle) * 100L
        res += levenshtein(artist, realArtist) * 10L
        res += levenshtein(album, realAlbum).toLong()
        return res
    }

    private fun levenshtein(a: CharSequence, b: CharSequence): Int {
        if (TextUtils.isEmpty(a)) {
            return if (TextUtils.isEmpty(b)) 0 else b.length
        } else if (TextUtils.isEmpty(b)) {
            return if (TextUtils.isEmpty(a)) 0 else a.length
        }
        val lenA = a.length
        val lenB = b.length
        val dp = Array(lenA + 1) { IntArray(lenB + 1) }
        var flag: Int
        for (i in 0..lenA) {
            for (j in 0..lenB) dp[i][j] = lenA + lenB
        }
        for (i in 1..lenA) dp[i][0] = i
        for (j in 1..lenB) dp[0][j] = j
        for (i in 1..lenA) {
            for (j in 1..lenB) {
                flag = if (a[i - 1] == b[j - 1]) {
                    0
                } else {
                    1
                }
                dp[i][j] = min(dp[i - 1][j - 1] + flag, min(dp[i - 1][j] + 1, dp[i][j - 1] + 1))
            }
        }
        return dp[lenA][lenB]
    }

    fun isLyricContent(content: String): Boolean {
        return if (TextUtils.isEmpty(content)) false else LyricContentPattern.matcher(content).find()
    }
}