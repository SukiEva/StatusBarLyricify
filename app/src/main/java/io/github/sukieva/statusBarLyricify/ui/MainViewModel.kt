package io.github.sukieva.statusBarLyricify.ui

import StatusBarLyric.API.StatusBarLyric
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import io.github.sukieva.statusBarLyricify.MyApp


class MainViewModel : ViewModel() {
    private val statusBarLyric = StatusBarLyric(
        MyApp.context, null,
        "io.github.sukieva.statusBarLyricify.MusicListenerService",
        false
    )
    var isLyricEnabled = mutableStateOf(false)
    var dialogId = 1

    fun checkLyricEnabled() {
        isLyricEnabled.value = statusBarLyric.hasEnable()
    }

}