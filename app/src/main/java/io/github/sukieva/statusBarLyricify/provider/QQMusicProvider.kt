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

class QQMusicProvider : ILrcProvider {

    override suspend fun getLyric(data: Media): ILrcProvider.LyricResult? {
        val searchUrl = java.lang.String.format(Locale.getDefault(), QM_SEARCH_URL_FORMAT, LyricSearch.getSearchKey(data))
        val searchResult: JSONObject?
        try {
            searchResult = EasyOkhttp.request(searchUrl, QM_REFERER)
            if (searchResult != null && searchResult.getLong("code") == 0L) {
                val array = searchResult.getJSONObject("data").getJSONObject("song").getJSONArray("list")
                val pair = getLrcUrl(array, data)
                val lrcJson = EasyOkhttp.request(pair.first, QM_REFERER)
                val result = ILrcProvider.LyricResult()
                if (lrcJson != null) {
                    result.mLyric = String(Base64.decode(lrcJson.getString("lyric").toByteArray(), Base64.DEFAULT))
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
        private const val QM_BASE_URL = "https://c.y.qq.com/"
        private const val QM_REFERER = "https://y.qq.com"
        private const val QM_SEARCH_URL_FORMAT = QM_BASE_URL + "soso/fcgi-bin/client_search_cp?w=%s&format=json"
        private const val QM_LRC_URL_FORMAT = QM_BASE_URL + "lyric/fcgi-bin/fcg_query_lyric_yqq.fcg?songmid=%s&format=json"

        private fun getLrcUrl(jsonArray: JSONArray, Media: Media): Pair<String, Long> {
            var currentMID = ""
            var minDistance: Long = 10000
            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                val soundName = jsonObject.getString("songname")
                val albumName = jsonObject.getString("albumname")
                val singers = jsonObject.getJSONArray("singer")
                val dis: Long = LyricSearch.getMetadataDistance(Media, soundName, LyricSearch.parseArtists(singers, "name"), albumName)
                if (dis < minDistance) {
                    minDistance = dis
                    currentMID = jsonObject.getString("songmid")
                }
            }
            return Pair(String.format(Locale.getDefault(), QM_LRC_URL_FORMAT, currentMID), minDistance)
        }
    }
}