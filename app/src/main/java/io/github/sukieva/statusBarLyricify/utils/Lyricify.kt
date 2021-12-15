package io.github.sukieva.statusBarLyricify.utils

import StatusBarLyric.API.StatusBarLyric
import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.text.TextUtils
import cn.zhaiyifan.lyric.LyricUtils
import cn.zhaiyifan.lyric.model.Lyric
import io.github.sukieva.statusBarLyricify.MyApp
import io.github.sukieva.statusBarLyricify.data.Media


object Lyricify {
    private const val MSG_LYRIC_UPDATE_DONE = 2
    private const val SERVICE_NAME = "io.github.sukieva.statusBarLyricify.service.MusicListenerService"
    private const val TAG = "Lyricify"

    private var mLastSentenceFromTime: Long = -1
    private var mLyric: Lyric? = null

    var requiredLrcTitle: String? = null
    var isPlaying: Boolean = false
    var position: Long = 0
    var timeSentInMs: Long = 0

    @SuppressLint("StaticFieldLeak")
    private val statusBarLyric = StatusBarLyric(MyApp.context, null, SERVICE_NAME, false)

    fun startLyric() {
        LogUtil.d(TAG, " ==> Start lyric...")
        mLastSentenceFromTime = -1
        mHandler.post(mLyricUpdateRunnable)
    }

    fun stopLyric() {
        LogUtil.d(TAG, " ==> Stop lyric...")
        mHandler.removeCallbacks(mLyricUpdateRunnable)
        //StatusBarLyric(MyApp.context, null, SERVICE_NAME, false).stopLyric()
        statusBarLyric.stopLyric()
    }

    fun sendLyric(msg: String) {
        statusBarLyric.updateLyric(msg)
    }

    private fun updateLyric(pos: Long) {
        //LogUtil.d(TAG, " ==> Update lyric...")
        if (mLyric == null) return
        val sentence = LyricUtils.getSentence(mLyric, pos) ?: return
        if (sentence.fromTime != mLastSentenceFromTime) {
            if (!TextUtils.isEmpty(sentence.content)) {
                statusBarLyric.updateLyric(sentence.content.replace("&apos;", "'"))
            }
            mLastSentenceFromTime = sentence.fromTime
        }
    }

    fun setLrc(data: Media) {
        LrcUpdateThread(mHandler, data).start()
    }


    private val mHandler: Handler = object : Handler(Looper.myLooper()!!) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            if (msg.what == MSG_LYRIC_UPDATE_DONE && msg.data.getString("title", "") == requiredLrcTitle) {
                mLyric = if (msg.obj == null) {
                    null
                } else {
                    msg.obj as Lyric
                }
                if (mLyric == null) {
                    sendLyric("未找到歌词")
                    stopLyric()
                }
            }
        }
    }

    private val mLyricUpdateRunnable: Runnable = object : Runnable {
        override fun run() {
            if (!isPlaying) {
                stopLyric()
                return
            }
            updateLyric(position + System.currentTimeMillis() - timeSentInMs)
            mHandler.postDelayed(this, 250)
        }
    }

    private class LrcUpdateThread(private val handler: Handler?, private val data: Media) : Thread() {
        private val context: Context = MyApp.context
        override fun run() {
            if (handler == null) return
            val lrc = LrcGetter.getLyric(context, data)
            val message = Message()
            message.what = MSG_LYRIC_UPDATE_DONE
            message.obj = lrc
            val bundle = Bundle()
            bundle.putString("title", data.title)
            message.data = bundle
            handler.sendMessage(message)
        }
    }
}