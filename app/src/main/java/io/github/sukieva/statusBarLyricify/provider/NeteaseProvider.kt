package io.github.sukieva.statusBarLyricify.provider

import android.util.Pair
import io.github.sukieva.statusBarLyricify.data.Media
import io.github.sukieva.statusBarLyricify.utils.EasyOkhttp
import io.github.sukieva.statusBarLyricify.utils.LyricSearch
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class NeteaseProvider : ILrcProvider {

    override suspend fun getLyric(data: Media): ILrcProvider.LyricResult? {
        val searchUrl = java.lang.String.format(NETEASE_SEARCH_URL_FORMAT, LyricSearch.getSearchKey(data))
        val searchResult: JSONObject?
        try {
            searchResult = EasyOkhttp.request(searchUrl)
            if (searchResult != null && searchResult.getLong("code") == 200L) {
                val array = searchResult.getJSONObject("result").getJSONArray("songs")
                val pair = getLrcUrl(array, data)
                val lrcJson = EasyOkhttp.request(pair.first)
                val result = ILrcProvider.LyricResult()
                if (lrcJson != null) {
                    result.mLyric = lrcJson.getJSONObject("lrc").getString("lyric")
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
        private const val NETEASE_BASE_URL = "http://music.163.com/api/"
        private const val NETEASE_SEARCH_URL_FORMAT = NETEASE_BASE_URL + "search/pc?s=%s&type=1&offset=0&limit=10"
        private const val NETEASE_LRC_URL_FORMAT = NETEASE_BASE_URL + "song/lyric?os=pc&id=%d&lv=-1&kv=-1&tv=-1"

        private fun getLrcUrl(jsonArray: JSONArray, Media: Media): Pair<String, Long> {
            var currentID: Long = -1
            var minDistance: Long = 10000
            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                val soundName = jsonObject.getString("name")
                val albumName = jsonObject.getJSONObject("album").getString("name")
                val artists = jsonObject.getJSONArray("artists")
                val dis = LyricSearch.getMetadataDistance(Media, soundName, LyricSearch.parseArtists(artists, "name"), albumName)
                if (dis < minDistance) {
                    minDistance = dis
                    currentID = jsonObject.getLong("id")
                }
            }
            return Pair(String.format(Locale.getDefault(), NETEASE_LRC_URL_FORMAT, currentID), minDistance)
        }
    }
}