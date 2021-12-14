package io.github.sukieva.statusBarLyricify.utils

import android.widget.Toast
import io.github.sukieva.statusBarLyricify.MyApp

fun String.toast(duration: Int = Toast.LENGTH_SHORT) =
    Toast.makeText(MyApp.context, this, duration).show()
