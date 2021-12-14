package io.github.sukieva.statusBarLyricify.utils

import StatusBarLyric.API.StatusBarLyric
import android.annotation.SuppressLint
import android.text.TextUtils
import cn.zhaiyifan.lyric.LyricUtils
import cn.zhaiyifan.lyric.model.Lyric
import io.github.sukieva.statusBarLyricify.MyApp
import io.github.sukieva.statusBarLyricify.data.Media
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


object Lyricify {

    private const val SERVICE_NAME = "io.github.sukieva.statusBarLyricify.service.MusicListenerService"
    private const val TAG = "Lyricify"
    private val job = Job()
    private val scope = CoroutineScope(job)
    private var lastSentenceFromTime: Long = -1
    private var position: Long = 0
    private var lyric: Lyric? = null

    fun startLyric() {
        LogUtil.d(TAG, " ==> Start!...")
        println("Update lyric...")
        //scope.launch {
            val startTime = System.currentTimeMillis()
            val tmpPosition = position
            while (true) {
                position = tmpPosition + System.currentTimeMillis() - startTime
                if (!updateLyric()) break
            }
        //}
    }

    fun stopLyric() {
        job.cancel()
        StatusBarLyric(MyApp.context, null, SERVICE_NAME, false).stopLyric()
        LogUtil.d(TAG, " ==> Stop lyric...")
    }

    private fun updateLyric(): Boolean {
        LogUtil.d(TAG, " ==> Update lyric...")
        if (lyric == null) {
            LogUtil.d(TAG, " ==> lyric is null...")
            return false
        }
        val sentence = LyricUtils.getSentence(lyric, position)
        if (sentence == null) {
            LogUtil.d(TAG, " ==> sentence is null...")
            return false
        }
        LogUtil.d(TAG, " ==> yeah here...")
        if (sentence.fromTime != lastSentenceFromTime) {
            if (!TextUtils.isEmpty(sentence.content)) {
                StatusBarLyric(MyApp.context, null, SERVICE_NAME, false)
                    .updateLyric(sentence.content.replace("&apos;", "'"))
            }
            lastSentenceFromTime = sentence.fromTime
        }
        return true
    }

    fun setLrc(data: Media) {
        scope.launch {
            lyric = LrcGetter.getLyric(MyApp.context, data)
            LogUtil.d(TAG, " ==> Try to get lyric...")
        }
    }

    fun updatePosition(p: Long) {
        position = p
        LogUtil.d(TAG, " ==> Update lyric position...")
    }

}