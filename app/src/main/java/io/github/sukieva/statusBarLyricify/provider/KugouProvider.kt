package io.github.sukieva.statusBarLyricify.provider

import android.util.Base64
import android.util.Pair
import io.github.sukieva.statusBarLyricify.data.Media
import io.github.sukieva.statusBarLyricify.utils.EasyOkhttp
import io.github.sukieva.statusBarLyricify.utils.LyricSearch
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class KugouProvider : ILrcProvider {

    override suspend fun getLyric(data: Media): ILrcProvider.LyricResult? {
        val searchUrl = String.format(Locale.getDefault(), KUGOU_SEARCH_URL_FORMAT, LyricSearch.getSearchKey(data), data.duration)
        val searchResult: JSONObject?
        try {
            searchResult = EasyOkhttp.request(searchUrl)
            if (searchResult != null && searchResult.getLong("status") == 200L) {
                val array = searchResult.getJSONArray("candidates")
                val pair = getLrcUrl(array, data)
                val lrcJson = EasyOkhttp.request(pair.first)
                val result = ILrcProvider.LyricResult()
                if (lrcJson != null) {
                    result.mLyric = String(Base64.decode(lrcJson.getString("content").toByteArray(), Base64.DEFAULT))
                }
                result.mDistance = pair.second
                return result
            }
        } catch (e: JSONException) {
            e.printStackTrace()
            return null
        }
        return null
    }

    companion object {
        private const val KUGOU_BASE_URL = "http://lyrics.kugou.com/"
        private const val KUGOU_SEARCH_URL_FORMAT = KUGOU_BASE_URL + "search?ver=1&man=yes&client=pc&keyword=%s&duration=%d"
        private const val KUGOU_LRC_URL_FORMAT = KUGOU_BASE_URL + "download?ver=1&client=pc&id=%d&accesskey=%s&fmt=lrc&charset=utf8"

        private fun getLrcUrl(jsonArray: JSONArray, mediaMetadata: Media): Pair<String, Long> {
            var currentAccessKey = ""
            var minDistance: Long = 10000
            var currentId: Long = -1
            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                val soundName = jsonObject.getString("soundname")
                val artist = jsonObject.getString("singer")
                val dis = LyricSearch.getMetadataDistance(mediaMetadata, soundName, artist, "")
                if (dis < minDistance) {
                    minDistance = dis
                    currentId = jsonObject.getLong("id")
                    currentAccessKey = jsonObject.getString("accesskey")
                }
            }
            return Pair(String.format(Locale.getDefault(), KUGOU_LRC_URL_FORMAT, currentId, currentAccessKey), minDistance)
        }
    }

}